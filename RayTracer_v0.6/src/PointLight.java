import java.awt.Color;

// point light - radiates in all directions from a fixed position, like a light bulb
// the direction L changes for every hit point - it always points from the hit to the light

public class PointLight extends Light {

    public Vector3D position; 

    public PointLight(Vector3D position, Color color, double intensity) {
        super(color, intensity);
        this.position = position;
    }

    // for point light we calculate the direction from the hit point to the light
    // L = normalize(light.position - hitPoint)
    //  is different for every point in the scene
    @Override
    public Vector3D getDirection(Vector3D hitPoint) {
        return position.subtract(hitPoint).normalize();
    }
}