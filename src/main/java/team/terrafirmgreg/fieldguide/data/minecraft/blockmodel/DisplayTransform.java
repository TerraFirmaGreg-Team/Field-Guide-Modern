package team.terrafirmgreg.fieldguide.data.minecraft.blockmodel;

import lombok.Data;

@Data
public class DisplayTransform {
    private double[] translation = {0, 0, 0};
    private double[] rotation = {0, 0, 0};
    private double[] scale = {1, 1, 1};
}
