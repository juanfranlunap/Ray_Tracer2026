import java.util.ArrayList;
import java.util.List;


public class Scene {
    private List<Object3D> objects;

    public Scene() {
        this.objects = new ArrayList<>();
    }


    public void addObject(Object3D obj) {
        objects.add(obj);
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