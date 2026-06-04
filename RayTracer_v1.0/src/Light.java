import java.awt.Color;

public abstract class Light {

    public Color  color;
    public double intensity;

    public Light(Color color, double intensity) {
        this.color     = color;
        this.intensity = intensity;
    }

    // Direction from hitPoint toward the light source
    public abstract Vector3D getDirection(Vector3D hitPoint);

    // Effective intensity at a given hit point (accounts for falloff)
    public abstract double getEffectiveIntensity(Vector3D hitPoint);

    // Distance from hitPoint to the light source (MAX_VALUE for directional)
    public abstract double getDistanceToSource(Vector3D hitPoint);
}
