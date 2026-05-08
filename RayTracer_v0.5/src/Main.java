import java.awt.Color;
import java.util.List;

// Ray Tracer v0.5
// We add phong shadeding and support for vertex normals from the .obj file

public class Main {

    public static void main(String[] args) throws Exception {

        Scene scene = new Scene();

        List<Triangle> objTriangles = ObjReader.read("1.obj", Color.GREEN);
        for (int i = 0; i < objTriangles.size(); i++) {
            scene.addObject(objTriangles.get(i));
        }

        Camera camera = new Camera(
            new Vector3D(0.0, 10.0, 40.0),
            800,
            600,
            60.0
        );

        Light light = new Light(
            new Vector3D(1.0, -0.5, 1.0),
            Color.WHITE,
            1.0
        );

        /* 
        Camera camera = new Camera(
            new Vector3D(0.5, 0.5, 4.0),  
            800,
            600,
            60.0
        );

        Light light = new Light(
            new Vector3D(1.0, 1.0, -1.0),  
            Color.WHITE,
            1.0
        );
        conf for cube obj */

        Raytracer raytracer = new Raytracer(scene, camera, light);
        raytracer.renderToFile("output_v0.5.png");
    }
}