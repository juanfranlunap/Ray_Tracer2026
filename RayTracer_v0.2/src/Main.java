import java.awt.Color;

// Ray Tracer v0.2
// Use of  clipping, near and far
// Triangle with Moller-Trumbore 

public class Main {

    public static void main(String[] args) throws Exception {

        Scene scene = new Scene();

        // red sphere 
        scene.addObject(new Sphere(
            new Vector3D(-1.0, 0.0, -5.0),
            1.0,
            Color.RED
        ));

        // blue sphere 
        scene.addObject(new Sphere(
            new Vector3D(1.5, 0.0, -5.0),
            0.6,
            Color.BLUE
        ));

        // green triangle 
        // v0 is the top vertex, v1 is bottom left, v2 is bottom right
        scene.addObject(new Triangle(
            new Vector3D( 0.0,  2.0, -6.0),  // v0
            new Vector3D(-3.0, -1.5, -4.0),  // v1
            new Vector3D( 3.0, -1.5, -4.0),  // v2
            Color.GREEN
        ));

        // camera at the origin looking into the scene
        Camera camera = new Camera(
            new Vector3D(0.0, 0.0, 0.0),
            800,
            600,
            60.0
        );

        Raytracer raytracer = new Raytracer(scene, camera);
        raytracer.renderToFile("output_v02.png");
    }
}