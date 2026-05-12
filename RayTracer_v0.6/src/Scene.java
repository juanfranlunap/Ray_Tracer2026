import java.util.ArrayList;
import java.util.List;


public class Scene {
    private List<Object3D> objects;
    private List<Light>    lights  = new ArrayList<>();

    public Scene() {
        this.objects = new ArrayList<>();
        this.lights = new ArrayList<>();
    }


    public void addObject(Object3D obj) {
        objects.add(obj);
    }

    public void addLight(Light light) {
        lights.add(light);
    }
 
    public List<Light> getLights() {
        return lights;
    }

    // Test the ray against all objects to return the nearest hit
    // Why: A ray can hit multiple objects, but we only render the closest one
    public Intersection intersect(Ray ray) {
        Intersection nearest = new Intersection(); 


        for (Object3D obj : objects) {
            Intersection current = obj.intersect(ray);
            if (current.hit && current.t < nearest.t) {
                nearest = current;
            }
        }

        return nearest;
    }
}