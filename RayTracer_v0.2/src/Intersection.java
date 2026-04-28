

public class Intersection {
    public boolean hit;      // was there a collision?
    public double  t;        // distance t where the ray hits 
    public Object3D object;  // which object was hit

    // No collision
    public Intersection() {
        this.hit    = false;
        this.t      = Double.MAX_VALUE;
        this.object = null;
    }

    // Collision found
    public Intersection(double t, Object3D object) {
        this.hit    = true;
        this.t      = t;
        this.object = object;
    }
}