import java.awt.Color;

// Point light - radiates in all directions from a fixed position, like a light bulb
public class PointLight extends Light {

    public Vector3D position;

    public PointLight(Vector3D position, Color color, double intensity) {
        super(color, intensity);
        this.position = position;
    }

    @Override
    public Vector3D getDirection(Vector3D hitPoint) {
        return position.subtract(hitPoint).normalize();
    }

    @Override
    public double getEffectiveIntensity(Vector3D hitPoint) {
        double d = getDistanceToSource(hitPoint);
        if (d < 0.0001) return intensity;
        return intensity / (d * d); // inverse square falloff: LI = intensity / d²
    }

    @Override
    public double getDistanceToSource(Vector3D hitPoint) {
        return position.subtract(hitPoint).length();
    }
}
