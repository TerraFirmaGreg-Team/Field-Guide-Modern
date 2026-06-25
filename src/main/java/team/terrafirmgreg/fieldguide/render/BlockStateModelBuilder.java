package team.terrafirmgreg.fieldguide.render;

import team.terrafirmgreg.fieldguide.export.BlockStateLayer;
import team.terrafirmgreg.fieldguide.export.ExportModelLoader;
import team.terrafirmgreg.fieldguide.data.minecraft.blockmodel.BlockModel;
import team.terrafirmgreg.fieldguide.render3d.scene.Node;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class BlockStateModelBuilder extends BaseModelBuilder {

    public BlockStateModelBuilder(ExportModelLoader assetLoader) {
        super(assetLoader);
    }

    @Override
    public Node buildModel(String modelId) {
        if (modelId.startsWith("#")) {
            List<String> blocks = assetLoader.loadBlockTag(modelId.substring(1));
            if (blocks.isEmpty()) {
                return new Node();
            }
            modelId = blocks.get(0);
        }

        List<BlockStateLayer> layers = assetLoader.resolveBlockStateLayers(modelId);
        if (layers.isEmpty()) {
            return new Node();
        }

        Node root = new Node();
        for (BlockStateLayer layer : layers) {
            if (!layer.model().hasElements()) {
                continue;
            }
            Node layerNode = buildModel(layer.model());
            VariantRotationUtil.applyBlockVariantRotation(layerNode, layer.rotX(), layer.rotY(), layer.rotZ());
            root.attachChild(layerNode);
        }
        return root;
    }

    @Override
    protected BlockModel loadModel(String modelId) {
        if (modelId.startsWith("#")) {
            List<String> blocks = assetLoader.loadBlockTag(modelId.substring(1));
            if (blocks.isEmpty()) {
                return new BlockModel();
            }
            modelId = blocks.get(0);
        }
        BlockModel blockModel = assetLoader.loadBlockModelWithState(modelId);
        if (!blockModel.hasElements()) {
            return new BlockModel();
        }
        return blockModel;
    }
}
