package team.terrafirmgreg.fieldguide.render3d.scene;

import team.terrafirmgreg.fieldguide.render3d.math.Vector2f;
import team.terrafirmgreg.fieldguide.render3d.math.Vector3f;
import team.terrafirmgreg.fieldguide.render3d.math.Vector4f;

public class Mesh {
    
    protected Vertex[] vertexes;
    
    protected int[] indexes;

    public Vertex[] getVertexes() {
        return vertexes;
    }
    
    public int[] getIndexes() {
        return indexes;
    }
    
    public Mesh() {
    }
    
    public Mesh(Vector3f[] positions, int[] indexes) {
        this(positions, indexes, null, null, null);
    }
    
    public Mesh(Vector3f[] positions, int[] indexes, Vector2f[] texCoords, Vector3f[] normals, Vector4f[] colors) {
        this.indexes = indexes;
        this.vertexes = new Vertex[positions.length];
        for(int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            vertexes[index] = new Vertex();
            vertexes[index].position = positions[index];
            if (normals != null) {
                vertexes[index].normal = normals[index];
            }
            if (colors != null) {
                vertexes[index].color = colors[index];
            }
            if (texCoords != null) {
                vertexes[index].texCoord = texCoords[index];
            }
        }
    }
}