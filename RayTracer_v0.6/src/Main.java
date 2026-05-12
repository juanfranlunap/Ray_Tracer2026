import java.awt.Color;
import java.util.List;

// Ray Tracer v0.6

//Light is now a base class with DirectionalLight and PointLight
//Scene holds a list of lights instead of one
//Raytracer sums the contribution of every light per pixel
//PointLight calculates L from the hit point to the light position

public class Main {

    public static void main(String[] args) throws Exception {

        Scene scene = new Scene();

        List<Triangle> objTriangles = ObjReader.read("cube.obj", Color.GREEN);
        for (int i = 0; i < objTriangles.size(); i++) {
            scene.addObject(objTriangles.get(i));
        }


        // directional light 1
        scene.addLight(new DirectionalLight(
            new Vector3D(1.0, -0.5, 1.0),
            Color.WHITE,
            1.1
        ));

        // directional light 2 
        scene.addLight(new DirectionalLight(
            new Vector3D(-1.0, 0.0, 1.0),
            Color.RED,
            0.5
        ));

        // point light 
        scene.addLight(new PointLight(
            new Vector3D(0.5, 3.0, 2.0), 
            Color.WHITE,
            0.9
        ));

        Camera camera = new Camera(
            new Vector3D(0.5, 0.5, 4.0),
            800,
            600,
            60.0
        );

        Raytracer raytracer = new Raytracer(scene, camera);
        raytracer.renderToFile("output_v06.png");
    }
}

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

    