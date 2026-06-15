package team.terrafirmgreg.fieldguide.render3d.math;

public final class Vector4f {

    public float x, y, z, w;
    
    public final static Vector4f UNIT_X = new Vector4f(1, 0, 0, 0);
    public final static Vector4f UNIT_Y = new Vector4f(0, 1, 0, 0);
    public final static Vector4f UNIT_Z = new Vector4f(0, 0, 1, 0);
    public final static Vector4f UNIT_W = new Vector4f(0, 0, 0, 1);
    
    public final static Vector4f ZERO = new Vector4f(0, 0, 0, 0);

    public Vector4f() {
        x = y = z = w = 0;
    }
    
    public Vector4f(float value) {
        x = y = z = w = value;
    }
    
    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector4f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = v.w;
    }

    public Vector4f(Vector3f v, float w) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = w;
    }
    
    public Vector4f(Vector2f v0, Vector2f v1) {
        this.x = v0.x;
        this.y = v0.y;
        this.z = v1.x;
        this.w = v1.y;
    }
    
    public Vector4f negate() {
        return new Vector4f(-x, -y, -z, -w);
    }

    public Vector4f negateLocal() {
        x = -x;
        y = -y;
        z = -z;
        w = -w;
        return this;
    }
    
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }
    
    public Vector4f normalize() {
        float length = x * x + y * y + z * z + w * w;
        if (length != 1f && length != 0f){
            length = 1.0f / (float) Math.sqrt(length);
            return new Vector4f(x * length, y * length, z * length, w * length);
        }
        return new Vector4f(this);
    }

    public Vector4f normalizeLocal() {
        float length = x * x + y * y + z * z + w * w;
        if (length != 1f && length != 0f){
            length = 1.0f / (float) Math.sqrt(length);
            x *= length;
            y *= length;
            z *= length;
            w *= length;
        }
        return this;
    }
    
    public Vector4f add(Vector4f v) {
        return new Vector4f(x + v.x, y + v.y, z + v.z, w + v.w);
    }

    public Vector4f addLocal(Vector4f v) {
        x += v.x;
        y += v.y;
        z += v.z;
        w += v.w;
        return this;
    }

    public Vector4f subtract(Vector4f v) {
        return new Vector4f(x - v.x, y - v.y, z - v.z, w - v.w);
    }

    public Vector4f subtractLocal(Vector4f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        w -= v.w;
        return this;
    }
    
    public float dot(Vector4f v) {
        return x * v.x + y * v.y + z * v.z + w * v.w;
    }

    public Vector4f project(Vector4f other){
        float n = this.dot(other); 
        float d = other.lengthSquared(); 
        return new Vector4f(other).multLocal(n/d);
    }

    public float distanceSquared(Vector4f v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;
        double dw = w - v.w;
        return (float) (dx * dx + dy * dy + dz * dz + dw * dw);
    }

    public float distance(Vector4f v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;
        double dw = w - v.w;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
    }

    public Vector4f mult(float scalar) {
        return new Vector4f(x * scalar, y * scalar, z * scalar, w * scalar);
    }
    
    public Vector4f multLocal(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
        return this;
    }

    public Vector4f mult(Vector4f v) {
        return new Vector4f(x * v.x, y * v.y, z * v.z, w * v.w);
    }

    public Vector4f mult(Vector4f v, Vector4f store) {
        if (store == null)
            store = new Vector4f();
        store.x = x * v.x;
        store.y = y * v.y;
        store.z = z * v.z;
        store.w = w * v.w;
        return store;
    }

    public Vector4f multLocal(Vector4f vec) {
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
        w *= vec.w;
        return this;
    }

    public Vector4f divide(float scalar) {
        scalar = 1f/scalar;
        return new Vector4f(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public Vector4f divideLocal(float scalar) {
        scalar = 1f/scalar;
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
        return this;
    }

    public Vector4f divide(Vector4f v) {
        return new Vector4f(x / v.x, y / v.y, z / v.z, w / v.w);
    }
    
    public Vector4f divideLocal(Vector4f v) {
        x /= v.x;
        y /= v.y;
        z /= v.z;
        w /= v.w;
        return this;
    }

    public Vector4f interpolateLocal(Vector4f finalVec, float changeAmnt) {
        this.x=(1-changeAmnt)*this.x + changeAmnt*finalVec.x;
        this.y=(1-changeAmnt)*this.y + changeAmnt*finalVec.y;
        this.z=(1-changeAmnt)*this.z + changeAmnt*finalVec.z;
        this.w=(1-changeAmnt)*this.w + changeAmnt*finalVec.w;
        return this;
    }

    public Vector4f interpolateLocal(Vector4f beginVec,Vector4f finalVec, float changeAmnt) {
        this.x=(1-changeAmnt)*beginVec.x + changeAmnt*finalVec.x;
        this.y=(1-changeAmnt)*beginVec.y + changeAmnt*finalVec.y;
        this.z=(1-changeAmnt)*beginVec.z + changeAmnt*finalVec.z;
        this.w=(1-changeAmnt)*beginVec.w + changeAmnt*finalVec.w;
        return this;
    }
    public Vector4f set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4f set(Vector3f v, float w) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = w;
        return this;
    }
    
    public Vector4f set(Vector4f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = v.w;
        return this;
    }
    
    public float get(int index) {
        switch (index) {
        case 0:
            return x;
        case 1:
            return y;
        case 2:
            return z;
        case 3:
            return w;
        default:
            throw new IndexOutOfBoundsException();
        }
    }
    
    public float getX() {
        return x;
    }

    public Vector4f setX(float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public Vector4f setY(float y) {
        this.y = y;
        return this;
    }

    public float getZ() {
        return z;
    }

    public Vector4f setZ(float z) {
        this.z = z;
        return this;
    }

    public float getW() {
        return w;
    }

    public Vector4f setW(float w) {
        this.w = w;
        return this;
    }

}