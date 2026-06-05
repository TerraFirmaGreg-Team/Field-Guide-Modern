package team.terrafirmgreg.fieldguide.localization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportLanguagesTest {

    @TempDir
    Path tempDir;

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

    @Test
    void mainWritesWholeCsvToFile() throws Exception {
        Path out = tempDir.resolve("export-languages.txt");
        ExportLanguages.main(new String[] { out.toString() });
        String csv = Files.readString(out).replace("\r", "").replace("\n", "");
        assertEquals(ExportLanguages.toCsv(ExportLanguages.allKeys()), csv);
    }
}
