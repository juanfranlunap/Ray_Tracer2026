import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

// Steps
// 1. For every pixel → generates a ray (O + tD)
// 2. Asks the scene for an intersection
// 3. Paints the pixel with the object's color or the background color if no hit.

public class Raytracer {
    private Scene  scene;
    private Camera camera;
    private Color  backgroundColor;
     private Light  light; 

    public Raytracer(Scene scene, Camera camera, Light light) {
        this.scene           = scene;
        this.camera          = camera;
        this.backgroundColor = Color.WHITE;
        this.light  = light; 
    }

    public BufferedImage render() {
        int width  = camera.width;
        int height = camera.height;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int j = 0; j < height; j++) {       // rows
            for (int i = 0; i < width; i++) {     // columns

                // 1. Generate ray for this pixel  ( O + tD)
                Ray ray = camera.getRayForPixel(i, j);

                // 2. Ask the scene: does this ray hit anything?
                Intersection intersection = scene.intersect(ray);

                // 3. Paint the pixel
                Color pixelColor;
                if (intersection.hit) {
                    pixelColor = shade(intersection); 
                } else {
                    pixelColor = backgroundColor;
                }

                image.setRGB(i, j, pixelColor.getRGB());
            }
        }

        return image;
    }


    // shade calculates the final color of a pixel using flat shading
    private Color shade(Intersection intersection) {
 
        Vector3D N;
 
        if (intersection.object instanceof Triangle) {
            Triangle tri = (Triangle) intersection.object;
            N = tri.getPhongNormal(intersection.u, intersection.v);
        } else {
            N = new Vector3D(0, 0, 1);
        }
 
        // we use Math.max to clamp it to 0 - negative means light is behind the surface
        double NdotL = Math.max(0, N.dot(light.direction));
 
        // OC = object color broken into r g b components (0.0 to 1.0)
        double ocR = intersection.object.color.getRed()   / 255.0;
        double ocG = intersection.object.color.getGreen() / 255.0;
        double ocB = intersection.object.color.getBlue()  / 255.0;
 
        // LC = light color broken into r g b components (0.0 to 1.0)
        double lcR = light.color.getRed()   / 255.0;
        double lcG = light.color.getGreen() / 255.0;
        double lcB = light.color.getBlue()  / 255.0;
 
        // Diffuse = LC x OC x LI x (N · L)  
        double r = lcR * ocR * light.intensity * NdotL;
        double g = lcG * ocG * light.intensity * NdotL;
        double b = lcB * ocB * light.intensity * NdotL;
 
        // we clamp the values between 0 and 255 so the color stays valid
        int finalR = (int) Math.min(255, r * 255);
        int finalG = (int) Math.min(255, g * 255);
        int finalB = (int) Math.min(255, b * 255);
 
        return new Color(finalR, finalG, finalB);
    }
    public void renderToFile(String filename) throws Exception {
        BufferedImage image = render();
        File output = new File(filename);
        ImageIO.write(image, "png", output);
        System.out.println("Image saved to: " + output.getAbsolutePath());
    }
}