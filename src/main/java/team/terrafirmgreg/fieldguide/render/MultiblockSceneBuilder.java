package team.terrafirmgreg.fieldguide.render;

import team.terrafirmgreg.fieldguide.render3d.math.Vector3f;
import team.terrafirmgreg.fieldguide.render3d.scene.Node;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static team.terrafirmgreg.fieldguide.render.BaseModelBuilder.v3;

@Slf4j
public class MultiblockSceneBuilder {

    private final BaseModelBuilder modelBuilder;

    public MultiblockSceneBuilder(BaseModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
    }

    public Node buildMultiblock(String[][] pattern, Map<String, String> mapping) {
        Node root = new Node();
        int height = pattern.length;
        int col = pattern[0].length;
        int row = pattern[0][0].length();
        log.debug("Model size: {}x{}x{}", col, height, row);

        float startX = -row * 8f;
        float startY = -height * 8f;
        float startZ = -col * 8f;

        for (int y = 0; y < height; y++) {
            String[] layer = pattern[height - y - 1];
            for (int z = 0; z < col; z++) {
                String line = layer[z];
                for (int x = 0; x < row; x++) {
                    char c = line.charAt(x);
                    if (c == ' ') {
                        continue;
                    }
                    String model = mapping.get(String.valueOf(c));
                    if (model == null || "AIR".equalsIgnoreCase(model) || "minecraft:air".equalsIgnoreCase(model)) {
                        continue;
                    }
                    Vector3f location = v3(x * 16 + startX, y * 16 + startY, z * 16 + startZ);
                    Node node = modelBuilder.buildModel(model);
                    node.getLocalTransform().setTranslation(location);
                    root.attachChild(node);
                }
            }
        }
        return root;
    }
}
