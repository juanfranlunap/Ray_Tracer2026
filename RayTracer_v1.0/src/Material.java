import java.awt.Color;

public class Material {
    public Color  color;
    public double ambient;      // kA - base light even without direct illumination
    public double diffuse;      // kD - how much direct light the surface scatters
    public double specular;     // kS - strength of the shiny highlight
    public double shininess;    // n  - tightness of the specular highlight (higher = sharper)
    public double reflectivity; // 0.0 = matte, 1.0 = perfect mirror
    public double transparency; // 0.0 = opaque, 1.0 = fully transparent
    public double ior;          // index of refraction (glass=1.5, water=1.33, diamond=2.42)

    public Material(Color color, double ambient, double diffuse, double specular,
                    double shininess, double reflectivity, double transparency, double ior) {
        this.color        = color;
        this.ambient      = ambient;
        this.diffuse      = diffuse;
        this.specular     = specular;
        this.shininess    = shininess;
        this.reflectivity = reflectivity;
        this.transparency = transparency;
        this.ior          = ior;
    }
}
