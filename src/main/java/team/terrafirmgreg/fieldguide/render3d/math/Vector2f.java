package team.terrafirmgreg.fieldguide.render3d.math;

public final class Vector2f {

    public float x, y;
    
    public static final Vector2f ZERO = new Vector2f(0f, 0f);

    public Vector2f() {
        x = y = 0;
    }
    
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2f v) {
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2f negate() {
        return new Vector2f(-x, -y);
    }

    public Vector2f negateLocal() {
        x = -x;
        y = -y;
        return this;
    }
    
    public Vector2f add(Vector2f v) {
        return new Vector2f(x + v.x, y + v.y);
    }

    public Vector2f addLocal(Vector2f v) {
        x += v.x;
        y += v.y;
        return this;
    }

    public Vector2f subtract(Vector2f v) {
        return new Vector2f(x - v.x, y - v.y);
    }

    public Vector2f subtract(Vector2f v, Vector2f result) {
        if (result == null)
            result = new Vector2f();
        
        result.x = x - v.x;
        result.y = y - v.y;
        return result;
    }
    
    public Vector2f subtractLocal(Vector2f v) {
        x -= v.x;
        y -= v.y;
        return this;
    }
    
    public float dot(Vector2f v) {
        return x * v.x + y * v.y;
    }

    public Vector3f cross(Vector2f v) {
        return new Vector3f(0, 0, determinant(v));
    }

    public float determinant(Vector2f v) {
        return (x * v.y) - (y * v.x);
    }
    
    public Vector2f interpolateLocal(Vector2f finalVec, float changeAmnt) {
        this.x = (1 - changeAmnt) * this.x + changeAmnt * finalVec.x;
        this.y = (1 - changeAmnt) * this.y + changeAmnt * finalVec.y;
        return this;
    }

    public Vector2f interpolateLocal(Vector2f beginVec, Vector2f finalVec,
            float changeAmnt) {
        this.x = (1 - changeAmnt) * beginVec.x + changeAmnt * finalVec.x;
        this.y = (1 - changeAmnt) * beginVec.y + changeAmnt * finalVec.y;
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float lengthSquared() {
        return x * x + y * y;
    }

    public float distanceSquared(Vector2f v) {
        double dx = x - v.x;
        double dy = y - v.y;
        return (float) (dx * dx + dy * dy);
    }

    public float distance(Vector2f v) {
        double dx = x - v.x;
        double dy = y - v.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public Vector2f mult(float scalar) {
        return new Vector2f(x * scalar, y * scalar);
    }

    public Vector2f multLocal(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public Vector2f mult(Vector2f v) {
        return new Vector2f(x * v.x, y * v.y);
    }
    
    public Vector2f multLocal(Vector2f v) {
        x *= v.x;
        y *= v.y;
        return this;
    }

    public Vector2f divide(float scalar) {
        return new Vector2f(x / scalar, y / scalar);
    }

    public Vector2f divideLocal(float scalar) {
        x /= scalar;
        y /= scalar;
        return this;
    }

    public Vector2f normalize() {
        float length = length();
        if (length != 0) {
            return divide(length);
        }

        return divide(1);
    }

    public Vector2f normalizeLocal() {
        float length = length();
        if (length != 0) {
            return divideLocal(length);
        }

        return divideLocal(1);
    }

    public float smallestAngleBetween(Vector2f otherVector) {
        float dotProduct = dot(otherVector);
        float angle = (float) Math.acos(dotProduct);
        return angle;
    }

    public float angleBetween(Vector2f otherVector) {
        float angle = (float)(Math.atan2(otherVector.y, otherVector.x)
                - Math.atan2(y, x));
        return angle;
    }

    public float getAngle() {
        return (float) Math.atan2(y, x);
    }
    
    public Vector2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2f set(Vector2f vec) {
        this.x = vec.x;
        this.y = vec.y;
        return this;
    }
    
    public float getX() {
        return x;
    }

    public Vector2f setX(float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public Vector2f setY(float y) {
        this.y = y;
        return this;
    }

}
