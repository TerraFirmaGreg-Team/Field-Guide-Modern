package team.terrafirmgreg.fieldguide.export;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import team.terrafirmgreg.fieldguide.data.patchouli.page.PageEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EntityRenderResolverTest {

    @Test
    void resolvesFromEntityIdMap(@TempDir Path root) throws Exception {
        writeMeta(root, """
                {
                  "entityRenders": {
                    "minecraft:piglin": {
                      "scale": 0.9,
                      "offset": 0.0,
                      "defaultRotation": -45.0,
                      "path": "assets/entities/minecraft/piglin.png",
                      "width": 256,
                      "height": 256
                    }
                  }
                }
                """);

        EntityRenderResolver resolver = EntityRenderResolver.load(root);
        PageEntity page = new PageEntity();
        page.setEntityId("minecraft:piglin");
        page.setScale(0.9f);

        EntityRenderRecord record = resolver.resolve(page).orElseThrow();
        assertEquals("minecraft:piglin", record.entity());
        assertEquals("assets/entities/minecraft/piglin.png", record.path());
    }

    @Test
    void resolvesNbtEntityKey(@TempDir Path root) throws Exception {
        String entity = "tfc:dog{NoAI:1b,birth:-100000000L,geneticSize:16}";
        writeMeta(root, """
                {
                  "entityRenders": {
                    "%s": {
                      "scale": 0.7,
                      "offset": 0.0,
                      "defaultRotation": -45.0,
                      "path": "assets/entities/tfc/dog/abc123.png",
                      "width": 256,
                      "height": 256
                    }
                  }
                }
                """.formatted(entity));

        EntityRenderResolver resolver = EntityRenderResolver.load(root);
        PageEntity page = new PageEntity();
        page.setEntityId(entity);
        page.setScale(0.7f);

        EntityRenderRecord record = resolver.resolve(page).orElseThrow();
        assertEquals("assets/entities/tfc/dog/abc123.png", record.path());
    }

    @Test
    void supportsLegacyArrayFormat(@TempDir Path root) throws Exception {
        writeMeta(root, """
                {
                  "entityRenders": [
                    {
                      "entity": "minecraft:cow",
                      "scale": 1.0,
                      "offset": 0.0,
                      "defaultRotation": -45.0,
                      "path": "assets/entities/minecraft/cow.png",
                      "width": 256,
                      "height": 256
                    }
                  ]
                }
                """);

        EntityRenderResolver resolver = EntityRenderResolver.load(root);
        PageEntity page = new PageEntity();
        page.setEntityId("minecraft:cow");

        assertTrue(resolver.resolve(page).isPresent());
    }

    @Test
    void emptyWhenMetaMissing() {
        EntityRenderResolver resolver = EntityRenderResolver.fromMeta(Map.of());
        PageEntity page = new PageEntity();
        page.setEntityId("minecraft:piglin");
        assertTrue(resolver.resolve(page).isEmpty());
    }

    private static void writeMeta(Path root, String json) throws Exception {
        Files.writeString(root.resolve("meta.json"), json);
    }
}
