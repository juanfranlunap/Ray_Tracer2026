import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;



public class Raytracer {

    private Scene  scene;
    private Camera camera;

    public Raytracer(Scene scene, Camera camera) {
        this.scene  = scene;
        this.camera = camera;
    }

    public BufferedImage render() {

        int width  = camera.width;
        int height = camera.height;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                // step 1 - generate the ray for this pixel: P = O + tD
                Ray ray = camera.getRayForPixel(i, j);

                // step 2 - ask the scene if this ray hits anything
                Intersection intersection = scene.intersect(ray);

                // step 3 - paint the pixel
                Color pixelColor = Color.BLACK;

                if (intersection.hit) {
                    pixelColor = shade(intersection, ray);
                }

                image.setRGB(i, j, pixelColor.getRGB());
            }
        }

        return image;
    }

    // we calculate the final color by summing the contribution of every light
    // for each light: Diffuse = LC x OC x LI x (N · L)
    // then we add them all together and clamp to 0-255
    private Color shade(Intersection intersection, Ray ray) {

        // we get the normal N - phong interpolated for triangles
        Vector3D N;
        if (intersection.object instanceof Triangle) {
            Triangle tri = (Triangle) intersection.object;
            N = tri.getPhongNormal(intersection.u, intersection.v);
        } else {
            N = new Vector3D(0, 0, 1);
        }

        // we calculate the hit point on the surface: P = O + t*D
        // we need this for point lights to calculate their direction
        Vector3D hitPoint = ray.pointAt(intersection.t);

        // OC = object color (0.0 to 1.0)
        double ocR = intersection.object.color.getRed()   / 255.0;
        double ocG = intersection.object.color.getGreen() / 255.0;
        double ocB = intersection.object.color.getBlue()  / 255.0;

        // we accumulate the color from all lights
        double totalR = 0;
        double totalG = 0;
        double totalB = 0;

        List<Light> lights = scene.getLights();

        for (int i = 0; i < lights.size(); i++) {
            Light light = lights.get(i);

            // L = direction toward the light from the hit point
            // for directional: always the same
            // for point: calculated from hit point to light position
            Vector3D L = light.getDirection(hitPoint);

            // N · L = how much light hits this surface 
            // we shoot a ray from the hit point toward the light
            // if it hits anything, this point is in shadow for this light
            // we offset the origin slightly with tNear to avoid self-collision
            if (light instanceof PointLight) {
                PointLight pl = (PointLight) light;
                Vector3D offsetOrigin = hitPoint.add(L.multiply(0.001)); // smaller offset is better
                Ray shadowRay = new Ray(offsetOrigin, L);
                
                double distanceToLight = pl.position.subtract(hitPoint).length();
                
                Intersection shadowHit = scene.intersect(shadowRay);
                if (shadowHit.hit && shadowHit.t < distanceToLight) continue;
            }
 
            // N · L = how much light hits this surface
            double NdotL = Math.max(0, N.dot(L));
 
            // FALLOFF for point lights: LI = intensity / d²
            // directional lights have no falloff - distance is infinite
            double li;
            if (light instanceof PointLight) {
                li = ((PointLight) light).getFalloffIntensity(hitPoint);
            } else {
                li = light.intensity;
            }
 
            // LC = light color (0.0 to 1.0)
            double lcR = light.color.getRed()   / 255.0;
            double lcG = light.color.getGreen() / 255.0;
            double lcB = light.color.getBlue()  / 255.0;
 
            // Diffuse = LC x OC x LI x (N · L)
            totalR += lcR * ocR * li * NdotL;
            totalG += lcG * ocG * li * NdotL;
            totalB += lcB * ocB * li * NdotL;
        }
 
        int finalR = (int) Math.min(255, totalR * 255);
        int finalG = (int) Math.min(255, totalG * 255);
        int finalB = (int) Math.min(255, totalB * 255);
 
        return new Color(finalR, finalG, finalB);
    }
    public void renderToFile(String filename) throws Exception {
        BufferedImage image = render();
        ImageIO.write(image, "png", new File(filename));
        System.out.println("Done! Open " + filename);
    }
}