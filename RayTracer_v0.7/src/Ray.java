// Ray equation :
//   P = O + t*D
//   O = origin of the ray
//   D = direction of the ray
//   t = distance

public class Ray {
    public Vector3D origin;     
    public Vector3D direction;   

    public Ray(Vector3D origin, Vector3D direction) {
        this.origin    = origin;
        this.direction = direction.normalize(); 
    }

    public Vector3D pointAt(double t) {
        return origin.add(direction.multiply(t));
    }
}