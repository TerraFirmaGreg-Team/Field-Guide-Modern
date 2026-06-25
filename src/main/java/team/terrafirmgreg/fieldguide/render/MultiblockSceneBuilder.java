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
        int sizeX = pattern[0].length;
        int sizeZ = pattern[0][0].length();
        log.debug("Model size: {}x{}x{} (patchouli x/y/z)", sizeX, height, sizeZ);

        float startX = -sizeX * 8f;
        float startY = -height * 8f;
        float startZ = -sizeZ * 8f;

        for (int y = 0; y < height; y++) {
            String[] layer = pattern[height - y - 1];
            for (int patchouliX = 0; patchouliX < sizeX; patchouliX++) {
                String line = layer[patchouliX];
                for (int patchouliZ = 0; patchouliZ < sizeZ; patchouliZ++) {
                    char c = line.charAt(patchouliZ);
                    if (c == ' ') {
                        continue;
                    }
                    String model = mapping.get(String.valueOf(c));
                    if (model == null || "AIR".equalsIgnoreCase(model) || "minecraft:air".equalsIgnoreCase(model)) {
                        continue;
                    }
                    Vector3f location = blockLocation(patchouliX, y, patchouliZ, startX, startY, startZ);
                    Node node = modelBuilder.buildModel(model);
                    node.getLocalTransform().getTranslation().addLocal(location);
                    root.attachChild(node);
                }
            }
        }
        return root;
    }

    static Vector3f blockLocation(
            int patchouliX, int patchouliY, int patchouliZ,
            float startX, float startY, float startZ) {
        return v3(
                patchouliX * 16 + startX,
                patchouliY * 16 + startY,
                patchouliZ * 16 + startZ);
    }
}
