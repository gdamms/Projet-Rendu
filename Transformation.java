
import algebra.*;

/**
 * author: cdehais
 */
public class Transformation {

    Matrix worldToCamera;
    Matrix projection;
    Matrix calibration;

    public Transformation() {
        try {
            worldToCamera = new Matrix("W2C", 4, 4);
            projection = new Matrix("P", 3, 4);
            calibration = Matrix.createIdentity(3);
            calibration.setName("K");
        } catch (InstantiationException e) {
            /* should not reach */
        }
    }

    public void setLookAt(Vector3 cam, Vector3 lookAt, Vector3 up) {
        try {
            // compute rotation
            Vector3 e3c = new Vector3(
                    lookAt.getX() - cam.getX(),
                    lookAt.getY() - cam.getY(),
                    lookAt.getZ() - cam.getZ());
            e3c.normalize();

            Vector3 e1c = up.cross(e3c);
            e1c.normalize();

            Vector3 e2c = e3c.cross(e1c);

            // compute translation
            worldToCamera.set(0, 0, e1c.getX());
            worldToCamera.set(0, 1, e1c.getY());
            worldToCamera.set(0, 2, e1c.getZ());

            worldToCamera.set(1, 0, e2c.getX());
            worldToCamera.set(1, 1, e2c.getY());
            worldToCamera.set(1, 2, e2c.getZ());

            worldToCamera.set(2, 0, e3c.getX());
            worldToCamera.set(2, 1, e3c.getY());
            worldToCamera.set(2, 2, e3c.getZ());

            worldToCamera.set(3, 3, 1);

            Matrix A = worldToCamera.getSubMatrix(0, 0, 3, 3);

            Vector translation = A.multiply(cam);

            worldToCamera.set(0, 3, -translation.get(0));
            worldToCamera.set(1, 3, -translation.get(1));
            worldToCamera.set(2, 3, -translation.get(2));

        } catch (Exception e) {
            /* unreached */
        }

        System.out.println("Modelview matrix:\n" + worldToCamera);
    }

    public void setProjection() {
        projection.set(0, 0, 1);
        projection.set(1, 1, 1);
        projection.set(2, 2, 1);

        System.out.println("Projection matrix:\n" + projection);
    }

    public void setCalibration(double focal, double width, double height) {

        calibration.set(0, 0, focal);
        calibration.set(1, 1, focal);
        calibration.set(2, 2, 1);

        calibration.set(0, 2, width / 2);
        calibration.set(1, 2, height / 2);

        System.out.println("Calibration matrix:\n" + calibration);
    }

    /**
     * Projects the given homogeneous, 4 dimensional point onto the screen.
     * The resulting Vector as its (x,y) coordinates in pixel, and its z coordinate
     * is the depth of the point in the camera coordinate system.
     */
    public Vector3 projectPoint(Vector p)
            throws SizeMismatchException, InstantiationException {
        Vector ps = new Vector(3);

        ps = worldToCamera.multiply(p);
        ps = projection.multiply(ps);
        ps = calibration.multiply(ps);

        Matrix inter = Matrix.createIdentity(3);
        inter.set(0, 0, 1 / ps.get(2));
        inter.set(1, 1, 1 / ps.get(2));
        inter.set(2, 2, 1);

        ps = inter.multiply(ps);

        return new Vector3(ps);
    }

    /**
     * Transform a vector from world to camera coordinates.
     */
    public Vector3 transformVector(Vector3 v)
            throws SizeMismatchException, InstantiationException {
        /* Doing nothing special here because there is no scaling */
        Matrix R = worldToCamera.getSubMatrix(0, 0, 3, 3);
        Vector tv = R.multiply(v);
        return new Vector3(tv);
    }

}
