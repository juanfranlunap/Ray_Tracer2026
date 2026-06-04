// Axis-Aligned Bounding Box: a box where every side is parallel to one of the X/Y/Z axes.
// Used by the BVH to quickly reject rays before testing expensive triangle math.
public class AABB {

    public Vector3D min; // corner with the smallest x, y, z
    public Vector3D max; // corner with the largest  x, y, z

    public AABB(Vector3D min, Vector3D max) {
        this.min = min;
        this.max = max;
    }

    // Center of the box , used to decide which side of a split an object falls on
    public Vector3D centroid() {
        return new Vector3D(
            (min.x + max.x) / 2.0,
            (min.y + max.y) / 2.0,
            (min.z + max.z) / 2.0
        );
    }

    // Returns 0=X, 1=Y, 2=Z ,  the axis along which this box is longest.
    // Splitting along the longest axis gives the most balanced BVH.
    public int longestAxis() {
        double dx = max.x - min.x;
        double dy = max.y - min.y;
        double dz = max.z - min.z;
        if (dx >= dy && dx >= dz) return 0;
        if (dy >= dz)             return 1;
        return 2;
    }

    // Returns a new box that is just big enough to contain both a and b
    public static AABB surrounding(AABB a, AABB b) {
        return new AABB(
            new Vector3D(Math.min(a.min.x, b.min.x),
                         Math.min(a.min.y, b.min.y),
                         Math.min(a.min.z, b.min.z)),
            new Vector3D(Math.max(a.max.x, b.max.x),
                         Math.max(a.max.y, b.max.y),
                         Math.max(a.max.z, b.max.z))
        );
    }

// For each axis, we calculate where the ray enters and exits the box.
// If the intervals overlap on all three axes, the ray intersects the box.
// We also check that tmax > 0 to ensure the box is in front of the ray origin.

    public boolean hit(Ray ray) {
        double invX = 1.0 / ray.direction.x;
        double tx1  = (min.x - ray.origin.x) * invX;
        double tx2  = (max.x - ray.origin.x) * invX;
        double tmin = Math.min(tx1, tx2);
        double tmax = Math.max(tx1, tx2);

        double invY = 1.0 / ray.direction.y;
        double ty1  = (min.y - ray.origin.y) * invY;
        double ty2  = (max.y - ray.origin.y) * invY;
        tmin = Math.max(tmin, Math.min(ty1, ty2));
        tmax = Math.min(tmax, Math.max(ty1, ty2));

        double invZ = 1.0 / ray.direction.z;
        double tz1  = (min.z - ray.origin.z) * invZ;
        double tz2  = (max.z - ray.origin.z) * invZ;
        tmin = Math.max(tmin, Math.min(tz1, tz2));
        tmax = Math.min(tmax, Math.max(tz1, tz2));

        // The ray hits the box if the intervals overlap AND the box is in front of the ray
        return tmax >= tmin && tmax > 0;
    }
}
