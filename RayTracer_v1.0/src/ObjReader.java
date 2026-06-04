import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// Reads a .obj file and returns a list of triangles normalized to a 1-unit bounding box.
//   v  = vertex position
//   vn = vertex normal
//   f  = face (vertex indices)

public class ObjReader {

    // Stores raw face data before vertices are normalized
    private static class FaceData {
        int vi0, vi1, vi2, vi3;   // vi3 = -1 if triangle, >= 0 if quad
        int ni0, ni1, ni2, ni3;
        int smoothingGroup;

        FaceData(int vi0, int vi1, int vi2, int vi3,
                 int ni0, int ni1, int ni2, int ni3,
                 int smoothingGroup) {
            this.vi0 = vi0; this.vi1 = vi1; this.vi2 = vi2; this.vi3 = vi3;
            this.ni0 = ni0; this.ni1 = ni1; this.ni2 = ni2; this.ni3 = ni3;
            this.smoothingGroup = smoothingGroup;
        }
    }

    public static List<Triangle> read(String filename, Material material) {
        return read(filename, material, 1.0, new Vector3D(0, 0, 0), 0.0);
    }

    public static List<Triangle> read(String filename, Material material, double scale, Vector3D offset) {
        return read(filename, material, scale, offset, 0.0);
    }

    public static List<Triangle> read(String filename, Material material, double scale, Vector3D offset, double rotationY) {

        List<Vector3D> vertices  = new ArrayList<>();
        List<Vector3D> normals   = new ArrayList<>();
        List<FaceData> faces     = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();
        int currentSmoothingGroup = 0;

        // Pass 1: read vertices, normals and face indices 
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 0 || parts[0].equals("#")) continue;

                if (parts[0].equals("v")) {
                    vertices.add(new Vector3D(
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3])
                    ));
                }

                if (parts[0].equals("vn")) {
                    normals.add(new Vector3D(
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3])
                    ));
                }

                if (parts[0].equals("s")) {
                    currentSmoothingGroup = parts[1].equals("off") || parts[1].equals("0")
                        ? 0 : Integer.parseInt(parts[1]);
                }

                if (parts[0].equals("f")) {
                    int vi0 = getVertexIndex(parts[1]);
                    int vi1 = getVertexIndex(parts[2]);
                    int vi2 = getVertexIndex(parts[3]);
                    int vi3 = (parts.length == 5) ? getVertexIndex(parts[4]) : -1;

                    int ni0 = getNormalIndex(parts[1]);
                    int ni1 = getNormalIndex(parts[2]);
                    int ni2 = getNormalIndex(parts[3]);
                    int ni3 = (parts.length == 5) ? getNormalIndex(parts[4]) : -1;

                    faces.add(new FaceData(vi0, vi1, vi2, vi3, ni0, ni1, ni2, ni3, currentSmoothingGroup));
                }
            }

            reader.close();

        } catch (Exception e) {
            System.out.println("Error reading file: " + filename);
        }

        // Normalize vertices: center at origin, longest axis = 1 unit
        if (!vertices.isEmpty()) {
            double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
            for (Vector3D v : vertices) {
                if (v.x < minX) minX = v.x; if (v.x > maxX) maxX = v.x;
                if (v.y < minY) minY = v.y; if (v.y > maxY) maxY = v.y;
                if (v.z < minZ) minZ = v.z; if (v.z > maxZ) maxZ = v.z;
            }
            double cx = (minX + maxX) / 2.0;
            double cy = (minY + maxY) / 2.0;
            double cz = (minZ + maxZ) / 2.0;
            double longest = Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
            double normScale = (longest > 0) ? 1.0 / longest : 1.0;
            for (int i = 0; i < vertices.size(); i++) {
                Vector3D v = vertices.get(i);
                vertices.set(i, new Vector3D((v.x - cx) * normScale, (v.y - cy) * normScale, (v.z - cz) * normScale));
            }
        }

        // Apply Y-axis rotation to normalized vertices
        if (rotationY != 0.0) {
            double rad = Math.toRadians(rotationY);
            double cosA = Math.cos(rad), sinA = Math.sin(rad);
            for (int i = 0; i < vertices.size(); i++) {
                Vector3D v = vertices.get(i);
                vertices.set(i, new Vector3D(v.x * cosA + v.z * sinA, v.y, -v.x * sinA + v.z * cosA));
            }
        }

        // Pass 2: build triangles from normalized and  transformed vertices
        for (FaceData f : faces) {
            Vector3D v0 = apply(vertices.get(f.vi0 - 1), scale, offset);
            Vector3D v1 = apply(vertices.get(f.vi1 - 1), scale, offset);
            Vector3D v2 = apply(vertices.get(f.vi2 - 1), scale, offset);

            Vector3D n0 = (f.ni0 > 0) ? normals.get(f.ni0 - 1) : null;
            Vector3D n1 = (f.ni1 > 0) ? normals.get(f.ni1 - 1) : null;
            Vector3D n2 = (f.ni2 > 0) ? normals.get(f.ni2 - 1) : null;

            triangles.add(new Triangle(v0, v1, v2, n0, n1, n2, f.smoothingGroup, material));

            // quad face: split into a second triangle
            if (f.vi3 >= 0) {
                Vector3D v3 = apply(vertices.get(f.vi3 - 1), scale, offset);
                Vector3D n3 = (f.ni3 > 0) ? normals.get(f.ni3 - 1) : null;
                triangles.add(new Triangle(v0, v2, v3, n0, n2, n3, f.smoothingGroup, material));
            }
        }

        System.out.println("Loaded " + triangles.size() + " triangles from " + filename);
        return triangles;
    }

    private static Vector3D apply(Vector3D v, double scale, Vector3D offset) {
        return new Vector3D(v.x * scale + offset.x, v.y * scale + offset.y, v.z * scale + offset.z);
    }

    private static int getVertexIndex(String token) {
        return Integer.parseInt(token.split("/")[0]);
    }

    private static int getNormalIndex(String token) {
        String[] parts = token.split("/");
        if (parts.length < 3 || parts[2].isEmpty()) return -1;
        return Integer.parseInt(parts[2]);
    }
}
