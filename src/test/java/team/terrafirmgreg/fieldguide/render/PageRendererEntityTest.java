package team.terrafirmgreg.fieldguide.render;

import org.junit.jupiter.api.Test;
import team.terrafirmgreg.fieldguide.data.patchouli.BookEntry;
import team.terrafirmgreg.fieldguide.data.patchouli.page.PageEntity;
import team.terrafirmgreg.fieldguide.export.EntityRenderResolver;
import team.terrafirmgreg.fieldguide.localization.Language;
import team.terrafirmgreg.fieldguide.localization.LocalizationManager;
import team.terrafirmgreg.fieldguide.site.emi.EmiRecipeIndex;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PageRendererEntityTest {

    @Test
    void entityPageRendersPreviewImage() {
        EntityRenderResolver resolver = EntityRenderResolver.fromMeta(Map.of(
                "entityRenders", Map.of(
                        "minecraft:piglin", Map.of(
                                "scale", 0.9,
                                "offset", 0.0,
                                "defaultRotation", -45.0,
                                "path", "assets/entities/minecraft/piglin.png",
                                "width", 256,
                                "height", 256))));

        PageRenderer renderer = new PageRenderer(
                null,
                stubL10n(),
                null,
                EmiRecipeIndex.load(Path.of("nonexistent-emi-dir-for-test")),
                Map.of(),
                resolver);

        BookEntry entry = new BookEntry();
        PageEntity page = new PageEntity();
        page.setEntityId("minecraft:piglin");
        page.setScale(0.9f);
        page.setName("Piglin");
        page.setText("You can find Piglins in the Beneath.");

        renderer.renderPage(entry, page);

        String html = String.join("", entry.getBuffer());
        assertTrue(html.contains("entity-preview-container"));
        assertTrue(html.contains("src=\"../../assets/entities/minecraft/piglin.png\""));
        assertTrue(html.contains("alt=\"Piglin\""));
        assertTrue(html.contains("You can find Piglins in the Beneath."));
    }

    @Test
    void entityPageFallsBackWhenPreviewMissing() {
        PageRenderer renderer = new PageRenderer(
                null,
                stubL10n(),
                null,
                EmiRecipeIndex.load(Path.of("nonexistent-emi-dir-for-test")),
                Map.of(),
                EntityRenderResolver.fromMeta(Map.of()));

        BookEntry entry = new BookEntry();
        PageEntity page = new PageEntity();
        page.setEntityId("minecraft:piglin");
        page.setName("Piglin");

        renderer.renderPage(entry, page);

        String html = String.join("", entry.getBuffer());
        assertFalse(html.contains("entity-preview-container"));
        assertTrue(html.contains("<code>minecraft:piglin</code>"));
        assertTrue(html.contains("View the field guide in Minecraft to see this entity."));
    }

    private static LocalizationManager stubL10n() {
        return new LocalizationManager() {
            @Override
            public void switchLanguage(Language lang) {}

            @Override
            public Language getCurrentLanguage() {
                return Language.EN_US;
            }

            @Override
            public String translate(String... keys) {
                return switch (keys[0]) {
                    case "field_guide.entity" -> "Entity";
                    case "field_guide.entity_only_in_game" ->
                            "View the field guide in Minecraft to see this entity.";
                    default -> keys[0];
                };
            }

            @Override
            public String translateWithArgs(String key, Object... args) {
                return key;
            }

            @Override
            public Map<String, String> getKeybindings() {
                return Map.of();
            }

            @Override
            public void lazyLoadNamespace(String namespace) {}
        };
    }
}
