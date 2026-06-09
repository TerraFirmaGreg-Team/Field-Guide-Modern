package team.terrafirmgreg.fieldguide.site;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiteSeoTest {

    @Test
    void canonicalUrlUsesTrailingSlashForIndex() {
        assertEquals(
                "https://wiki.terrafirmagreg.team/field-guide-modern/en_us/",
                SiteSeo.canonicalUrl(SiteSeo.DEFAULT_SITE_BASE_URL, "en_us", "index.html"));
    }

    @Test
    void canonicalUrlKeepsHtmlForEntryPages() {
        assertEquals(
                "https://wiki.terrafirmagreg.team/field-guide-modern/zh_cn/getting_started.html",
                SiteSeo.canonicalUrl(SiteSeo.DEFAULT_SITE_BASE_URL, "zh_cn", "getting_started.html"));
    }

    @Test
    void ogImageUrlUsesImagesDirForSplash() {
        assertEquals(
                "https://wiki.terrafirmagreg.team/field-guide-modern/_images/splash.png",
                SiteSeo.ogImageUrl(SiteSeo.DEFAULT_SITE_BASE_URL, "splash.png", true));
    }

    @Test
    void ogImageUrlUsesAssetPathForEntryIcons() {
        assertEquals(
                "https://wiki.terrafirmagreg.team/field-guide-modern/assets/icons/atlas-000.png",
                SiteSeo.ogImageUrl(
                        SiteSeo.DEFAULT_SITE_BASE_URL, "../../assets/icons/atlas-000.png", false));
    }

    @Test
    void sitemapXmlDeduplicatesUrls() {
        String xml = SiteSeo.sitemapXml(List.of(
                "https://example.test/en_us/",
                "https://example.test/en_us/",
                "https://example.test/en_us/foo.html"));
        assertTrue(xml.contains("<loc>https://example.test/en_us/</loc>"));
        assertTrue(xml.contains("<loc>https://example.test/en_us/foo.html</loc>"));
        assertEquals(2, xml.split("<url>").length - 1);
    }

    @Test
    void robotsTxtReferencesSitemap() {
        String robots = SiteSeo.robotsTxt(SiteSeo.DEFAULT_SITE_BASE_URL);
        assertTrue(robots.contains("Sitemap: https://wiki.terrafirmagreg.team/field-guide-modern/sitemap.xml"));
    }
}
