package team.terrafirmgreg.fieldguide.render3d.math;

public class Transform {

    public static final Transform IDENTITY = new Transform();
    
    private Vector3f scale = new Vector3f(1, 1, 1);
    private Quaternion rot = new Quaternion();
    private Vector3f translation = new Vector3f(0, 0, 0);
    
    public Transform(Vector3f translation, Quaternion rot){
        this.translation.set(translation);
        this.rot.set(rot);
    }
    
    public Transform(Vector3f translation, Quaternion rot, Vector3f scale){
        this(translation, rot);
        this.scale.set(scale);
    }

    public Transform(Vector3f translation){
        this(translation, Quaternion.IDENTITY);
    }

    public Transform(Quaternion rot){
        this(Vector3f.ZERO, rot);
    }

    public Transform(){
        this(Vector3f.ZERO, Quaternion.IDENTITY);
    }
    
    public void loadIdentity() {
        scale.set(1, 1, 1);
        rot.set(0, 0, 0, 1);
        translation.set(0, 0, 0);
    }
    
    public Vector3f transformVector(final Vector3f in, Vector3f store){
        if (store == null)
            store = new Vector3f();
        
        store.set(in);
        
        store.multLocal(scale);
        
        rot.mult(store, store);
        
        store.addLocal(translation);
        return store;
    }

    public Vector3f transformInverseVector(final Vector3f in, Vector3f store){
        if (store == null)
            store = new Vector3f();

        in.subtract(translation, store);
        
        rot.inverse().mult(store, store);
        
        store.divideLocal(scale);

        return store;
    }
    
    public Vector3f transformNormal(final Vector3f in, Vector3f store){
        if (store == null)
            store = new Vector3f();
        
        store.set(in);
        
        if (scale.x == scale.y && scale.y == scale.z) {
            
            if (scale.x != 0) {
                store.multLocal(1.0f / scale.x);
            }
        }
        
        rot.mult(store, store);
        
        return store;
    }
    
    public Matrix4f toTransformMatrix() {
        Matrix4f trans = new Matrix4f();
        trans.setTranslation(translation);
        trans.setRotationQuaternion(rot);
        trans.setScale(scale);
        return trans;
    }
    
    public void fromTransformMatrix(Matrix4f mat) {
        translation.set(mat.toTranslationVector());
        rot.set(mat.toRotationQuad());
        scale.set(mat.toScaleVector());
    }
    
    public Transform invert() {
        Transform t = new Transform();
        t.fromTransformMatrix(toTransformMatrix().invertLocal());
        return t;
    }
    
    public void interpolateTransforms(Transform t1, Transform t2, float delta) {
        this.rot.slerp(t1.rot,t2.rot,delta);
        this.translation.interpolateLocal(t1.translation,t2.translation,delta);
        this.scale.interpolateLocal(t1.scale,t2.scale,delta);
    }
    
    public Transform combineWithParent(Transform parent) {
        scale.multLocal(parent.scale);
        parent.rot.mult(rot, rot);

        translation.multLocal(parent.scale);
        parent.rot.multLocal(translation)   
            .addLocal(parent.translation);  
        return this;
    }
    
    public Transform set(Transform matrixQuat) {
        this.translation.set(matrixQuat.translation);
        this.rot.set(matrixQuat.rot);
        this.scale.set(matrixQuat.scale);
        return this;
    }
    
    public Transform setTranslation(Vector3f trans) {
        this.translation.set(trans);
        return this;
    }
    
    public Transform setTranslation(float x,float y, float z) {
        translation.set(x,y,z);
        return this;
    }

    public Vector3f getTranslation(Vector3f trans) {
        if (trans==null)
            trans=new Vector3f();
        trans.set(this.translation);
        return trans;
    }
    
    public Vector3f getTranslation() {
        return translation;
    }

    public Transform setScale(Vector3f scale) {
        this.scale.set(scale);
        return this;
    }

    public Transform setScale(float x, float y, float z) {
        scale.set(x,y,z);
        return this;
    }
    
    public Transform setScale(float scale) {
        this.scale.set(scale, scale, scale);
        return this;
    }

    public Vector3f getScale(Vector3f scale) {
        if (scale==null)
            scale=new Vector3f();
        scale.set(this.scale);
        return scale;
    }
    
    public Vector3f getScale() {
        return scale;
    }
    
    public Transform setRotation(Quaternion rot) {
        this.rot.set(rot);
        return this;
    }

    public Quaternion getRotation(Quaternion quat) {
        if (quat==null)
            quat=new Quaternion();
        quat.set(rot);
        return quat;
    }
    
    public Quaternion getRotation() {
        return rot;
    }
}
