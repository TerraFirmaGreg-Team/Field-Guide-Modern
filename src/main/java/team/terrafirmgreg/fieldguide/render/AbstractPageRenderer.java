package team.terrafirmgreg.fieldguide.render;

import lombok.extern.slf4j.Slf4j;
import team.terrafirmgreg.fieldguide.data.patchouli.BookEntry;
import team.terrafirmgreg.fieldguide.data.patchouli.BookPage;
import team.terrafirmgreg.fieldguide.data.patchouli.page.*;
import team.terrafirmgreg.fieldguide.data.tfc.page.*;
import team.terrafirmgreg.fieldguide.export.EntityRenderResolver;
import team.terrafirmgreg.fieldguide.localization.LocalizationManager;

import java.util.List;
import java.util.Map;

/**
 * Shared Patchouli page dispatch for HTML and MDX renderers.
 * Subclasses implement output-specific hooks only.
 */
@Slf4j
public abstract class AbstractPageRenderer implements PageRenderer {

    protected final TextureRenderer textureRenderer;
    protected final LocalizationManager localizationManager;
    protected final EntityRenderResolver entityRenders;
    protected Map<String, String> bookMacros = Map.of();

    protected AbstractPageRenderer(
            LocalizationManager localizationManager,
            TextureRenderer textureRenderer,
            EntityRenderResolver entityRenders) {
        this.localizationManager = localizationManager;
        this.textureRenderer = textureRenderer;
        this.entityRenders = entityRenders != null ? entityRenders : EntityRenderResolver.fromMeta(Map.of());
    }

    @Override
    public void setBookMacros(Map<String, String> bookMacros) {
        this.bookMacros = bookMacros != null ? Map.copyOf(bookMacros) : Map.of();
    }

    @Override
    public final void renderPage(BookEntry entry, BookPage page) {
        String anchor = page.getAnchor();
        if (anchor != null && !anchor.isBlank()) {
            emitPageAnchor(entry, anchor);
        }

        List<String> buffer = entry.getBuffer();
        if (page instanceof PageLink pageLink) {
            renderPageLink(entry, buffer, pageLink);
        } else if (page instanceof PageText pageText) {
            formatTitle(entry, buffer, pageText.getTitle());
            formatText(entry, buffer, pageText.getText());
        } else if (page instanceof PageImage pageImage) {
            formatTitle(entry, buffer, pageImage.getTitle());
            renderImagePage(entry, buffer, pageImage);
            formatCenteredText(entry, buffer, pageImage.getText());
        } else if (page instanceof PageCrafting pageCrafting) {
            formatTitle(entry, buffer, pageCrafting.getTitle());
            emitDoubleRecipePage(buffer, pageCrafting);
            formatText(entry, buffer, pageCrafting.getText());
        } else if (page instanceof PageSpotlight pageSpotlight) {
            renderSpotlightPage(entry, buffer, pageSpotlight);
            formatText(entry, buffer, pageSpotlight.getText());
        } else if (page instanceof PageEntity pageEntity) {
            formatTitle(entry, buffer, pageEntity.getName());
            renderEntityPage(buffer, pageEntity);
            formatCenteredText(entry, buffer, pageEntity.getText());
        } else if (page instanceof PageEmpty) {
            emitEmptyPage(buffer);
        } else if (page instanceof PageMultiblock pageMultiblock) {
            formatTitle(entry, buffer, pageMultiblock.getName());
            renderMultiblockPage(buffer, pageMultiblock);
            formatCenteredText(entry, buffer, pageMultiblock.getText());
        } else if (page instanceof PageMultiMultiblock pageMultiMultiblock) {
            renderMultiMultiblockPage(buffer, pageMultiMultiblock);
            formatCenteredText(entry, buffer, pageMultiMultiblock.getText());
        } else if (page instanceof PageHeating pageHeating) {
            emitDoubleRecipePage(buffer, pageHeating);
            formatText(entry, buffer, pageHeating.getText());
        } else if (page instanceof PageQuern pageQuern) {
            emitDoubleRecipePage(buffer, pageQuern);
            formatText(entry, buffer, pageQuern.getText());
        } else if (page instanceof PageLoom pageLoom) {
            emitDoubleRecipePage(buffer, pageLoom);
            formatText(entry, buffer, pageLoom.getText());
        } else if (page instanceof PageAnvil pageAnvil) {
            emitDoubleRecipePage(buffer, pageAnvil);
            formatText(entry, buffer, pageAnvil.getText());
        } else if (page instanceof PageBetterAnvil pageBetterAnvil) {
            emitBetterAnvilPage(buffer, pageBetterAnvil);
            if (pageBetterAnvil.getText4() != null && !pageBetterAnvil.getText4().isBlank()) {
                formatText(entry, buffer, pageBetterAnvil.getText4());
            }
        } else if (page instanceof PageGlassworking pageGlassworking) {
            emitDoubleRecipePage(buffer, pageGlassworking);
            formatText(entry, buffer, pageGlassworking.getText());
        } else if (page instanceof PageSmelting pageSmelting) {
            emitDoubleRecipePage(buffer, pageSmelting);
            formatText(entry, buffer, pageSmelting.getText());
        } else if (page instanceof PageDrying pageDrying) {
            emitDoubleRecipePage(buffer, pageDrying);
            formatText(entry, buffer, pageDrying.getText());
        } else if (page instanceof PageBarrel pageBarrel) {
            emitDoubleRecipePage(buffer, pageBarrel);
            formatText(entry, buffer, pageBarrel.getText());
        } else if (page instanceof PageWelding pageWelding) {
            emitDoubleRecipePage(buffer, pageWelding);
            formatText(entry, buffer, pageWelding.getText());
        } else if (page instanceof PageRockKnapping pageRockKnapping) {
            emitRecipeList(buffer, pageRockKnapping.getRecipes());
            formatText(entry, buffer, pageRockKnapping.getText());
        } else if (page instanceof PageKnapping pageKnapping) {
            emitDoubleRecipePage(buffer, pageKnapping);
            formatText(entry, buffer, pageKnapping.getText());
        } else if (page instanceof PageTable pageTable) {
            renderTablePage(entry, buffer, pageTable);
        } else {
            log.warn("Unrecognized page type: {}, {}", page.getType(), page);
            onUnrecognizedPage(entry, buffer, page);
        }
    }

