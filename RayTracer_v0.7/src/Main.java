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

        scene.addLight(new DirectionalLight(
            new Vector3D(0.0, 0.0, -1.0),
            Color.WHITE,
            0.2
        ));

        scene.addLight(new DirectionalLight(
            new Vector3D(1.0, -1.0, -0.5),
            Color.WHITE,
            0.15
        ));


        scene.addLight(new PointLight(
            new Vector3D(5.0, 15.0, 25.0),
            Color.WHITE,
            800.0
        ));

        Raytracer raytracer = new Raytracer(scene, camera);
        raytracer.renderToFile("output_v07.png");
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

    