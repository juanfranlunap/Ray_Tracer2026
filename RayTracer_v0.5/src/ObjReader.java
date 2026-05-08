import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// reads a .obj file and returns a list of triangles 
//   v  = vertex position x y z
//   f  = face defined by vertex indices
//  vn = vertex normal x y z


public class ObjReader {

    public static List<Triangle> read(String filename, Color color) {

        List<Vector3D> vertices  = new ArrayList<>();
        List<Vector3D> normals   = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;

            while ((line = reader.readLine()) != null) {

                // We split the line by spaces to read each part
                String[] parts = line.trim().split("\\s+");

                if (parts.length == 0 || parts[0].equals("#")) continue;

                // v line = vertex position
                if (parts[0].equals("v")) {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    vertices.add(new Vector3D(x, y, z));
                }

                if (parts[0].equals("vn")) {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    normals.add(new Vector3D(x, y, z));
                }

                // we only need the first number before the /
                if (parts[0].equals("f")) {

                    // vertex positions 
                    int vi0 = getVertexIndex(parts[1]);
                    int vi1 = getVertexIndex(parts[2]);
                    int vi2 = getVertexIndex(parts[3]);

                    Vector3D v0 = vertices.get(vi0 - 1);
                    Vector3D v1 = vertices.get(vi1 - 1);
                    Vector3D v2 = vertices.get(vi2 - 1);

                    // if the file has no normals we pass null
                    // and Triangle will fall back to the V x W normal
                    Vector3D n0 = null, n1 = null, n2 = null;

                    if (normals.size() > 0) {
                        int ni0 = getNormalIndex(parts[1]);
                        int ni1 = getNormalIndex(parts[2]);
                        int ni2 = getNormalIndex(parts[3]);

                        if (ni0 > 0) n0 = normals.get(ni0 - 1);
                        if (ni1 > 0) n1 = normals.get(ni1 - 1);
                        if (ni2 > 0) n2 = normals.get(ni2 - 1);
                    }

                    triangles.add(new Triangle(v0, v1, v2, n0, n1, n2, color));

                    // if the face has 4 vertices (quad) we split it into 2 triangles
                    if (parts.length == 5) {
                        int vi3 = getVertexIndex(parts[4]);
                        Vector3D v3 = vertices.get(vi3 - 1);

                        Vector3D n3 = null;
                        if (normals.size() > 0) {
                            int ni3 = getNormalIndex(parts[4]);
                            if (ni3 > 0) n3 = normals.get(ni3 - 1);
                        }

                        triangles.add(new Triangle(v0, v2, v3, n0, n2, n3, color));
                    }
                }
            }

            reader.close();

        } catch (Exception e) {
            System.out.println("error reading file: " + filename);
        }

        System.out.println("loaded " + triangles.size() + " triangles from " + filename);
        return triangles;
    }

    // first number in "3/1/2" = vertex index
    private static int getVertexIndex(String token) {
        return Integer.parseInt(token.split("/")[0]);
    }
    // third number in "3/1/2" = normal index
    private static int getNormalIndex(String token) {
        String[] parts = token.split("/");
        if (parts.length < 3 || parts[2].isEmpty()) return -1;
        return Integer.parseInt(parts[2]);
    }
}