package team.terrafirmgreg.fieldguide.export;

import team.terrafirmgreg.fieldguide.data.minecraft.blockmodel.BlockModel;

public record BlockStateLayer(BlockModel model, int rotX, int rotY, int rotZ) {

    public boolean hasRotation() {
        return rotX != 0 || rotY != 0 || rotZ != 0;
    }
}
