public class Camera {
    public Vector3D position;   
    public int      width;     
    public int      height;     
    public double   fov;        

    public Camera(Vector3D position, int width, int height, double fov) {
        this.position = position;
        this.width    = width;
        this.height   = height;
        this.fov      = fov;
    }

    // Generate the ray for pixel (i, j)
    // Converts pixel coordinates → normalized device coords → camera space
    // Why: Pixels are 2D, but rays live in 3D , so we have to make the conversion
    public Ray getRayForPixel(int i, int j) {
        double aspectRatio = (double) width / height;
        // Why: prevents image stretching when width ≠ height

        double scale = Math.tan(Math.toRadians(fov / 2.0));
        // Why: controls how wide the camera sees ( larger FOV → wider view)

        // Map pixel center to [-1, 1] range, then scale by FOV & aspect ratio
        // Why: normalize pixel coordinates so they fit the camera's view space
        double px = (2.0 * (i + 0.5) / width  - 1.0) * scale * aspectRatio;
        double py = (1.0 - 2.0 * (j + 0.5) / height) * scale;
        // Why: (i + 0.5, j + 0.5) , to be more accurated the  shoots of the ray through pixel center 

        // Direction D from camera origin toward pixel
        // Why: defines where the ray is going in 3D space
        Vector3D direction = new Vector3D(px, py, -1.0); 


        return new Ray(this.position, direction);
    }
}