import java.awt.Color;


public class Triangle extends Object3D {

    public Vector3D v0, v1, v2;
    public Vector3D normal;

    double tNear   = 0.001;
    double tFar    = 1000;
    double epsilon = 1e-8; // a very small number to avoid division by zero

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, Color color) {
        super(color);
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;

        // V = v1 - v0
        // W = v0 - v2
        // N = Normalize(V x W)
        Vector3D V = v1.subtract(v0);
        Vector3D W = v0.subtract(v2);
        this.normal = V.cross(W).normalize();
    }

    // we use this in the raytracer to calculate the diffuse light
    public Vector3D getNormal() {
        return normal;
    }

    @Override
    public Intersection intersect(Ray ray) {

        Vector3D v2v0 = v2.subtract(v0);
        Vector3D v1v0 = v1.subtract(v0);

        // cross product of ray direction and edge v1v0
        Vector3D P = ray.direction.cross(v1v0);

        // if determinant is 0 the ray is parallel to the triangle, no hit
        double determinant = v2v0.dot(P);

        if (Math.abs(determinant) < epsilon) return new Intersection();

        double invDet = 1.0 / determinant;
        Vector3D T = ray.origin.subtract(v0);
        double u = invDet * T.dot(P);

        if (u < 0 || u > 1) return new Intersection(); // hit outside the triangle

        Vector3D Q = T.cross(v2v0);
        double v = invDet * ray.direction.dot(Q);
        if (v < 0 || (u + v) > (1.0 + epsilon)) return new Intersection();
        double t = invDet * Q.dot(v1v0);

        // clipping t to the valid range
        if (t < tNear || t > tFar) return new Intersection();

        // valid hit found
        return new Intersection(t, this);
    }
}