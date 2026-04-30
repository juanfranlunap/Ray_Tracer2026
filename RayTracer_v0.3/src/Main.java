import java.awt.Color;
import java.util.List;

// Ray Tracer v0.3
// Creation of ObjReader that reads a .obj file and converts it into triangles 

public class Main {

    public static void main(String[] args) throws Exception {

        Scene scene = new Scene();

        List<Triangle> objTriangles = ObjReader.read("1.obj", Color.GREEN);
        for (int i = 0; i < objTriangles.size(); i++) {
            scene.addObject(objTriangles.get(i));
        }


        Camera camera = new Camera(
            new Vector3D(0.0, 10.0, 30.0),
            800,
            600,
            60.0
        );

        Raytracer raytracer = new Raytracer(scene, camera);
        raytracer.renderToFile("output_v03.png");
    }
}