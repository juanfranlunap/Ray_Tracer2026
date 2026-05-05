public class Vector3D {

    public double x, y, z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D add(Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }


    // used to get the edges of the triangle: v1 - v0, v2 - v0
    // also used to get T = O - v0 and L = C - O in sphere
    public Vector3D subtract(Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    public Vector3D multiply(double t) {
        return new Vector3D(x * t, y * t, z * t);
    }

    // used  to find tca in sphere, and to find u v t in triangle
    public double dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

  
    // used to calculate P = D x v1v0  and  Q = T x v2v0
    public Vector3D cross(Vector3D v) {
        return new Vector3D(
            y * v.z - z * v.y,
            z * v.x - x * v.z,
            x * v.y - y * v.x
        );
    }

    // real lenght of the vector, used to normalize it
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    // we make the vector length equal to 1
    // used in Ray so that t represents actual distance
    public Vector3D normalize() {
        double len = length();
        return new Vector3D(x / len, y / len, z / len);
    }
}