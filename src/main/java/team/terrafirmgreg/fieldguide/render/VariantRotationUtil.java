package team.terrafirmgreg.fieldguide.render;

import team.terrafirmgreg.fieldguide.render3d.math.Quaternion;
import team.terrafirmgreg.fieldguide.render3d.math.Vector3f;
import team.terrafirmgreg.fieldguide.render3d.scene.Node;

final class VariantRotationUtil {

    private static final Vector3f BLOCK_CENTER = new Vector3f(0.5f, 0.5f, 0.5f);

    private VariantRotationUtil() {
    }

    static void applyBlockVariantRotation(Node node, int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) {
            return;
        }
        Quaternion rotation = rotationFromDegrees(x, y, z);
        Vector3f rotatedCenter = rotation.mult(BLOCK_CENTER, new Vector3f());
        Vector3f offset = BLOCK_CENTER.subtract(rotatedCenter, new Vector3f());
        node.getLocalTransform().setRotation(rotation);
        node.getLocalTransform().setTranslation(offset);
    }

    static Quaternion rotationFromDegrees(int x, int y, int z) {
        Quaternion result = new Quaternion();
        result.loadIdentity();
        if (x != 0) {
            result.multLocal(new Quaternion().rotateX((float) Math.toRadians(x)));
        }
        if (y != 0) {
            result.multLocal(new Quaternion().rotateY((float) Math.toRadians(-y)));
        }
        if (z != 0) {
            result.multLocal(new Quaternion().rotateZ((float) Math.toRadians(z)));
        }
        return result;
    }
}
