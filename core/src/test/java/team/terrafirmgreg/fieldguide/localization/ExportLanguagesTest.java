package team.terrafirmgreg.fieldguide.localization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportLanguagesTest {

    @AfterEach
    void cleanup() {
        System.clearProperty("fieldguide.exportLanguages");
    }

    @Test
    void allKeysMatchesLanguageEnum() {
        assertEquals(Language.values().length, ExportLanguages.allKeys().size());
        assertTrue(ExportLanguages.allKeys().contains("zh_cn"));
    }

    @Test
    void resolveConfiguredUsesPropertyWhenSet() {
        System.setProperty("fieldguide.exportLanguages", "en_us, zh_cn");
        assertEquals(Set.of("en_us", "zh_cn"), ExportLanguages.resolveConfigured());
    }

    @Test
    void toCsvPrintsStableList() {
        assertEquals("en_us,de_de,es_es", ExportLanguages.toCsv(java.util.List.of("en_us", "de_de", "es_es")));
    }
}
