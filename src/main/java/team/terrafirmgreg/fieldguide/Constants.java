package team.terrafirmgreg.fieldguide;

import java.util.Set;

public final class Constants {

    private Constants() {}

    public static final String CACHE = ".cache";
    public static final String MC_VERSION = "1.20.1";
    public static final String FORGE_VERSION = "47.4.13";

    public static final String EN_US = "en_us";

    public static final String FIELD_GUIDE = "field_guide";
    public static final String DEFAULT_BOOK_NAMESPACE = "tfc";
    public static final String DEFAULT_BOOK_ID = FIELD_GUIDE;

    public static final String BOOK_PATH = "data/%s/patchouli_books/%s/book.json";
    public static final String BOOK_CATEGORY_DIR = "assets/%s/patchouli_books/%s/%s/categories";
    public static final String BOOK_CATEGORY_PATH = "assets/%s/patchouli_books/%s/%s/categories/%s.json";
    public static final String BOOK_ENTRY_DIR = "assets/%s/patchouli_books/%s/%s/entries";
    public static final String BOOK_ENTRY_PATH = "assets/%s/patchouli_books/%s/%s/entries/%s.json";

    public static final Set<String> EXCLUDES_CATEGORIES = Set.of(
            "tfc_gurman"
    );

    public static final Set<String> EXCLUDES_ENTRIES = Set.of(
            "firmalife/more_fertilizer",
            "firmalife/stainless_steel",
            "beneath/ancient_altar",
            "beneath/crops",
            "beneath/list_of_sacrifices",
            "beneath/how_to_go_beneath",
            "beneath/burpflower",
            "mechanics/crankshaft",
            "mechanics/gems",
            "mechanics/mechanical_power",
            "mechanics/minecarts",
            "mechanics/pumps",
            "the_world/ores_and_minerals",
            "sns/lunchbox",
            "sns/mob_net",
            
            "gurman_beverages",
            "gurman_borscht",
            "gurman_cheese_making",
            "gurman_croissants",
            "gurman_intro",
            "gurman_kvass",
            "gurman_milking",
            "gurman_pelmeni",
            "gurman_pizza",
            "gurman_ramen"
    );

    public static String getBookPath(String namespace, String bookId) {
        return String.format(BOOK_PATH, namespace, bookId);
    }

    public static String getCategoryDir(String namespace, String bookId, String lang) {
        return String.format(BOOK_CATEGORY_DIR, namespace, bookId, lang);
    }

    public static String getCategoryPath(String namespace, String bookId, String lang, String categoryId) {
        return String.format(BOOK_CATEGORY_PATH, namespace, bookId, lang, categoryId);
    }

    public static String getEntryDir(String namespace, String bookId, String lang) {
        return String.format(BOOK_ENTRY_DIR, namespace, bookId, lang);
    }

    public static String getEntryPath(String namespace, String bookId, String lang, String entryId) {
        return String.format(BOOK_ENTRY_PATH, namespace, bookId, lang, entryId);
    }
}