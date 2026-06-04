import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Raytracer {

    private static final int MAX_DEPTH = 4; // max bounces (reflection + refraction)

    private Scene  scene;
    private Camera camera;
    private int    width;
    private int    height;

    public Raytracer(Scene scene, Camera camera, int width, int height) {
        this.scene  = scene;
        this.camera = camera;
        this.width  = width;
        this.height = height;
    }

    public BufferedImage render() {
        BufferedImage image    = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int           nThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        CountDownLatch  latch    = new CountDownLatch(height);

        for (int j = 0; j < height; j++) {
            final int row = j;
            executor.submit(() -> {
                for (int i = 0; i < width; i++) {
                    Ray   ray   = camera.getRayForPixel(i, row, width, height);
                    Color color = trace(ray, MAX_DEPTH);
                    image.setRGB(i, row, color.getRGB());
                }
                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdown();
        return image;
    }

    // Traces a ray - depth controls remaining bounces
    private Color trace(Ray ray, int depth) {
        Intersection hit = scene.intersect(ray);
        if (!hit.hit) return skyColor(ray);
        return shade(hit, ray, depth);
    }

    // Gradient: amarillo (arriba) -> azul (medio) -> rojo (horizonte)
    private Color skyColor(Ray ray) {
    return new Color(0, 0, 0);  // negro puro
    }

    // Blinn-Phong + reflection + refraction
    //
    // ambient  = kA * objectColor
    // diffuse  = kD * OC * LC * LI * max(0, N·L)
    // specular = kS * LC * LI * max(0, N·H)^shininess   H = normalize(L + V)
    // reflection: R = D - 2(D·N)N
    // refraction: Snell's law  n1*sin(θ1) = n2*sin(θ2)
    private Color shade(Intersection intersection, Ray ray, int depth) {
        Material mat      = intersection.object.material;
        Vector3D hitPoint = ray.pointAt(intersection.t);
        Vector3D N        = intersection.object.getNormal(hitPoint, intersection.u, intersection.v);
        Vector3D V        = ray.direction.multiply(-1).normalize();

        // normales invertidas
        if (N.dot(V) < 0) {
            N = N.multiply(-1);
        }
        
        double ocR = mat.color.getRed()   / 255.0;
        double ocG = mat.color.getGreen() / 255.0;
        double ocB = mat.color.getBlue()  / 255.0;

        // Ambient
        double totalR = mat.ambient * ocR;
        double totalG = mat.ambient * ocG;
        double totalB = mat.ambient * ocB;

        for (Light light : scene.getLights()) {
            Vector3D L    = light.getDirection(hitPoint);
            double   dist = light.getDistanceToSource(hitPoint);

            // Shadow check — intersectAny exits at first blocker, skips finding closest hit
            Ray shadowRay = new Ray(hitPoint.add(L.multiply(0.001)), L);
            if (scene.intersectAny(shadowRay, dist)) continue;

            double li  = light.getEffectiveIntensity(hitPoint);
            double lcR = light.color.getRed()   / 255.0;
            double lcG = light.color.getGreen() / 255.0;
            double lcB = light.color.getBlue()  / 255.0;

            // Diffuse
            double NdotL = Math.max(0, N.dot(L));
            totalR += mat.diffuse * ocR * lcR * li * NdotL;
            totalG += mat.diffuse * ocG * lcG * li * NdotL;
            totalB += mat.diffuse * ocB * lcB * li * NdotL;

            // Specular
            Vector3D H     = L.add(V).normalize();
            double   NdotH = Math.max(0, N.dot(H));
            double   spec  = mat.specular * li * Math.pow(NdotH, mat.shininess);
            totalR += spec * lcR;
            totalG += spec * lcG;
            totalB += spec * lcB;
        }

        // Reflection: R = D - 2(D·N)N
        if (mat.reflectivity > 0 && depth > 0) {
            Vector3D D          = ray.direction;
            Vector3D reflectDir = D.subtract(N.multiply(2.0 * D.dot(N)));
            Color    reflColor  = trace(new Ray(hitPoint.add(N.multiply(0.001)), reflectDir), depth - 1);

            double rr = reflColor.getRed()   / 255.0;
            double rg = reflColor.getGreen() / 255.0;
            double rb = reflColor.getBlue()  / 255.0;

            totalR = (1 - mat.reflectivity) * totalR + mat.reflectivity * rr;
            totalG = (1 - mat.reflectivity) * totalG + mat.reflectivity * rg;
            totalB = (1 - mat.reflectivity) * totalB + mat.reflectivity * rb;
        }

        // Refraction (Snell's law)
        if (mat.transparency > 0 && depth > 0) {
            Vector3D refractN = N;
            double   n1, n2;

            // Why: D·N > 0 means the ray hits the back face - we are exiting the object
            if (ray.direction.dot(N) > 0) {
                refractN = N.multiply(-1); // flip normal when exiting
                n1 = mat.ior;
                n2 = 1.0;                  // back into air
            } else {
                n1 = 1.0;                  // coming from air
                n2 = mat.ior;
            }

            double ratio  = n1 / n2;
            double cosI   = -refractN.dot(ray.direction);   // cos of incident angle
            double sin2T  = ratio * ratio * (1.0 - cosI * cosI); // sin²(refracted angle)

            // Why: sin2T > 1 means total internal reflection - no refracted ray exists
            if (sin2T <= 1.0) {
                double   cosT       = Math.sqrt(1.0 - sin2T);
                Vector3D refractDir = ray.direction.multiply(ratio)
                                      .add(refractN.multiply(ratio * cosI - cosT));

                // offset origin against refractN to step inside/outside the surface
                Ray   refractRay   = new Ray(hitPoint.subtract(refractN.multiply(0.001)), refractDir);
                Color refractColor = trace(refractRay, depth - 1);

                double tr = refractColor.getRed()   / 255.0;
                double tg = refractColor.getGreen() / 255.0;
                double tb = refractColor.getBlue()  / 255.0;

                totalR = (1 - mat.transparency) * totalR + mat.transparency * tr;
                totalG = (1 - mat.transparency) * totalG + mat.transparency * tg;
                totalB = (1 - mat.transparency) * totalB + mat.transparency * tb;
            }
        }

        int r = (int) Math.min(255, totalR * 255);
        int g = (int) Math.min(255, totalG * 255);
        int b = (int) Math.min(255, totalB * 255);

        return new Color(r, g, b);
    }

    public void renderToFile(String filename) throws Exception {
        BufferedImage image = render();
        ImageIO.write(image, "png", new File(filename));
        System.out.println("Done! Open " + filename);
    }
}
