package team.terrafirmgreg.fieldguide.render;

import team.terrafirmgreg.fieldguide.export.EntityRenderResolver;
import team.terrafirmgreg.fieldguide.export.ExportModelLoader;
import team.terrafirmgreg.fieldguide.asset.ItemImageResult;
import team.terrafirmgreg.fieldguide.data.patchouli.BookEntry;
import team.terrafirmgreg.fieldguide.data.patchouli.BookPage;
import team.terrafirmgreg.fieldguide.data.patchouli.page.*;
import team.terrafirmgreg.fieldguide.data.tfc.page.*;
import team.terrafirmgreg.fieldguide.exception.InternalException;
import team.terrafirmgreg.fieldguide.gson.JsonUtils;
import team.terrafirmgreg.fieldguide.localization.I18n;
import team.terrafirmgreg.fieldguide.localization.LocalizationManager;
import team.terrafirmgreg.fieldguide.site.emi.EmiRecipeIndex;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.*;
import java.util.List;

import static team.terrafirmgreg.fieldguide.render.ImageTemplates.IMAGE_SINGLE;

@Slf4j
public class HtmlPageRenderer extends AbstractPageRenderer {

    private final ExportModelLoader assetLoader;
    private final EmiRecipeIndex emiRecipes;
    private final Map<String, String> recipeMountIds;

    private int id = 0;

    public HtmlPageRenderer(
            ExportModelLoader loader,
            LocalizationManager localizationManager,
            TextureRenderer textureRenderer,
            EmiRecipeIndex emiRecipes,
            Map<String, String> recipeMountIds,
            EntityRenderResolver entityRenders) {
        super(localizationManager, textureRenderer, entityRenders);
        this.assetLoader = loader;
        this.emiRecipes = emiRecipes;
        this.recipeMountIds = recipeMountIds == null ? Map.of() : Map.copyOf(recipeMountIds);
    }

    @Override
    protected void emitPageAnchor(BookEntry entry, String anchor) {
        entry.getBuffer().add(String.format("<a class=\"anchor\" id=\"%s\"></a>", anchor));
    }

    @Override
    protected void emitEmptyPage(List<String> buffer) {
        buffer.add("<hr>");
    }

    @Override
    protected void formatTitle(BookEntry entry, List<String> buffer, String title) {
        if (title != null && !title.isEmpty()) {
            String stripped = TextFormatter.stripVanillaFormatting(title);
            buffer.add("<h5>" + stripped + "</h5>\n");
            entry.addSearchContent(stripped);
        }
    }

    @Override
    protected void formatText(BookEntry entry, List<String> buffer, String text) {
        if (text != null && !text.isEmpty()) {
            TextFormatter.formatText(buffer, text, localizationManager, bookMacros);
            entry.addSearchContent(TextFormatter.searchStrip(text));
        }
    }

    @Override
    protected void formatCenteredText(BookEntry entry, List<String> buffer, String text) {
        buffer.add("<div style=\"text-align: center;\">");
        formatText(entry, buffer, text);
        buffer.add("</div>");
    }

    @Override
    protected void renderPageLink(BookEntry entry, List<String> buffer, PageLink pageLink) {
        formatText(entry, buffer, pageLink.getText());
        emitExternalLink(buffer, pageLink.getLinkText(), pageLink.getUrl());
    }

    @Override
    protected void renderImagePage(BookEntry entry, List<String> buffer, PageImage pageImage) {
        renderImagePage(buffer, pageImage.getImages());
    }

    @Override
    protected void emitRecipe(List<String> buffer, String recipeId) {
        formatRecipe(buffer, recipeId);
    }

    @Override
    protected void renderSpotlightPage(BookEntry entry, List<String> buffer, PageSpotlight page) {
        parseSpotlightPage(entry, buffer, page);
    }

    @Override
    protected void renderEntityPage(List<String> buffer, PageEntity page) {
        parseEntityPage(buffer, page);
    }

    @Override
    protected void renderMultiblockPage(List<String> buffer, PageMultiblock page) {
        parseMultiblockPage(buffer, page);
    }

