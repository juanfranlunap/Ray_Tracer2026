import java.awt.Color;


public class Sphere extends Object3D {

    public Vector3D center;
    public double   radius;

    double tNear = 0.001;
    double tFar  = 1000;

    public Sphere(Vector3D center, double radius, Color color) {
        super(color);
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Intersection intersect(Ray ray) {

        Vector3D L = center.subtract(ray.origin);
        double tca = L.dot(ray.direction);
        if (tca < 0) return new Intersection();
        double d2 = L.dot(L) - tca * tca;
        if (d2 > radius * radius) return new Intersection();
        double thc = Math.sqrt(radius * radius - d2);
        double t0 = tca - thc;
        double t1 = tca + thc;

        // CLIPPING: we only keep t values inside the near/far range
        if (t0 > tNear && t0 < tFar) return new Intersection(t0, this);
        if (t1 > tNear && t1 < tFar) return new Intersection(t1, this);

        // if the both t values are outside the valid range, no hit
        return new Intersection();
    }
}