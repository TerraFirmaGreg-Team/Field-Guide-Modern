package team.terrafirmgreg.fieldguide.site;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/** Canonical URLs, Open Graph helpers, and sitemap/robots output for the static handbook site. */
public final class SiteSeo {

    public static final String DEFAULT_SITE_BASE_URL = "https://wiki.terrafirmagreg.team/field-guide-modern";

    private SiteSeo() {}

    public static String normalizeBaseUrl(String url) {
        if (url == null) {
            return "";
        }
        String trimmed = url.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    public static String canonicalUrl(String siteBaseUrl, String localeKey, String outputFileName) {
        String base = normalizeBaseUrl(siteBaseUrl);
        if (base.isEmpty()) {
            return "";
        }
        if ("index.html".equals(outputFileName)) {
            return base + "/" + localeKey + "/";
        }
        return base + "/" + localeKey + "/" + outputFileName;
    }

    /** {@code useImagesDir} adds {@code _images/} prefix (home, category, search splash). */
    public static String ogImageUrl(String siteBaseUrl, String previewImage, boolean useImagesDir) {
        String base = normalizeBaseUrl(siteBaseUrl);
        if (base.isEmpty() || previewImage == null || previewImage.isBlank()) {
            return "";
        }
        String path = cleanAssetPath(previewImage);
        if (useImagesDir && !path.startsWith("_images/")) {
            path = "_images/" + path;
        }
        return base + "/" + path;
    }

    public static String webPageJsonLd(String title, String description, String url) {
        if (url == null || url.isBlank()) {
            return "";
        }
        return """
                {
                  "@context": "https://schema.org",
                  "@type": "WebPage",
                  "name": %s,
                  "description": %s,
                  "url": %s,
                  "isPartOf": {
                    "@type": "WebSite",
                    "name": "TerraFirmaGreg Field Guide",
                    "url": %s
                  }
                }
                """
                .formatted(jsonString(title), jsonString(description), jsonString(url), jsonString(siteRoot(url)));
    }

    public static String sitemapXml(Collection<String> urls) {
        Set<String> unique = urls.stream()
                .filter(url -> url != null && !url.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        body.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (String url : unique) {
            body.append("  <url><loc>").append(escapeXml(url)).append("</loc></url>\n");
        }
        body.append("</urlset>\n");
        return body.toString();
    }

    public static String robotsTxt(String siteBaseUrl) {
        String base = normalizeBaseUrl(siteBaseUrl);
        if (base.isEmpty()) {
            return """
                    User-agent: *
                    Allow: /
                    """;
        }
        return """
                User-agent: *
                Allow: /

                Sitemap: %s/sitemap.xml
                """
                .formatted(base);
    }

    public static String rootRedirectHtml(String siteBaseUrl) {
        String base = normalizeBaseUrl(siteBaseUrl);
        String canonical = base.isEmpty() ? "./en_us/" : base + "/en_us/";
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <meta http-equiv="refresh" content="0; url=en_us/">
                  <link rel="canonical" href="%s">
                  <title>TerraFirmaGreg Field Guide</title>
                  <script>location.replace('en_us/' + location.search + location.hash);</script>
                </head>
                <body>
                  <p><a href="en_us/">Continue to Field Guide</a></p>
                </body>
                </html>
                """
                .formatted(canonical);
    }

    private static String siteRoot(String pageUrl) {
        int idx = pageUrl.indexOf("://");
        if (idx < 0) {
            return pageUrl;
        }
        int slash = pageUrl.indexOf('/', idx + 3);
        if (slash < 0) {
            return pageUrl;
        }
        int locale = pageUrl.indexOf('/', slash + 1);
        return locale < 0 ? pageUrl : pageUrl.substring(0, locale);
    }

    private static String cleanAssetPath(String iconPath) {
        return iconPath.replace("../../", "").replace("..\\..\\", "");
    }

    private static String jsonString(String value) {
        if (value == null) {
            return "null";
        }
        StringBuilder out = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\' -> out.append("\\\\");
                case '"' -> out.append("\\\"");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        out.append('"');
        return out.toString();
    }

    private static String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
