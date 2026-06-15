package team.terrafirmgreg.fieldguide.render3d.scene;

import java.util.ArrayList;
import java.util.List;

public class Node extends Spatial {

    private List<Spatial> children;
    
    public Node() {
        children = new ArrayList<Spatial>();
    }
    
    public void attachChild(Spatial spatial) {
        children.add(spatial);
        spatial.setParent(this);
    }
    
    public void detachChild(Spatial spatial) {
        children.remove(spatial);
    }

    public List<Geometry> getGeometryList(List<Geometry> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        int len = children.size();
        for(int i=0; i<len; i++) {
            Spatial spatial = children.get(i);
            if (spatial instanceof Geometry) {
                list.add((Geometry) spatial);
            } else if (spatial instanceof Node) {
                
                Node node = (Node) spatial;
                node.getGeometryList(list);
            }
        }
        
        return list;
    }
}
