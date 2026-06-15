package team.terrafirmgreg.fieldguide.data.patchouli;

import com.google.gson.annotations.SerializedName;
import team.terrafirmgreg.fieldguide.asset.Asset;
import team.terrafirmgreg.fieldguide.asset.AssetSource;
import team.terrafirmgreg.fieldguide.localization.Language;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Data
public class Book {

    private String name; 

    @SerializedName("landing_text")
    private String landingText; 

    @SerializedName("use_resource_pack")
    private Boolean useResourcePack;

    @SerializedName("book_texture")
    private String bookTexture;

    @SerializedName("filter_texture")
    private String filterTexture;

    @SerializedName("crafting_texture")
    private String craftingTexture;

    private String model;

    @SerializedName("text_color")
    private String textColor;

    @SerializedName("header_color")
    private String headerColor;

    @SerializedName("nameplate_color")
    private String nameplateColor;

    @SerializedName("link_color")
    private String linkColor;

    @SerializedName("link_hover_color")
    private String linkHoverColor;

    @SerializedName("progress_bar_color")
    private String progressBarColor;

    @SerializedName("progress_bar_background")
    private String progressBarBackground;

    @SerializedName("open_sound")
    private String openSound;

    @SerializedName("flip_sound")
    private String flipSound;

    @SerializedName("index_icon")
    private String indexIcon;

    private Boolean pamphlet;

    @SerializedName("show_progress")
    private Boolean showProgress;

    private String version;

    private String subtitle;

    @SerializedName("creative_tab")
    private String creativeTab;

    @SerializedName("advancements_tab")
    private String advancementsTab;

    @SerializedName("dont_generate_book")
    private Boolean dontGenerateBook;

    @SerializedName("custom_book_item")
    private String customBookItem;

    @SerializedName("show_toasts")
    private Boolean showToasts;

    @SerializedName("use_blocky_font")
    private Boolean useBlockyFont;

    private Boolean i18n;

    private Map<String, String> macros;

    @SerializedName("pause_game")
    private Boolean pauseGame;

    @SerializedName("text_overflow_mode")
    private String textOverflowMode;

    @SerializedName("extend")
    private String extend;

    @SerializedName("allow_extensions")
    private Boolean allowExtensions;

    private transient Language language;
    private transient AssetSource assetSource;
    private transient List<BookCategory> categories = new ArrayList<>();
    private transient Map<String, BookCategory> categoryMap = new TreeMap<>();
    private transient List<BookEntry> entries = new ArrayList<>();
    private transient Map<String, BookEntry> entryMap = new TreeMap<>();

    public void setAssetSource(Asset asset) {
        this.assetSource = asset.getSource();
    }

    public void addCategory(BookCategory category) {
        BookCategory exist = categoryMap.get(category.getId());
        if (exist != null) {
            log.debug("Override category: {}, {} -> {}", category.getId(), exist.getAssetSource(), category.getAssetSource());
        } else {
            categories.add(category);
            categoryMap.put(category.getId(), category);
        }
    }

    public void addEntry(BookEntry entry) {
        BookEntry exist = entryMap.get(entry.getId());
        if (exist != null) {
            log.debug("Override entry: {}, {} -> {}", entry.getId(), exist.getAssetSource(), entry.getAssetSource());
        } else {
            BookCategory category = categoryMap.get(entry.getCategoryId());
            if (category != null) {
                entries.add(entry);
                entryMap.put(entry.getId(), entry);
                category.addEntry(entry);
            } else {
                log.warn("Entry {} has an unknown category: {}", entry.getId(), entry.getCategoryId());
            }
        }
    }

    public void sort() {
        this.categories.sort(BookCategory::compareTo);
        for (BookCategory cat : this.categories) {
            cat.getEntries().sort(BookEntry::compareTo);
        }
    }

    public void report() {
        System.out.printf("===== Report %s =====\n", language);
        System.out.printf("Total: %d categories, %d entries\n", getCategories().size(), getEntries().size());
        for (BookCategory category : getCategories()) {
            System.out.printf("%s - <%s> (%d entries): %s\n", category.getId(), category.getName(), category.getEntries().size(), category.getAssetSource().getSourceId());
            for (BookEntry entry : category.getEntries()) {
                System.out.printf("  %s/%s - <%s> (%d pages): %s\n", entry.getCategoryId(), entry.getRelId(), entry.getName(), entry.getPages().size(), entry.getAssetSource().getSourceId());
            }
        }
    }
}