import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// reads a .obj file and returns a list of triangles ready for the scene
//   v  = vertex position x y z
//   f  = face defined by vertex indices


public class ObjReader {

    public static List<Triangle> read(String filename, Color color) {
        List<Vector3D> vertices = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;

            while ((line = reader.readLine()) != null) {

                // We split the line by spaces to read each part
                String[] parts = line.trim().split("\\s+");

                if (parts.length == 0 || parts[0].equals("#")) continue;

                if (parts[0].equals("v")) {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    vertices.add(new Vector3D(x, y, z));
                }

                // we only need the first number before the /
                if (parts[0].equals("f")) {

                    int i0 = getIndex(parts[1]);
                    int i1 = getIndex(parts[2]);
                    int i2 = getIndex(parts[3]);

                    Vector3D v0 = vertices.get(i0 - 1);
                    Vector3D v1 = vertices.get(i1 - 1);
                    Vector3D v2 = vertices.get(i2 - 1);

                    triangles.add(new Triangle(v0, v1, v2, color));

                    // if the face has 4 vertices (quad) we split it into 2 triangles
                    if (parts.length == 5) {
                        int i3 = getIndex(parts[4]);
                        Vector3D v3 = vertices.get(i3 - 1);
                        triangles.add(new Triangle(v0, v2, v3, color));
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

    // Extraction of the  vertex index from tokens like "3/1/2" or "3//2" or just "3"
    // Only the firsrt number is useful
    private static int getIndex(String token) {
        return Integer.parseInt(token.split("/")[0]);
    }
}