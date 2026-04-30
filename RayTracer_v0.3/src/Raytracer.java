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

    public Raytracer(Scene scene, Camera camera) {
        this.scene           = scene;
        this.camera          = camera;
        this.backgroundColor = Color.WHITE;
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
                    pixelColor = intersection.object.color;
                } else {
                    pixelColor = backgroundColor;
                }

                image.setRGB(i, j, pixelColor.getRGB());
            }
        }

        return image;
    }

    public void renderToFile(String filename) throws Exception {
        BufferedImage image = render();
        File output = new File(filename);
        ImageIO.write(image, "png", output);
        System.out.println("Image saved to: " + output.getAbsolutePath());
    }
}