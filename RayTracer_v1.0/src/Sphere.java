public class Sphere extends Object3D {

    public Vector3D center;
    public double   radius;

    public Sphere(Vector3D center, double radius, Material material) {
        super(material);
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Intersection intersect(Ray ray) {
        Vector3D L = center.subtract(ray.origin);
        double tca = L.dot(ray.direction);
        if (tca < 0) return Intersection.MISS;
        double d2 = L.dot(L) - tca * tca;
        if (d2 > radius * radius) return Intersection.MISS;
        double thc = Math.sqrt(radius * radius - d2);
        double t0 = tca - thc;
        double t1 = tca + thc;

        if (t0 > T_NEAR && t0 < T_FAR) return new Intersection(t0, this);
        if (t1 > T_NEAR && t1 < T_FAR) return new Intersection(t1, this);

        return Intersection.MISS;
    }

    @Override
    public AABB getAABB() {
        return new AABB(
            new Vector3D(center.x - radius, center.y - radius, center.z - radius),
            new Vector3D(center.x + radius, center.y + radius, center.z + radius)
        );
    }

    @Override
    public Vector3D getNormal(Vector3D hitPoint, double u, double v) {
        return hitPoint.subtract(center).normalize();
    }
}
