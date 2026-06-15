package team.terrafirmgreg.fieldguide.export;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EntryOgImagesTest {

    @Test
    void loadsFromMeta() {
        Map<String, String> paths = EntryOgImages.fromMeta(Map.of(
                "entryOgImages",
                Map.of("beneath/piglins", "assets/icons/og/beneath/piglins.png")));
        assertEquals("assets/icons/og/beneath/piglins.png", paths.get("beneath/piglins"));
    }
}
