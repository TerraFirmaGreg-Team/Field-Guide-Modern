package team.terrafirmgreg.fieldguide.data.minecraft.blockmodel;

import lombok.Data;

@Data
public class ElementRotation {
    private double[] origin;
    private String axis;
    private Double angle;
    private Boolean rescale = false;
}
