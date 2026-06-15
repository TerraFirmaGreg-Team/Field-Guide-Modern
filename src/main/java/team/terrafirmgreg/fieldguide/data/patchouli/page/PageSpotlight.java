package team.terrafirmgreg.fieldguide.data.patchouli.page;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import team.terrafirmgreg.fieldguide.gson.PageSpotlightItemAdapter;
import lombok.Data;

import java.util.List;

@Data
public class PageSpotlight extends IPageWithText {

    @JsonAdapter(PageSpotlightItemAdapter.class)
    private List<PageSpotlightItem> item;

    private String title;

    @SerializedName("link_recipe")
    private Boolean linkRecipe;
}
