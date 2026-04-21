import java.awt.Color;


public class Sphere extends Object3D {
    public Vector3D center; 
    public double   radius; 

    public Sphere(Vector3D center, double radius, Color color) {
        super(color);
        this.center = center;
        this.radius = radius;
    }

    public Intersection intersectAnalytical(Ray ray) {
        // Vector from sphere center to ray origin
        // Why: We shift the problem so the sphere is relative to the ray
        Vector3D oc = ray.origin.subtract(this.center);

        double a = ray.direction.dot(ray.direction);           // D·D
        double b = 2.0 * oc.dot(ray.direction);               // 2*(OC·D)
        double c = oc.dot(oc) - (this.radius * this.radius);  // OC·OC - R²
        // Why: plugging the ray equation into the sphere equation so gives a quadratic

        double discriminant = b * b - 4 * a * c;              
        // Why: Tells us how many intersections exist

        if (discriminant < 0) {
            return new Intersection(); // Δ < 0 → no intersection
        }

        double sqrtDisc = Math.sqrt(discriminant);

        // It gives us two solutions from quadratic formula 
        // Why: The ray can enter and exit the sphere,  2 intersection points
        double t0 = (-b - sqrtDisc) / (2.0 * a); // closer intersection
        double t1 = (-b + sqrtDisc) / (2.0 * a); // farther intersection

        // +t= in front of camera, -t= behind
        // Why: We only render what the camera can see, when t is positive.
        if (t0 > 0) {
            return new Intersection(t0, this);
        }
        if (t1 > 0) {
            return new Intersection(t1, this);
        }

        return new Intersection(); // when both t are negative 
    }


    public Intersection intersectGeometric(Ray ray) {
        Vector3D L   = this.center.subtract(ray.origin); // L = C - O
        double   tca = L.dot(ray.direction);             // tca = L · D
        // Why: The projection of L onto ray direction , sowe look the closest approach to center

        // if tca < 0 , then the  sphere center is behind the ray, so there isnt collision
        // Why: Ray is pointing away from the sphere
        if (tca < 0) {
            return new Intersection();
        }

        // d² = L·L - tca²   (Pythagorean theorem)
        // Why: To reach the distance from sphere center to the ray
        double d2 = L.dot(L) - (tca * tca);

        if (d2 > this.radius * this.radius) {
            return new Intersection();
        }

        // thc = √(radius² - d²)
        // Why: The distance from closest point to intersection points
        double thc = Math.sqrt(this.radius * this.radius - d2);

        double t0 = tca - thc; // the entry point
        double t1 = tca + thc; // the exit point

        // Pick the nearest positive t 
        // Why: We choose the closest visible surface is what we render
        if (t0 > 0) {
            return new Intersection(t0, this);
        }
        if (t1 > 0) {
            return new Intersection(t1, this);
        }

        return new Intersection();
    }

    @Override
    public Intersection intersect(Ray ray) {
        return intersectGeometric(ray);
    }
}