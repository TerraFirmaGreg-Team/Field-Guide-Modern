package team.terrafirmgreg.fieldguide.data.patchouli.page;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PageMultiblock extends IPageWithText {

    private String name;

    @SerializedName("multiblock_id")
    private String multiblockId;

    private PageMultiblockData multiblock;

    @SerializedName("enable_visualize")
    private boolean enableVisualize;
}
