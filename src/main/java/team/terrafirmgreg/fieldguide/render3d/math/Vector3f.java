package team.terrafirmgreg.fieldguide.render3d.math;

public class Vector3f {

    public float x, y, z;

    public final static Vector3f UNIT_X = new Vector3f(1, 0, 0);
    public final static Vector3f UNIT_Y = new Vector3f(0, 1, 0);
    public final static Vector3f UNIT_Z = new Vector3f(0, 0, 1);
    
    public final static Vector3f ZERO = new Vector3f(0, 0, 0);
    
    public Vector3f() {
        x = y = z = 0;
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    
    public Vector3f negate() {
        return new Vector3f(-x, -y, -z);
    }
    
    public Vector3f negateLocal() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }
    
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3f normalize() {
        float length = x * x + y * y + z * z;
        if (length != 1f && length != 0f) {
            length = (float) (1.0 / Math.sqrt(length));
            return new Vector3f(x * length, y * length, z * length);
        }
        return new Vector3f(x, y, z);
    }
    
    public Vector3f normalizeLocal() {
        float length = x * x + y * y + z * z;
        if (length != 1f && length != 0f) {
            length = (float) (1.0 / Math.sqrt(length));
            x *= length;
            y *= length;
            z *= length;
        }
        return this;
    }
    
    public float distance(Vector3f v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public float distanceSquared(Vector3f v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;
        return (float) (dx * dx + dy * dy + dz * dz);
    }

    public Vector3f add(Vector3f v) {
        return new Vector3f(x + v.x, y + v.y, z + v.z);
    }
    
    public Vector3f add(Vector3f vec, Vector3f result) {
        if (result == null) result = new Vector3f();
        result.x = x + vec.x;
        result.y = y + vec.y;
        result.z = z + vec.z;
        return result;
    }
    
    public Vector3f addLocal(Vector3f v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    public Vector3f subtract(Vector3f v) {
        return new Vector3f(x - v.x, y - v.y, z - v.z);
    }
    
    public Vector3f subtract(Vector3f v, Vector3f result) {
        if(result == null) {
            result = new Vector3f();
        }
        result.x = x - v.x;
        result.y = y - v.y;
        result.z = z - v.z;
        return result;
    }
    
    public Vector3f subtractLocal(Vector3f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    public Vector3f mult(float scalor) {
        return new Vector3f(x * scalor, y * scalor, z * scalor);
    }
    
    public Vector3f mult(float scalor, Vector3f store) {
        if (store == null) {
            store = new Vector3f();
        }
        store.set(x * scalor, y * scalor, z * scalor);
        return store;
    }
    
    public Vector3f multLocal(float scalor) {
        x *= scalor;
        y *= scalor;
        z *= scalor;
        return this;
    }
    
    public Vector3f mult(Vector3f v) {
        return new Vector3f(x * v.x, y * v.y, z * v.z);
    }

    public Vector3f mult(Vector3f vec, Vector3f store) {
        if (store == null) store = new Vector3f();
        return store.set(x * vec.x, y * vec.y, z * vec.z);
    }
    
    public Vector3f multLocal(Vector3f v) {
        x *= v.x;
        y *= v.y;
        z *= v.z;
        return this;
    }
    
    public Vector3f divide(float scalor) {
        scalor = 1 / scalor;
        return new Vector3f(x * scalor, y * scalor, z * scalor);
    }
    
    public Vector3f divideLocal(float scalor) {
        scalor = 1 / scalor;
        x *= scalor;
        y *= scalor;
        z *= scalor;
        return this;
    }

    public Vector3f divide(Vector3f v) {
        return new Vector3f( x / v.x, y / v.y, z / v.z);
    }
    
    public Vector3f divideLocal(Vector3f v) {
        x /= v.x;
        y /= v.y;
        z /= v.z;
        return this;
    }
    
    public float dot(Vector3f v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public float angleBetween(Vector3f v) {
        float dotProduct = x * v.x + y * v.y + z * v.z;
        float angle = (float) Math.acos(dotProduct);
        return angle;
    }
    
    public Vector3f project(Vector3f v){
        float n = x * v.x + y * v.y + z * v.z; 
        float d = v.lengthSquared(); 
        float scalor = n / d;
        return new Vector3f(v.x * scalor, v.y * scalor, v.z * scalor);
    }

    public Vector3f projectLocal(Vector3f v){
        float n = this.dot(v); 
        float d = v.lengthSquared(); 
        float scalor = n / d;
        x = v.x * scalor;
        y = v.y * scalor;
        z = v.z * scalor;
        return this;
    }
    
    public Vector3f cross(Vector3f v) {
        float rx = y * v.z - z * v.y;
        float ry = z * v.x - x * v.z;
        float rz = x * v.y - y * v.x;
        return new Vector3f(rx, ry, rz);
    }
    
    public Vector3f cross(Vector3f v, Vector3f result) {
        if (result == null) result = new Vector3f();
        float resX = ((y * v.x) - (z * v.y)); 
        float resY = ((z * v.x) - (x * v.z));
        float resZ = ((x * v.y) - (y * v.x));
        result.set(resX, resY, resZ);
        return result;
    }
    
    public Vector3f crossLocal(Vector3f v) {
        float tempX = y * v.z - z * v.y;
        float tempY = z * v.x - x * v.z;
        z = x * v.y - y * v.x;
        x = tempX;
        y = tempY;
        return this;
    }
    
    public Vector3f interpolateLocal(Vector3f finalVec, float changeAmnt) {
        this.x=(1-changeAmnt)*this.x + changeAmnt*finalVec.x;
        this.y=(1-changeAmnt)*this.y + changeAmnt*finalVec.y;
        this.z=(1-changeAmnt)*this.z + changeAmnt*finalVec.z;
        return this;
    }

    public Vector3f interpolateLocal(Vector3f beginVec,Vector3f finalVec, float changeAmnt) {
        this.x=(1-changeAmnt)*beginVec.x + changeAmnt*finalVec.x;
        this.y=(1-changeAmnt)*beginVec.y + changeAmnt*finalVec.y;
        this.z=(1-changeAmnt)*beginVec.z + changeAmnt*finalVec.z;
        return this;
    }
    
    public Vector3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3f set(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        return this;
    }
    
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
