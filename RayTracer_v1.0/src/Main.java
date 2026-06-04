
// Scene  outputchess
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class Main {

    // Rota un vector alrededor del eje Y
    static Vector3D rotYVec(Vector3D v, double cos, double sin) {
        return new Vector3D(v.x * cos + v.z * sin, v.y, -v.x * sin + v.z * cos);
    }

    static List<Triangle> rotateY(List<Triangle> tris, double angleDeg) {
        double rad = Math.toRadians(angleDeg);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        List<Triangle> result = new ArrayList<>();
        for (Triangle t : tris) {
            Vector3D rv0 = rotYVec(t.v0, cos, sin);
            Vector3D rv1 = rotYVec(t.v1, cos, sin);
            Vector3D rv2 = rotYVec(t.v2, cos, sin);
            Vector3D rn0 = t.n0 != null ? rotYVec(t.n0, cos, sin) : null;
            Vector3D rn1 = t.n1 != null ? rotYVec(t.n1, cos, sin) : null;
            Vector3D rn2 = t.n2 != null ? rotYVec(t.n2, cos, sin) : null;
            result.add(new Triangle(rv0, rv1, rv2, rn0, rn1, rn2, t.smoothingGroup, t.material));
        }
        return result;
    }

    static Vector3D rotXVec(Vector3D v, double cos, double sin) {
        return new Vector3D(v.x, v.y * cos - v.z * sin, v.y * sin + v.z * cos);
    }
        // Rota un vector alrededor del ejeX
    static List<Triangle> rotateX(List<Triangle> tris, double angleDeg) {
        double rad = Math.toRadians(angleDeg);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        List<Triangle> result = new ArrayList<>();
        for (Triangle t : tris) {
            Vector3D rv0 = rotXVec(t.v0, cos, sin);
            Vector3D rv1 = rotXVec(t.v1, cos, sin);
            Vector3D rv2 = rotXVec(t.v2, cos, sin);
            Vector3D rn0 = t.n0 != null ? rotXVec(t.n0, cos, sin) : null;
            Vector3D rn1 = t.n1 != null ? rotXVec(t.n1, cos, sin) : null;
            Vector3D rn2 = t.n2 != null ? rotXVec(t.n2, cos, sin) : null;
            result.add(new Triangle(rv0, rv1, rv2, rn0, rn1, rn2, t.smoothingGroup, t.material));
        }
        return result;
    }

    public static void printBounds(String name, List<Triangle> triangles) {
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        for (Triangle t : triangles) {
            Vector3D[] verts = {t.v0, t.v1, t.v2};
            for (Vector3D v : verts) {
                if (v.x < minX) minX = v.x;  if (v.x > maxX) maxX = v.x;
                if (v.y < minY) minY = v.y;  if (v.y > maxY) maxY = v.y;
                if (v.z < minZ) minZ = v.z;  if (v.z > maxZ) maxZ = v.z;
            }
        }
        System.out.println("=== " + name + " ===");
        System.out.println("  X: " + minX + " to " + maxX + "  (centro: " + ((minX+maxX)/2) + ")");
        System.out.println("  Y: " + minY + " to " + maxY + "  (centro: " + ((minY+maxY)/2) + ")");
        System.out.println("  Z: " + minZ + " to " + maxZ + "  (centro: " + ((minZ+maxZ)/2) + ")");
    }

    public static void main(String[] args) throws Exception {

        Scene scene = new Scene();

        Material matGlass = new Material(
            new Color(200, 230, 255),  // azul muy claro
            0.15, 0.05, 0.95, 128.0,
            0.2,   // poca reflexion
            0.85,  // mucha transparencia
            1.5    // ior vidrio
        );

        Material matWater = new Material(
            new Color(15, 30, 60),
            0.05, 0.2, 0.7, 64.0,
            0.5,   // muy reflectivo
            0.0, 1.33
        );

        Material matWhite = new Material(
            new Color(220, 235, 255),
            0.05, 0.3, 0.8, 64.0,
            0.5,   // reflectivo
            0.0, 1.0
        );

        List<Triangle> queen  = ObjReader.read("queen.obj",  matGlass, 5.0, new Vector3D(-3.0, 1.5, 2.0));
        List<Triangle> king   = ObjReader.read("king.obj",   matGlass, 5.0, new Vector3D( 0.0, 1.5, 2.0));
        List<Triangle> knight = ObjReader.read("knight.obj", matGlass, 5.0, new Vector3D(-2.5, -1.0, 1.0));
        queen  = rotateX(queen,  -90.0);
        king   = rotateX(king,   -90.0);
        knight = rotateX(knight, -90.0);
        knight = rotateY(knight, 180.0);  
       

        printBounds("queen",  queen);
        printBounds("king",   king);
        printBounds("knight", knight);

        // Chess board
        for (int row = -5; row < 5; row++) {
            for (int col = -5; col < 5; col++) {
                Material mat = ((row + col) % 2 == 0) ? matWater : matWhite;
                float x0 = col * 2.0f;
                float x1 = x0 + 2.0f;
                float z0 = row * 2.0f;
                float z1 = z0 + 2.0f;
                scene.addObject(new Triangle(
                    new Vector3D(x0, 0, z0),
                    new Vector3D(x1, 0, z0),
                    new Vector3D(x1, 0, z1),
                    mat
                ));
                scene.addObject(new Triangle(
                    new Vector3D(x0, 0, z0),
                    new Vector3D(x1, 0, z1),
                    new Vector3D(x0, 0, z1),
                    mat
                ));
            }
        }

        
        for (Triangle t : queen)  scene.addObject(t);
        for (Triangle t : king)   scene.addObject(t);
        for (Triangle t : knight) scene.addObject(t);

        Camera camera = new Camera(new Vector3D(0.0, 4.0, 9.0), 65.0);

        scene.addLight(new DirectionalLight(
            new Vector3D(0.0, -1.0, -0.3),
            Color.WHITE,
            2.0  
        ));

        scene.addLight(new PointLight(
            new Vector3D(2.0, 12.0, 5.0),
            Color.WHITE,
            5000.0  
        ));

        scene.addLight(new PointLight(
            new Vector3D(-5.0, 6.0, 4.0),
            new Color(150, 180, 255),
            2000.0  
        ));

        scene.addLight(new PointLight(
            new Vector3D(0.0, 8.0, -5.0),
            Color.WHITE,
            2000.0
        ));
        scene.addLight(new PointLight(
            new Vector3D(0.0, -1.0, 2.0),  
            Color.WHITE,
            2000.0
        ));
        scene.buildBVH();

        Raytracer raytracer = new Raytracer(scene, camera, 4096, 2160);
        raytracer.renderToFile("output_chess.png");
    }
}

