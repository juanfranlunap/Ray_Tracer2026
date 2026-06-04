public class Camera {
    public Vector3D position;
    public double   fov;

    public Camera(Vector3D position, double fov) {
        this.position = position;
        this.fov      = fov;
    }

    // Resolution belongs to the output image, not the camera, so width/height are passed in
    public Ray getRayForPixel(int i, int j, int width, int height) {
        double aspectRatio = (double) width / height;
        double scale = Math.tan(Math.toRadians(fov / 2.0));

        double px = (2.0 * (i + 0.5) / width  - 1.0) * scale * aspectRatio;
        double py = (1.0 - 2.0 * (j + 0.5) / height) * scale;

        return new Ray(this.position, new Vector3D(px, py, -1.0));
    }
}
