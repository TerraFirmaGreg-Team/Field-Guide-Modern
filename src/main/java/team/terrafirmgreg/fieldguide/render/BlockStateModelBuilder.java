package team.terrafirmgreg.fieldguide.render;

import team.terrafirmgreg.fieldguide.export.ExportModelLoader;
import team.terrafirmgreg.fieldguide.data.minecraft.blockmodel.BlockModel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class BlockStateModelBuilder extends BaseModelBuilder {

    public BlockStateModelBuilder(ExportModelLoader assetLoader) {
        super(assetLoader);
    }

    @Override
    protected BlockModel loadModel(String modelId) {
        if (modelId.startsWith("#")) {
            List<String> blocks = assetLoader.loadBlockTag(modelId.substring(1));
            modelId = blocks.get(0); 
        }
        BlockModel blockModel = assetLoader.loadBlockModelWithState(modelId);
        if (!blockModel.hasElements()) {
            
            return new BlockModel(); 
        }
        return blockModel;
    }
}