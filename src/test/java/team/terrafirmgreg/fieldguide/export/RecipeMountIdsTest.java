package team.terrafirmgreg.fieldguide.export;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RecipeMountIdsTest {

    @Test
    void readsRecipeMountIdsFromRefs() {
        Map<String, Object> meta = Map.of(
                "refs",
                Map.of(
                        "recipeMountIds",
                        Map.of(
                                "tfc:barrel/soaked_papyrus_strip",
                                "toomanyrecipeviewers:/tfc/barrel/soaked_papyrus_strip")));
        Map<String, String> mounts = RecipeMountIds.fromMeta(meta);
        assertEquals(
                "toomanyrecipeviewers:/tfc/barrel/soaked_papyrus_strip",
                mounts.get("tfc:barrel/soaked_papyrus_strip"));
    }

    @Test
    void emptyWhenMissing() {
        assertTrue(RecipeMountIds.fromMeta(Map.of()).isEmpty());
        assertTrue(RecipeMountIds.fromMeta(null).isEmpty());
    }
}
