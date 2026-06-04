public abstract class Object3D {
    public Material material;

    public static final double T_NEAR = 0.001;
    public static final double T_FAR  = 10000.0;

    public Object3D(Material material) {
        this.material = material;
    }

    public abstract Intersection intersect(Ray ray);

    // u, v are barycentric coordinates 
    public abstract Vector3D getNormal(Vector3D hitPoint, double u, double v);

    // Returns the axis-aligned bounding box that fully encloses this object
    public abstract AABB getAABB();
}
