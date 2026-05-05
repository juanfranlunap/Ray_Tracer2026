import java.awt.Color;
import java.util.List;

// Ray Tracer v0.3
// Creation of ObjReader that reads a .obj file and converts it into triangles 

public class Main {

    public static void main(String[] args) throws Exception {

        Scene scene = new Scene();

        List<Triangle> objTriangles = ObjReader.read("cube.obj", Color.GREEN);
        for (int i = 0; i < objTriangles.size(); i++) {
            scene.addObject(objTriangles.get(i));
        }


        Camera camera = new Camera(
            new Vector3D(1.0, 1.5, 3.0),  
            800,
            600,
            60.0
        );

        Light light = new Light(
            new Vector3D(1.0, 1.0, -1.0),  
            Color.WHITE,
            1.0
        );

        Raytracer raytracer = new Raytracer(scene, camera, light);
        raytracer.renderToFile("output_v0.4.png");
    }
}