public class Vector3D {
    public double x, y, z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Vector addition  V + W
    // Why: We use it to move a point in space (position + direction)
    public Vector3D add(Vector3D v) {
        return new Vector3D(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    // Vector subtraction  V - W
    // Subtracts each component independently
    // Why: It gives the direction from one point to another (A - B = direction)
    public Vector3D subtract(Vector3D v) {
        return new Vector3D(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    // Scalar multiplication  V * t
    // Multiplies each component by a scalar t
    // Why: Used to control distance 
    public Vector3D multiply(double t) {
        return new Vector3D(this.x * t, this.y * t, this.z * t); // ray = origin + direction * t
    }

    // Dot product  V·W
    // Why: We use it for projections, angles, lighting, and intersections
    // Analytical Solution :  a = D·D,  b = 2*(O·D),  c = O·O - R²
    // Geometric:   tca = L·D,  d² = L·L - tca²
    public double dot(Vector3D v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    // Squared length  
    public double lengthSquared() {
        return this.dot(this);
    }

    // In this case the lenght is the magnitude  |V|
    // It represents the real distance of the vector
    // Why: W use this  to do the normalization and calculate real distance operations
    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    // Normalize (Unit Vcetor)
    // Why: Keeps only direction, removes magnitude 
    public Vector3D normalize() {
        double len = this.length();
        return new Vector3D(this.x / len, this.y / len, this.z / len);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }
}