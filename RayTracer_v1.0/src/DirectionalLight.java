import java.awt.Color;

// Directional light - same direction everywhere, like the sun
public class DirectionalLight extends Light {

    public Vector3D direction;

    public DirectionalLight(Vector3D direction, Color color, double intensity) {
        super(color, intensity);
        this.direction = direction.normalize();
    }

    @Override
    public Vector3D getDirection(Vector3D hitPoint) {
        return direction;
    }

    @Override
    public double getEffectiveIntensity(Vector3D hitPoint) {
        return intensity; // no falloff for directional light
    }

    @Override
    public double getDistanceToSource(Vector3D hitPoint) {
        return Double.MAX_VALUE; // directional light has no position
    }
}
