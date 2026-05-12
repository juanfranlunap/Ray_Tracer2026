import java.awt.Color;


public class Triangle extends Object3D {
    public Vector3D flatNormal;
    public Vector3D n0, n1, n2;
    public Vector3D v0, v1, v2;
    public int smoothingGroup;


    double tNear   = 0.001;
    double tFar    = 1000;
    double epsilon = 1e-8; // a very small number to avoid division by zero

    
    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2,
                    Vector3D n0, Vector3D n1, Vector3D n2, int smoothingGroup, Color color) {
        super(color);
        this.v0 = v0; this.v1 = v1; this.v2 = v2;
        this.n0 = n0; this.n1 = n1; this.n2 = n2;
        this.smoothingGroup = smoothingGroup;
        Vector3D V = v1.subtract(v0);
        Vector3D W = v0.subtract(v2);
        this.flatNormal = V.cross(W).normalize();
    }
 
    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, Color color) {
        this(v0, v1, v2, null, null, null, 0, color);
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

        // w = 1 - u - v  
        // N = w*n0 + u*n1 + v*n2  then normalize
    public Vector3D getPhongNormal(double u, double v) {
        if (smoothingGroup == 0) return flatNormal;
        // if we have no vertex normals from the obj, use the flat normal
        if (n0 == null || n1 == null || n2 == null) return flatNormal;
 
        // w is the weight of v0 - the part that is not u or v
        double w = 1.0 - u - v;
 
        // we interpolate each component of the normal separately
        double nx = w * n0.x + u * n1.x + v * n2.x;
        double ny = w * n0.y + u * n1.y + v * n2.y;
        double nz = w * n0.z + u * n1.z + v * n2.z;
 
        // we normalize so the length is 1 - required for correct lighting math
        return new Vector3D(nx, ny, nz).normalize();
    }
 
    public Vector3D getNormal() {
        return flatNormal;
    }
}
