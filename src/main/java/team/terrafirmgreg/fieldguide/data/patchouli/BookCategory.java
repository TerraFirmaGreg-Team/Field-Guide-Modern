package team.terrafirmgreg.fieldguide.data.patchouli;

import com.google.gson.annotations.SerializedName;
import team.terrafirmgreg.fieldguide.asset.Asset;
import team.terrafirmgreg.fieldguide.asset.AssetSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Data
public class BookCategory implements Comparable<BookCategory> {

    private String id;
    
    private String name;
    
    private String description;
    
    private String icon;
    
    private String parent;
    
    private String flag;
    
    @SerializedName("sortnum")
    private int sort = 0;

    private boolean secret = false;

    private transient AssetSource assetSource;
    private transient List<BookEntry> entries = new ArrayList<>();
    private transient Map<String, BookEntry> entryMap = new TreeMap<>();

    public void setAssetSource(String categoryPath, Asset asset) {
        this.assetSource = asset.getSource();

        String assetPath = asset.getPath();
        try {
            String relativePath = assetPath.substring(categoryPath.length() + 1);
            this.id = relativePath.substring(0, relativePath.lastIndexOf('.'));
        } catch (Exception e) {
            log.error("Failed to parse category id for: {} -> {}", categoryPath, assetPath, e);
        }
    }

    public void addEntry(BookEntry entry) {
        this.entries.add(entry);
        this.entryMap.put(entry.getId(), entry);
    }

    @Override
    public String toString() {
        return id + "@" + assetSource.getSourceId();
    }

    @Override
    public int compareTo(BookCategory other) {
        if (this.sort != other.sort) {
            return Integer.compare(this.sort, other.sort);
        }
        return this.id.compareTo(other.id);
    }
}