import java.util.List;
import java.util.ArrayList;

// A node of the Bounding Volume Hierarchy (BVH).
//
// Each node stores a bounding box that covers all objects below it.
// Leaf nodes store a small list of objects and test them directly.
// Internal nodes store two children and only continue traversal if
// the ray hits the child's bounding box first.
//
// This helps skip large parts of the scene without testing every object.
public class BVHNode {

    private AABB         box;         // bounding box of everything in this subtree
    private BVHNode      left;        // null when this is a leaf
    private BVHNode      right;       // null when this is a leaf
    private List<Object3D> leafObjects; // only filled for leaf nodes

    // Maximum number of objects stored in a leaf node.
    // Smaller values create a deeper tree, while larger values
    // increase the number of object tests per leaf.
    private static final int LEAF_SIZE = 4;

    public BVHNode(List<Object3D> objects) {

        // Compute a bounding box that contains every object in this list
        box = objects.get(0).getAABB();
        for (int i = 1; i < objects.size(); i++) {
            box = AABB.surrounding(box, objects.get(i).getAABB());
        }

        // If there are only a few objects left, store them in a leaf node
        if (objects.size() <= LEAF_SIZE) {
            leafObjects = new ArrayList<>(objects);
            return;
        }

        // Find the best axis to split the objects.
        // We use the object centroids and choose the axis with
        // the largest spread.
        Vector3D firstCentroid = objects.get(0).getAABB().centroid();
        AABB centroidBox = new AABB(firstCentroid, firstCentroid);
        for (int i = 1; i < objects.size(); i++) {
            Vector3D c = objects.get(i).getAABB().centroid();
            centroidBox = AABB.surrounding(centroidBox, new AABB(c, c));
        }
        int axis = centroidBox.longestAxis(); // 0=X, 1=Y, 2=Z

        // Compute the midpoint along the selected axis
        double mid;
        if      (axis == 0) mid = (centroidBox.min.x + centroidBox.max.x) / 2.0;
        else if (axis == 1) mid = (centroidBox.min.y + centroidBox.max.y) / 2.0;
        else                mid = (centroidBox.min.z + centroidBox.max.z) / 2.0;

        // Split the objects into two groups using the midpoint
        List<Object3D> leftList  = new ArrayList<>();
        List<Object3D> rightList = new ArrayList<>();

        for (Object3D obj : objects) {
            double centroid;
            if      (axis == 0) centroid = obj.getAABB().centroid().x;
            else if (axis == 1) centroid = obj.getAABB().centroid().y;
            else                centroid = obj.getAABB().centroid().z;

            if (centroid < mid) leftList.add(obj);
            else                rightList.add(obj);
        }

        // If all objects ended up on one side, split the list in half
        if (leftList.isEmpty() || rightList.isEmpty()) {
            int half = objects.size() / 2;
            leftList  = new ArrayList<>(objects.subList(0, half));
            rightList = new ArrayList<>(objects.subList(half, objects.size()));
        }

        // Build the left and right child nodes recursively
        left  = new BVHNode(leftList);
        right = new BVHNode(rightList);
    }

    // Returns the closest intersection found along the ray
    public Intersection intersect(Ray ray) {

        // If the ray misses this bounding box, it cannot hit anything inside
        if (!box.hit(ray)) return Intersection.MISS;

        // Leaf node: test all stored objects
        if (leafObjects != null) {
            Intersection nearest = Intersection.MISS;
            for (Object3D obj : leafObjects) {
                Intersection hit = obj.intersect(ray);
                if (hit.hit && hit.t < nearest.t) nearest = hit;
            }
            return nearest;
        }

        // Internal node: test both children and keep the closest hit
        Intersection hitLeft  = left.intersect(ray);
        Intersection hitRight = right.intersect(ray);
        return hitLeft.t < hitRight.t ? hitLeft : hitRight;
    }

    // Shadow ray version.
    // Returns true as soon as any object is hit before maxDist.
    public boolean intersectAny(Ray ray, double maxDist) {

        // Skip this subtree if the ray misses its bounding box
        if (!box.hit(ray)) return false;

        if (leafObjects != null) {
            for (Object3D obj : leafObjects) {
                Intersection hit = obj.intersect(ray);
                if (hit.hit && hit.t < maxDist) return true;
            }
            return false;
        }

        // Stop as soon as one child reports a hit
        return left.intersectAny(ray, maxDist) || right.intersectAny(ray, maxDist);
    }
}