    protected final void emitDoubleRecipePage(List<String> buffer, IPageDoubleRecipe page) {
        emitRecipe(buffer, page.getRecipe());
        if (page.getRecipe2() != null && !page.getRecipe2().isBlank()) {
            emitRecipe(buffer, page.getRecipe2());
        }
    }

    protected final void emitRecipeList(List<String> buffer, List<String> recipeIds) {
        if (recipeIds == null) {
            return;
        }
        for (String id : recipeIds) {
            emitRecipe(buffer, id);
        }
    }

    protected final void emitBetterAnvilPage(List<String> buffer, PageBetterAnvil page) {
        for (String id : List.of(page.getRecipe(), page.getRecipe2(), page.getRecipe3(), page.getRecipe4())) {
            if (id == null || id.isBlank()) {
                continue;
            }
            emitRecipe(buffer, id);
        }
    }

    protected static String entityAltText(PageEntity page) {
        if (page.getName() != null && !page.getName().isBlank()) {
            return page.getName();
        }
        String entityId = page.getEntityId();
        if (entityId == null) {
            return "Entity";
        }
        int brace = entityId.indexOf('{');
        return brace > 0 ? entityId.substring(0, brace) : entityId;
    }

    protected abstract void emitPageAnchor(BookEntry entry, String anchor);

    protected abstract void emitEmptyPage(List<String> buffer);

    protected abstract void formatTitle(BookEntry entry, List<String> buffer, String title);

    protected abstract void formatText(BookEntry entry, List<String> buffer, String text);

    protected abstract void formatCenteredText(BookEntry entry, List<String> buffer, String text);

    protected abstract void renderPageLink(BookEntry entry, List<String> buffer, PageLink pageLink);

    protected abstract void renderImagePage(BookEntry entry, List<String> buffer, PageImage pageImage);

    protected abstract void emitRecipe(List<String> buffer, String recipeId);

    protected abstract void renderSpotlightPage(BookEntry entry, List<String> buffer, PageSpotlight page);

    protected abstract void renderEntityPage(List<String> buffer, PageEntity page);

    protected abstract void renderMultiblockPage(List<String> buffer, PageMultiblock page);

    protected abstract void renderMultiMultiblockPage(List<String> buffer, PageMultiMultiblock page);

    protected abstract void renderTablePage(BookEntry entry, List<String> buffer, PageTable page);

    protected abstract void onUnrecognizedPage(BookEntry entry, List<String> buffer, BookPage page);
}