    @Override
    protected void renderMultiMultiblockPage(List<String> buffer, PageMultiMultiblock page) {
        parseMultiMultiblockPage(buffer, page);
    }

    @Override
    protected void renderTablePage(BookEntry entry, List<String> buffer, PageTable page) {
        parseTablePage(entry, buffer, page);
    }

    @Override
    protected void onUnrecognizedPage(BookEntry entry, List<String> buffer, BookPage page) {
    }

    public void formatTitleWithIcon(BookEntry entry, List<String> buffer, ItemImageResult icon, String title) {
        formatTitleWithIcon(entry, buffer, icon, title, "h5", null);
    }

    public void formatTitleWithIcon(BookEntry entry, List<String> buffer, ItemImageResult icon,
                                    String inTitle, String tag,
                                    String tooltip) {
        String title = icon.getName();
        if (inTitle != null && !inTitle.isEmpty()) {
            title = TextFormatter.stripVanillaFormatting(inTitle);
        }
        if (tooltip == null) {
            tooltip = title != null ? title : "";
        }
        if (title != null && !title.isEmpty()) {
            entry.addSearchContent(title);
        }

        String iconHtml = IconMarkup.img(icon, "item-header-icon");
        String html = String.format("""
            <div class="item-header">
                <span href="#" data-bs-toggle="tooltip" title="%s">
                    %s
                </span>
                <%s>%s</%s>
            </div>
            """, tooltip, iconHtml, tag, title, tag);

        buffer.add(html);
    }

    public void formatTitleWithIcon(BookEntry entry, List<String> buffer, String iconSrc, String iconName,
                                    String inTitle, String tag,
                                    String tooltip) {
        formatTitleWithIcon(
                entry,
                buffer,
                ItemImageResult.legacy(iconSrc, iconName, null),
                inTitle,
                tag,
                tooltip);
    }

    public void formatTitleWithIcon(BookEntry entry, List<String> buffer, String iconSrc, String iconName, String title) {
        formatTitleWithIcon(entry, buffer, iconSrc, iconName, title, "h5", null);
    }

    public void formatWithTooltip(List<String> buffer, String text, String tooltip) {
        String html = String.format("""
            <div style="text-align: center;">
                <p class="text-muted"><span href="#" data-bs-toggle="tooltip" title="%s">%s</span></p>
            </div>
            """, tooltip, text);
        buffer.add(html);
    }

    public void formatRecipe(List<String> buffer, String recipeId) {
        if (recipeId == null || recipeId.isEmpty()) {
            return;
        }
        String mountId = recipeMountIds.getOrDefault(recipeId, recipeId);
        if (emiRecipes != null && !emiRecipes.isEmpty() && !emiRecipes.contains(mountId)) {
            String tmrvFallback = tmrvRecipeId(recipeId);
            if (tmrvFallback != null && emiRecipes.contains(tmrvFallback)) {
                mountId = tmrvFallback;
            }
        }
        if (emiRecipes != null && !emiRecipes.isEmpty() && !emiRecipes.contains(mountId)) {
            log.debug("Recipe not in EMI export, using in-game-only fallback: {} (mount={})", recipeId, mountId);
            String text = String.format(
                    "%s: <code>%s</code>",
                    localizationManager.translate(I18n.RECIPE),
                    escapeHtmlText(recipeId));
            formatWithTooltip(buffer, text, localizationManager.translate(I18n.RECIPE_ONLY_IN_GAME));
            return;
        }
        buffer.add(String.format(
                "<div class=\"emi-recipe my-2\" data-recipe-id=\"%s\"></div>",
                escapeHtmlAttr(mountId)));
    }

    private void emitExternalLink(List<String> buffer, String linkText, String url) {
        if (url == null || url.isBlank() || linkText == null || linkText.isBlank()) {
            return;
        }
        String label = TextFormatter.stripVanillaFormatting(linkText);
        buffer.add(String.format(
                "<p><a href=\"%s\" target=\"_blank\" rel=\"noopener noreferrer\">%s<span class=\"patchouli-external-link\" aria-hidden=\"true\">↪</span></a></p>\n",
                escapeHtmlAttr(url),
                escapeHtmlText(label)));
    }

