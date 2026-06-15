package team.terrafirmgreg.fieldguide.data.patchouli;

import com.google.gson.annotations.SerializedName;
import team.terrafirmgreg.fieldguide.asset.Asset;
import team.terrafirmgreg.fieldguide.asset.AssetSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class BookEntry implements Comparable<BookEntry> {
    
    private String name = "";
    
    private String category = "";

    private String icon = "";

    private List<BookPage> pages;

    private String advancement = "";

    private String flag = "";

    private Boolean priority = false;

    private Boolean secret = false;

    @SerializedName("sortnum")
    private int sort = 0;

    private String turnin;

    @SerializedName("extra_recipe_mappings")
    private Map<String, Integer> extraRecipeMappings;

    @SerializedName("entry_color")
    private String entryColor;

    private transient String id;    
    private transient String relId; 
    private transient String categoryId;

    private transient String iconPath = "";
    
    private transient String ogImagePath = "";
    private transient String iconName = "";
    
    private transient String iconHeaderHtml = "";
    
    private transient String iconCardHtml = "";

    private transient AssetSource assetSource;

    private transient List<String> buffer = new ArrayList<>();
    private transient List<Map<String, String>> searchTree = new ArrayList<>();
    private transient boolean isRendered = false;
    private transient String innerHtml;

    @Override
    public String toString() {
        return id + "@" + assetSource.getSourceId();
    }

    @Override
    public int compareTo(BookEntry other) {
        if (this.sort != other.sort) {
            return Integer.compare(this.sort, other.sort);
        }
        return this.id.compareTo(other.id);
    }

    public void setAssetSource(String entryPath, Asset asset) {
        this.assetSource = asset.getSource();

        String relativePath = asset.getPath().substring(entryPath.length() + 1);
        String entryId = relativePath.substring(0, relativePath.lastIndexOf('.'));
        this.id = entryId;

        int index = this.category.indexOf(':');
        if (index > 0) {
            this.categoryId = this.category.substring(index + 1);
        } else {
            this.categoryId = this.category;
        }

        if (entryId.contains("/")) {
            this.relId = entryId.split("/")[1];
        } else {
            this.relId = entryId;
        }
    }

    public void addSearchContent(String content) {
        Map<String, String> searchData = new HashMap<>();
        searchData.put("content", content);
        searchData.put("entry", name);
        searchData.put("url", "./" + categoryId + "/" + relId + ".html");
        this.searchTree.add(searchData);
    }
}