// Scene 2 output_scene2
 /* 
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class Main {

    static Vector3D rotZVec(Vector3D v, double cos, double sin) {
        return new Vector3D(v.x * cos - v.y * sin, v.x * sin + v.y * cos, v.z);
    }

    static List<Triangle> rotateZ(List<Triangle> tris, double angleDeg) {
        double rad = Math.toRadians(angleDeg);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        List<Triangle> result = new ArrayList<>();
        for (Triangle t : tris) {
            Vector3D rv0 = rotZVec(t.v0, cos, sin);
            Vector3D rv1 = rotZVec(t.v1, cos, sin);
            Vector3D rv2 = rotZVec(t.v2, cos, sin);
            Vector3D rn0 = t.n0 != null ? rotZVec(t.n0, cos, sin) : null;
            Vector3D rn1 = t.n1 != null ? rotZVec(t.n1, cos, sin) : null;
            Vector3D rn2 = t.n2 != null ? rotZVec(t.n2, cos, sin) : null;
            result.add(new Triangle(rv0, rv1, rv2, rn0, rn1, rn2, t.smoothingGroup, t.material));
        }
        return result;
    }

    static Vector3D rotXVec(Vector3D v, double cos, double sin) {
        return new Vector3D(v.x, v.y * cos - v.z * sin, v.y * sin + v.z * cos);
    }

    static List<Triangle> rotateX(List<Triangle> tris, double angleDeg) {
        double rad = Math.toRadians(angleDeg);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        List<Triangle> result = new ArrayList<>();
        for (Triangle t : tris) {
            Vector3D rv0 = rotXVec(t.v0, cos, sin);
            Vector3D rv1 = rotXVec(t.v1, cos, sin);
            Vector3D rv2 = rotXVec(t.v2, cos, sin);
            // Rotar tambien las normales por vertice (para que el smooth shading sea correcto)
            Vector3D rn0 = t.n0 != null ? rotXVec(t.n0, cos, sin) : null;
            Vector3D rn1 = t.n1 != null ? rotXVec(t.n1, cos, sin) : null;
            Vector3D rn2 = t.n2 != null ? rotXVec(t.n2, cos, sin) : null;
            result.add(new Triangle(rv0, rv1, rv2, rn0, rn1, rn2, t.smoothingGroup, t.material));
        }
        return result;
    }

    static Vector3D rotYVec(Vector3D v, double cos, double sin) {
        return new Vector3D(v.x * cos + v.z * sin, v.y, -v.x * sin + v.z * cos);
    }

    static List<Triangle> rotateY(List<Triangle> tris, double angleDeg) {
        double rad = Math.toRadians(angleDeg);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        List<Triangle> result = new ArrayList<>();
        for (Triangle t : tris) {
            Vector3D rv0 = rotYVec(t.v0, cos, sin);
            Vector3D rv1 = rotYVec(t.v1, cos, sin);
            Vector3D rv2 = rotYVec(t.v2, cos, sin);
            Vector3D rn0 = t.n0 != null ? rotYVec(t.n0, cos, sin) : null;
            Vector3D rn1 = t.n1 != null ? rotYVec(t.n1, cos, sin) : null;
            Vector3D rn2 = t.n2 != null ? rotYVec(t.n2, cos, sin) : null;
            result.add(new Triangle(rv0, rv1, rv2, rn0, rn1, rn2, t.smoothingGroup, t.material));
        }
        return result;
    }

    // Bounding box helper 
    public static void printBounds(String name, List<Triangle> triangles) {
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

        for (Triangle t : triangles) {
            Vector3D[] verts = {t.v0, t.v1, t.v2};
            for (Vector3D v : verts) {
                if (v.x < minX) minX = v.x;  if (v.x > maxX) maxX = v.x;
                if (v.y < minY) minY = v.y;  if (v.y > maxY) maxY = v.y;
                if (v.z < minZ) minZ = v.z;  if (v.z > maxZ) maxZ = v.z;
            }
        }

        double cx = (minX + maxX) / 2;
        double cy = (minY + maxY) / 2;
        double cz = (minZ + maxZ) / 2;

        System.out.println("=== " + name + " ===");
        System.out.println("  X: " + minX + " to " + maxX + "  (centro: " + cx + ")");
        System.out.println("  Y: " + minY + " to " + maxY + "  (centro: " + cy + ")");
        System.out.println("  Z: " + minZ + " to " + maxZ + "  (centro: " + cz + ")");
        System.out.println("  Tamanio: " + (maxX-minX) + " x " + (maxY-minY) + " x " + (maxZ-minZ));
    }

    public static void main(String[] args) throws Exception {

        Scene scene = new Scene();

        Material matSuelo = new Material(
            new Color(70, 70, 70),
            0.08, 0.6, 0.08, 6.0,
            0.15,  // reflectivity suave como concreto mojado
            0.0, 1.0
        );

        Material matRampa = new Material(
            new Color(130, 90, 60),
            0.08, 0.65, 0.18, 28.0,
            0.01, 0.0, 1.0
        );

        Material matPatineta = new Material(
            new Color(40, 20, 120),
            0.05, 0.65, 0.3, 48.0,
            0.02, 0.0, 1.0
        );

        Material matSpray = new Material(
            new Color(200, 200, 200),
            0.04, 0.35, 0.6, 64.0,
            0.10, 0.0, 1.0
        );

        List<Triangle> suelo = ObjReader.read("suelo.obj", matSuelo, 60.0, new Vector3D(0.0, 0.0, 0.0));

        List<Triangle> rampa = ObjReader.read("rampa.obj", matRampa, 7.0, new Vector3D(-5.5, -2.0, 4.0));
        rampa = rotateX(rampa, -90.0);
        rampa = rotateY(rampa, 65.0);
        List<Triangle> patineta = ObjReader.read("patineta.obj", matPatineta, 4.0, new Vector3D(0.0, 8.2, 4.0));
        patineta = rotateZ(patineta, 55.0);
        patineta = rotateX(patineta, 15.0);
        patineta = rotateY(patineta, 50.0);
        List<Triangle> spray = ObjReader.read("sprycan.obj", matSpray, 1.5, new Vector3D(4.0, 3.0, 9.0));

        printBounds("suelo",    suelo);
        printBounds("rampa",    rampa);
        printBounds("patineta", patineta);
        printBounds("spray",    spray);

        for (Triangle t : suelo)    scene.addObject(t);
        for (Triangle t : rampa)    scene.addObject(t);
        for (Triangle t : patineta) scene.addObject(t);
        for (Triangle t : spray)    scene.addObject(t);

        Camera camera = new Camera(new Vector3D(1.0, 5.5, 16.5), 65.0);

        scene.addLight(new DirectionalLight(
            new Vector3D(0.0, -1.0, -0.5),
            Color.WHITE,
            0.4
        ));

        scene.addLight(new PointLight(
            new Vector3D(-4.0, 12.0, 6.0),
            new Color(255, 240, 220),
            200.0
        ));

        scene.addLight(new PointLight(
            new Vector3D(5.0, 8.0, 10.0),
            new Color(200, 220, 255),
            100.0
        ));
        scene.buildBVH();

        Raytracer raytracer = new Raytracer(scene, camera, 4096, 2160);
        raytracer.renderToFile("output_scene2.png");
    }
}
*/

