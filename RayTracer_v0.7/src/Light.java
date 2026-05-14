import java.awt.Color;
public abstract class Light {

    public Color    color;     // LC - color of the light
    public double   intensity; // LI - how strong the light is (0.0 to 1.0)

    public Light(Color color, double intensity) {
        this.color     = color;
        this.intensity = intensity;
    }
public abstract Vector3D getDirection(Vector3D hitPoint);
}

