public class Triangle extends Object3D {
    public Vector3D flatNormal;
    public Vector3D n0, n1, n2;
    public Vector3D v0, v1, v2;
    public int smoothingGroup;

    private final Vector3D edge1; // v1 - v0
    private final Vector3D edge2; // v2 - v0

    private static final double EPSILON = 1e-8;

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2,
                    Vector3D n0, Vector3D n1, Vector3D n2, int smoothingGroup, Material material) {
        super(material);
        this.v0 = v0; this.v1 = v1; this.v2 = v2;
        this.n0 = n0; this.n1 = n1; this.n2 = n2;
        this.smoothingGroup = smoothingGroup;
        this.edge1 = v1.subtract(v0);
        this.edge2 = v2.subtract(v0);
        this.flatNormal = edge1.cross(edge2).normalize();
    }

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2, Material material) {
        this(v0, v1, v2, null, null, null, 0, material);
    }

    @Override
    public Intersection intersect(Ray ray) {
        Vector3D P = ray.direction.cross(edge1);
        double determinant = edge2.dot(P);

        if (Math.abs(determinant) < EPSILON) return Intersection.MISS;

        double invDet = 1.0 / determinant;
        Vector3D T = ray.origin.subtract(v0);
        double u = invDet * T.dot(P);
        if (u < 0 || u > 1) return Intersection.MISS;

        Vector3D Q = T.cross(edge2);
        double v = invDet * ray.direction.dot(Q);
        if (v < 0 || (u + v) > (1.0 + EPSILON)) return Intersection.MISS;

        double t = invDet * Q.dot(edge1);
        if (t < T_NEAR || t > T_FAR) return Intersection.MISS;

        return new Intersection(t, this, u, v);
    }

    // Small padding prevents zero-thickness boxes for axis-aligned triangles
    @Override
    public AABB getAABB() {
        double eps = 1e-4;
        return new AABB(
            new Vector3D(Math.min(v0.x, Math.min(v1.x, v2.x)) - eps,
                         Math.min(v0.y, Math.min(v1.y, v2.y)) - eps,
                         Math.min(v0.z, Math.min(v1.z, v2.z)) - eps),
            new Vector3D(Math.max(v0.x, Math.max(v1.x, v2.x)) + eps,
                         Math.max(v0.y, Math.max(v1.y, v2.y)) + eps,
                         Math.max(v0.z, Math.max(v1.z, v2.z)) + eps)
        );
    }

    // Phong normal interpolation: N = (1-u-v)*n0 + u*n1 + v*n2
    @Override
    public Vector3D getNormal(Vector3D hitPoint, double u, double v) {
        if (smoothingGroup == 0 || n0 == null || n1 == null || n2 == null) return flatNormal;
        double w  = 1.0 - u - v;
        double nx = w * n0.x + u * n1.x + v * n2.x;
        double ny = w * n0.y + u * n1.y + v * n2.y;
        double nz = w * n0.z + u * n1.z + v * n2.z;
        return new Vector3D(nx, ny, nz).normalize();
    }
}
