package team.terrafirmgreg.fieldguide.render3d.scene;

import team.terrafirmgreg.fieldguide.render3d.math.Transform;

public abstract class Spatial {

    private Node parent;
    
    private Transform localTransform = new Transform();
    
    private Transform worldTransform = new Transform();
    
    public Transform getLocalTransform() {
        return localTransform;
    }

    public Transform getWorldTransform() {
        worldTransform.set(localTransform);
        if (parent != null) {
            
            worldTransform.combineWithParent(parent.getWorldTransform());
        }
        return worldTransform;
    }
    
    public void removeFromParent() {
        if (parent != null) {
            parent.detachChild(this);
        }
    }
    
    protected void setParent(Node newParent) {
        if (newParent == null) {
            removeFromParent();
        }
        this.parent = newParent;
    }
    
    public Node getParent() {
        return parent;
    }
    
}
