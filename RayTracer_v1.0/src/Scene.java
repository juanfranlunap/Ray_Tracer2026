import java.util.ArrayList;
import java.util.List;


public class Scene {
    private List<Object3D> objects;
    private List<Light>    lights  = new ArrayList<>();
    private BVHNode        bvhRoot = null; 

    public Scene() {
        this.objects = new ArrayList<>();
        this.lights = new ArrayList<>();
    }

    public void buildBVH() {
        System.out.println("Building BVH for " + objects.size() + " objects...");
        bvhRoot = new BVHNode(objects);
        System.out.println("BVH ready.");
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
    public Intersection intersect(Ray ray) {
        if (bvhRoot != null) return bvhRoot.intersect(ray);

        // Brute-force fallback used in rare cases
        Intersection nearest = Intersection.MISS;
        for (Object3D obj : objects) {
            Intersection current = obj.intersect(ray);
            if (current.hit && current.t < nearest.t) nearest = current;
        }
        return nearest;
    }

    // For shadow rays: exits at the first blocker within maxDist
    public boolean intersectAny(Ray ray, double maxDist) {
        if (bvhRoot != null) return bvhRoot.intersectAny(ray, maxDist);

        for (Object3D obj : objects) {
            Intersection hit = obj.intersect(ray);
            if (hit.hit && hit.t < maxDist) return true;
        }
        return false;
    }
}