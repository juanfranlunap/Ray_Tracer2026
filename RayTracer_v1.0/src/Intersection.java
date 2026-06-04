

public class Intersection {
    public static final Intersection MISS = new Intersection();

    public boolean hit;      // was there a collision?
    public double  t;        // distance t where the ray hits
    public Object3D object;  // which object was hit
    public double u;
    public double v;

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
        this .u      = 0;
        this .v      = 0;
    }
    
    public Intersection(double t, Object3D object, double u, double v) {
        this.hit    = true;
        this.t      = t;
        this.object = object;
        this.u      = u;
        this.v      = v;
    }

}
