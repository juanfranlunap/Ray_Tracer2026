import java.awt.Color;
public class Light {

    public Vector3D direction; // L - where the light comes from
    public Color    color;     // LC - color of the light
    public double   intensity; // LI - how strong the light is (0.0 to 1.0)

    public Light(Vector3D direction, Color color, double intensity) {
        // normalization the direction so the dot product gives correct results
        this.direction = direction.normalize();
        this.color     = color;
        this.intensity = intensity;
    }
}