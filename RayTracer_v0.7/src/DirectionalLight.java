import java.awt.Color;

// directional light - same direction everywhere, like the sun
// the direction does not change no matter where the hit point is

public class DirectionalLight extends Light {

    public Vector3D direction; // L - fixed direction for all points in the scene

    public DirectionalLight(Vector3D direction, Color color, double intensity) {
        super(color, intensity);
        this.direction = direction.normalize(); 
    }
    @Override
    public Vector3D getDirection(Vector3D hitPoint) {
        return direction;
    }
}