// Scene  output_yahuarcocha
/*
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class Main {

    static Vector3D rotXVec(Vector3D v, double cos, double sin) {
        return new Vector3D(v.x, v.y * cos - v.z * sin, v.y * sin + v.z * cos);
    }

    static List<Triangle> rotateX(List<Triangle> tris, double angleDeg) {
        double rad = Math.toRadians(angleDeg);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        List<Triangle> result = new ArrayList<>();
        for (Triangle t : tris) {
            Vector3D rv0 = rotXVec(t.v0, cos, sin);
            Vector3D rv1 = rotXVec(t.v1, cos, sin);
            Vector3D rv2 = rotXVec(t.v2, cos, sin);
            // Rotar tambien las normales por vertice (para que el smooth shading sea correcto)
            Vector3D rn0 = t.n0 != null ? rotXVec(t.n0, cos, sin) : null;
            Vector3D rn1 = t.n1 != null ? rotXVec(t.n1, cos, sin) : null;
            Vector3D rn2 = t.n2 != null ? rotXVec(t.n2, cos, sin) : null;
            result.add(new Triangle(rv0, rv1, rv2, rn0, rn1, rn2, t.smoothingGroup, t.material));
        }
        return result;
    }

    // Bounding box helper
    public static void printBounds(String name, List<Triangle> triangles) {
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

        for (Triangle t : triangles) {
            Vector3D[] verts = {t.v0, t.v1, t.v2};
            for (Vector3D v : verts) {
                if (v.x < minX) minX = v.x;  if (v.x > maxX) maxX = v.x;
                if (v.y < minY) minY = v.y;  if (v.y > maxY) maxY = v.y;
                if (v.z < minZ) minZ = v.z;  if (v.z > maxZ) maxZ = v.z;
            }
        }

        double cx = (minX + maxX) / 2;
        double cy = (minY + maxY) / 2;
        double cz = (minZ + maxZ) / 2;

        System.out.println("=== " + name + " ===");
        System.out.println("  X: " + minX + " to " + maxX + "  (centro: " + cx + ")");
        System.out.println("  Y: " + minY + " to " + maxY + "  (centro: " + cy + ")");
        System.out.println("  Z: " + minZ + " to " + maxZ + "  (centro: " + cz + ")");
        System.out.println("  Tamanio: " + (maxX-minX) + " x " + (maxY-minY) + " x " + (maxZ-minZ));
    }

    public static void main(String[] args) throws Exception {

        Scene scene = new Scene();

        Material matInca = new Material(
            new Color(139, 90, 43),
            0.1, 0.7, 0.3, 32.0,
            0.0, 0.0, 1.0
        );

        Material matSkull = new Material(
            new Color(210, 200, 170),
            0.1, 0.8, 0.05, 8.0,
            0.0, 0.0, 1.0
        );

        Material matBlood = new Material(
            new Color(80,5,5),
            0.05, 0.4, 0.6, 64.0,
            0.5, 0.0, 1.0
        );

        // All the models are nomalized to fit in a unit cube centered at the origin (X,Y,Z in [-0.5, 0.5])

        List<Triangle> inca  = ObjReader.read("inca.obj",  matInca,  7.0, new Vector3D( 0.0,  3.5,  0.0), -90.0);
        // Fondo rectangular - montaña creada con triángulos
        // Ancho: X ±25, Alto: Y [0, 40], Profundidad: Z = -15
        List<Triangle> skull = ObjReader.read("skulls.obj", matSkull, 8.0, new Vector3D(0.0, 0.5, 2.0),-90);
        printBounds("inca",  inca);
        printBounds("skull", skull);

        Triangle blood1 = new Triangle(
            new Vector3D(-10, 0,  10),
            new Vector3D( 10, 0,  10),
            new Vector3D( 10, 0, -10),
            matBlood
        );
        Triangle blood2 = new Triangle(
            new Vector3D(-10, 0,  10),
            new Vector3D( 10, 0, -10),
            new Vector3D(-10, 0, -10),
            matBlood
        );
        scene.addObject(blood1);
        scene.addObject(blood2);
        for (Triangle t : inca)  scene.addObject(t);
        for (Triangle t : skull) scene.addObject(t);

        Camera camera = new Camera(new Vector3D(0.0, 3.0, 8.0), 75.0);

        scene.addLight(new DirectionalLight(
            new Vector3D(0.0, -1.0, -0.5),
            Color.WHITE,
            0.2
        ));

        scene.addLight(new PointLight(
            new Vector3D(3.0, 12.0, 8.0),
            new Color(255, 200, 120),
            800.0
        ));

        scene.addLight(new PointLight(
            new Vector3D(-5.0, 5.0, 8.0),
            new Color(100, 150, 255),
            20.0
        ));

        scene.buildBVH();

        Raytracer raytracer = new Raytracer(scene, camera, 4096, 2160);
        raytracer.renderToFile("output_yahuarcocha.png");
    }
}

*/