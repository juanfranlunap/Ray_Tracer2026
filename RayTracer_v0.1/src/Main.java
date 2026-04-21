import java.awt.Color;


public class Main {
    public static void main(String[] args) throws Exception {

        // Scene
        Scene scene = new Scene();

        // Red sphere  
        scene.addObject(new Sphere(
            new Vector3D(-1.0, 0.0, -15.0),  
            1.0,                             
            Color.RED
        ));

        // Blue sphere  
        scene.addObject(new Sphere(
            new Vector3D(1.5, 0.0, -5.0),   
            0.6,                             
            Color.BLUE
        ));

        //Camera  O + tD 
        // Camera is at the origin looking down -Z
        Camera camera = new Camera(
            new Vector3D(0.0, 0.0, 0.0), // position O
            800,                          // image width
            600,                          // image height
            60.0                          // field of view (in degrees)
        );

        // ── Raytracer ─────────────────────────────────────────
        Raytracer raytracer = new Raytracer(scene, camera);
        raytracer.renderToFile("output.png");

        System.out.println("Done!");
    }
}