    private static String tmrvRecipeId(String handbookRecipeId) {
        if (handbookRecipeId == null || handbookRecipeId.isBlank() || handbookRecipeId.indexOf(':') <= 0) {
            return null;
        }
        return "toomanyrecipeviewers:/" + handbookRecipeId.replace(':', '/');
    }

    private static String escapeHtmlText(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String escapeHtmlAttr(String value) {
        return value.replace("&", "&amp;").replace("\"", "&quot;");
    }

    private void renderImagePage(List<String> buffer, List<String> images) {
        List<Map.Entry<String, String>> processedImages = new ArrayList<>();

        if (images != null) {
            for (String image : images) {
                try {
                    String convertedImage = textureRenderer.convertImage(image);
                    processedImages.add(Map.entry(image, convertedImage));
                } catch (InternalException e) {
                    log.error("Failed to convert entry image: {}", image, e);
                }
            }
        }

        if (processedImages.size() == 1) {
            Map.Entry<String, String> imageEntry = processedImages.get(0);
            buffer.add(String.format(IMAGE_SINGLE,
                    imageEntry.getValue(), imageEntry.getKey()));
        } else if (!processedImages.isEmpty()) {
            String uid = String.valueOf(id++);
            StringBuilder parts = new StringBuilder();
            StringBuilder seq = new StringBuilder();

            for (int i = 0; i < processedImages.size(); i++) {
                Map.Entry<String, String> imageEntry = processedImages.get(i);
                String active = i == 0 ? "active" : "";
                parts.append(String.format(ImageTemplates.IMAGE_MULTIPLE_PART, active, imageEntry.getValue(), imageEntry.getKey()));

                if (i > 0) {
                    seq.append(String.format(ImageTemplates.IMAGE_MULTIPLE_SEQ, uid, i, i + 1));
                }
            }

            buffer.add(MessageFormat.format(ImageTemplates.IMAGE_MULTIPLE, uid, seq.toString(), parts.toString()));
        }
    }

    private void parseSpotlightPage(BookEntry entry, List<String> buffer, PageSpotlight page) {
        List<PageSpotlightItem> items = page.getItem();
        if (items == null || items.isEmpty()) {
            log.warn("Spotlight page did not have an item or tag key: {}", page);
            return;
        }
        try {
            for (PageSpotlightItem item : items) {
                if ("tag".equals(item.getType())) {
                    String tagId = item.getText();
                    ItemImageResult itemResult = textureRenderer.getItemImage("#" + tagId, false);
                    formatTitleWithIcon(entry, buffer, itemResult, page.getTitle());
                } else {
                    ItemImageResult itemResult = textureRenderer.getItemImage(item.getText(), false);
                    formatTitleWithIcon(entry, buffer, itemResult, page.getTitle());
                }
            }
        } catch (Exception e) {
            formatTitle(entry, buffer, page.getTitle());

            int count = 0;
            StringBuilder sb = new StringBuilder();
            for (PageSpotlightItem item : items) {
                if (count > 0) {
                    sb.append(", ");
                }
                sb.append("<code>");
                if ("tag".equals(item.getType())) {
                    sb.append('#').append(item.getText());
                } else {
                    sb.append(item.getText());
                }
                sb.append("</code>");
                count++;
            }
            String itemHtml = String.format("%s: %s", localizationManager.translate(count > 1 ? I18n.ITEMS : I18n.ITEM), sb);
            formatWithTooltip(buffer, itemHtml, localizationManager.translate(I18n.ITEM_ONLY_IN_GAME));
        }
    }

    private void parseEntityPage(List<String> buffer, PageEntity page) {
        entityRenders.resolve(page).ifPresentOrElse(
                record -> buffer.add(String.format("""
                        <div class="entity-preview-container">
                            <img class="entity-preview d-block mx-auto"
                                 src="../../%s"
                                 width="%d"
                                 height="%d"
                                 alt="%s"
                                 loading="lazy">
                        </div>
                        """,
                        record.path(),
                        record.width(),
                        record.height(),
                        escapeHtml(entityAltText(page)))),
                () -> {
                    String entityId = page.getEntityId() != null ? page.getEntityId() : "unknown";
                    formatWithTooltip(buffer,
                            String.format("%s: <code>%s</code>",
                                    localizationManager.translate(I18n.ENTITY),
                                    escapeHtml(entityId)),
                            localizationManager.translate(I18n.ENTITY_ONLY_IN_GAME));
                });
    }

    private static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private void parseMultiblockPage(List<String> buffer, PageMultiblock page) {
        try {
            String glbPath = textureRenderer.generateMultiblockGLB(page);
            if (glbPath != null && glbPath.endsWith(".glb")) {
                String viewerId = generateUniqueViewerId("multiblock");
                buffer.add(String.format("""
                    <div class="glb-viewer-container">
                        <div id="%s" 
                             class="glb-viewer" 
                             data-glb-viewer="../../%s"
                             data-viewer-type="multiblock"
                             data-auto-rotate="true"
                             data-auto-load="false">
                            <div class="glb-viewer-loading">
                                <div class="spinner-border" role="status">
                                    <span class="visually-hidden">Loading 3D model...</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    """,
                    viewerId,
                    glbPath));
            }
        } catch (Exception e) {
            if (page.getMultiblockId() != null) {
                formatWithTooltip(buffer,
                        String.format("%s: <code>%s</code>", localizationManager.translate(I18n.MULTIBLOCK), page.getMultiblockId()),
                        localizationManager.translate(I18n.MULTIBLOCK_ONLY_IN_GAME));
            } else {
                formatWithTooltip(buffer,
                        String.format("%s: <code>%s</code>", localizationManager.translate(I18n.MULTIBLOCK), JsonUtils.toJson(page.getMultiblock())),
                        localizationManager.translate(I18n.MULTIBLOCK_ONLY_IN_GAME));
            }
        }
    }

    private String generateUniqueViewerId(String prefix) {
        return String.format("glb-viewer-%s-%d-%d", prefix, System.currentTimeMillis(), id++);
    }

    private void parseMultiMultiblockPage(List<String> buffer, PageMultiMultiblock page) {
        try {
            List<String> glbPaths = textureRenderer.generateMultiMultiblockGLB(page);

            if (!glbPaths.isEmpty()) {
                String viewerId = generateUniqueViewerId("multimultiblock");
                StringBuilder glbPathsJson = new StringBuilder("[");

                for (int i = 0; i < glbPaths.size(); i++) {
                    if (i > 0) {
                        glbPathsJson.append(",");
                    }
                    glbPathsJson.append("\"../../")
                              .append(glbPaths.get(i))
                              .append("\"");
                }
                glbPathsJson.append("]");

                buffer.add(String.format("""
                    <div class="glb-viewer-container">
                        <div id="%s" 
                             class="glb-viewer"
                             data-glb-viewers=%s
                             data-viewer-type="multimultiblock"
                             data-auto-rotate="true"
                             data-auto-load="false">
                            <div class="glb-viewer-loading">
                                <div class="spinner-border" role="status">
                                    <span class="visually-hidden">Loading 3D model...</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    """,
                    viewerId, glbPathsJson));
            }
        } catch (Exception e) {
            formatWithTooltip(buffer, localizationManager.translate(I18n.MULTIBLOCK), localizationManager.translate(I18n.MULTIBLOCK_ONLY_IN_GAME));
        }
    }

    private void parseTablePage(BookEntry entry, List<String> buffer, PageTable page) {
        try {
            formatTable(entry, buffer, page);
        } catch (Exception e) {
            log.error("Table formatting failed for page '{}': {}",
                    page.getTitle() != null ? page.getTitle() : "Unknown", e.getMessage(), e);

            buffer.add("<div class=\"table-error\" style=\"color:#800;padding:15px;border:1px solid #800;border-radius:4px;margin:10px 0;background:#fee;\">");
            buffer.add("<strong>表格渲染错误</strong><br>");
            buffer.add("页面的表格数据格式有误，无法正常显示。请检查相关配置文件。");
            buffer.add("</div>");
        }
    }

    private void formatTable(BookEntry entry, List<String> buffer, PageTable data) {
        List<PageTableString> strings = data.getStrings();
        int configuredColumns = data.getColumns();
        int totalColumns = configuredColumns + 1;
        List<PageTableLegend> legend = data.getLegend();

        log.debug("Table data: {} elements, {} configured columns, {} total columns",
                strings.size(), configuredColumns, totalColumns);

        if (strings.size() < totalColumns) {
            log.warn("Table data incomplete: expected at least {} elements, got {}. Filling with empty cells.",
                    totalColumns, strings.size());

            List<PageTableString> paddedStrings = new ArrayList<>(strings);
            while (paddedStrings.size() < totalColumns) {
                paddedStrings.add(new PageTableString());
            }
            strings = paddedStrings;
        }

        if (strings.size() % totalColumns != 0) {
            log.warn("Table data does not perfectly divide columns: {} elements with {} columns. Trimming excess.",
                    strings.size(), totalColumns);

            int maxRows = strings.size() / totalColumns;
            if (maxRows < 2) {
                log.error("Table has too few elements ({} rows) for proper display", maxRows);
                buffer.add("<div class=\"table-error\" style=\"color:#888;padding:10px;border:1px solid #ddd;margin:10px 0;\">");
                buffer.add("<strong>Table data error</strong> Table has too few elements for proper display。<br>");
                buffer.add(String.format("expected columns:%d，actual columns：%d", totalColumns, strings.size()));
                buffer.add("</div>");
                return;
            }

            int trimmedSize = maxRows * totalColumns;
            strings = new ArrayList<>(strings.subList(0, trimmedSize));
            log.info("Trimmed table data to {} elements ({} rows)", trimmedSize, maxRows);
        }

        int rows = strings.size() / totalColumns;

        if (rows <= 1) {
            log.warn("Table has only {} rows, skipping render", rows);
            return;
        }

        List<PageTableString> headers = strings.subList(0, totalColumns);
        List<List<PageTableString>> body = new java.util.ArrayList<>();
        for (int i = 1; i < rows; i++) {
            body.add(strings.subList(i * totalColumns, (i + 1) * totalColumns));
        }

        formatTitle(entry, buffer, data.getTitle());
        formatText(entry, buffer, data.getText());

        if (legend != null && !legend.isEmpty()) {
            buffer.add("<div class=\"row\"><div class=\"col-md-9\">");
        }

        buffer.add("<figure class=\"table-figure\"><table><thead><tr>");
        for (PageTableString header : headers) {
            buffer.add(getComponent(header, "th"));
        }
        buffer.add("</tr></thead><tbody>");
        for (List<PageTableString> row : body) {
            buffer.add("<tr>");
            for (PageTableString td : row) {
                buffer.add(getComponent(td, "td"));
            }
            buffer.add("</tr>");
        }
        buffer.add("</tbody></table></figure>");

        if (legend != null && !legend.isEmpty()) {
            buffer.add("</div><div class=\"col-md-3\"><h4>Legend</h4>");
            for (PageTableLegend it : legend) {
                String color = it.getColor().substring(2);
                String text = it.getText();
                buffer.add(java.lang.String.format(
                        """
                        <div class="item-header">
                            <span style="background-color:#%s"></span>
                            <p>%s</p>
                        </div>
                        """, color, text));
            }
            buffer.add("</div></div>");
        }
    }

    private static String getComponent(PageTableString th, String key) {
        if (th.getFill() != null) {
            String color = th.getFill().substring(2);
            return String.format("<%s style=\"background-color:#%s;\"></%s>", key, color, key);
        }

        String text = th.getText();
        if (text.isEmpty()) {
            return String.format("<%s></%s>", key, key);
        }

        if (th.isBold()) {
            return String.format("<%s><p style=\"font-weight: bold;\">%s</p></%s>", key, text, key);
        } else {
            return String.format("<%s><p>%s</p></%s>", key, text, key);
        }
    }
}
