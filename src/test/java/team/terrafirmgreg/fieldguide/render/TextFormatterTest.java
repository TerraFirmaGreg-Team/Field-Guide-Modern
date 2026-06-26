package team.terrafirmgreg.fieldguide.render;

import org.junit.jupiter.api.Test;
import team.terrafirmgreg.fieldguide.localization.LocalizationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TextFormatterTest {

    @Test
    void resolveKeybindKeyAddsKeyPrefixForPatchouliAliases() {
        assertEquals("key.sneak", TextFormatter.resolveKeybindKey("sneak"));
        assertEquals("key.use", TextFormatter.resolveKeybindKey("use"));
        assertEquals("key.sneak", TextFormatter.resolveKeybindKey("key.sneak"));
    }

    @Test
    void shorthandKeybindUsesSiteKeybindingLabels() {
        LocalizationManager l10n = new StubLocalizationManager(Map.of(
                "key.sneak", "Shift",
                "key.use", "Right Click"
        ));
        String out = html("$(item)$(k:sneak)$() + $(item)$(k:use)$()", l10n);
        assertTrue(out.contains("Shift"));
        assertTrue(out.contains("Right Click"));
        assertFalse(out.contains("未配置"));
    }

    private static String html(String patchouli, LocalizationManager l10n) {
        List<String> buffer = new ArrayList<>();
        TextFormatter.formatText(buffer, patchouli, l10n);
        return String.join("", buffer);
    }

    private static final class StubLocalizationManager implements LocalizationManager {
        private final Map<String, String> keybindings;

        StubLocalizationManager(Map<String, String> keybindings) {
            this.keybindings = keybindings;
        }

        @Override
        public void switchLanguage(team.terrafirmgreg.fieldguide.localization.Language lang) {}

        @Override
        public team.terrafirmgreg.fieldguide.localization.Language getCurrentLanguage() {
            return team.terrafirmgreg.fieldguide.localization.Language.EN_US;
        }

        @Override
        public String translate(String... keys) {
            return keys[0];
        }

        @Override
        public String translateWithArgs(String key, Object... args) {
            return key;
        }

        @Override
        public Map<String, String> getKeybindings() {
            return keybindings;
        }

        @Override
        public void lazyLoadNamespace(String namespace) {}
    }

    private static String html(String patchouli) {
        return html(patchouli, null);
    }

    @Test
    void sedimentaryListUsesSingleBulletPerItem() {
        String out = html("They are:$(br)$(li)Shale$(li)Claystone$(li)Limestone");
        assertTrue(out.contains("<ul>"));
        assertFalse(out.contains("\u2022"), "must not embed Patchouli bullet char in HTML");
        assertFalse(out.contains("\u25E6"));
        assertEquals(3, countOccurrences(out, "<li>"));
        assertEquals(3, countOccurrences(out, "</li>"));
    }

    @Test
    void linkEndInsideListItemDoesNotBreakList() {
        String out = html(
                "$(li)Asbestos: $(l:tfg_ores/earth_vein_index#deep_asbestos)33%$(/l), "
                        + "$(l:tfg_ores/earth_vein_index#normal_asbestos_dry)14%$(/l)$()$(li)Barite: 25%");
        assertTrue(out.contains("33%</a>, <a href="), "percent links stay on one line");
        assertFalse(out.contains("</li>, "), "comma must not appear after closed list item");
        assertEquals(2, countOccurrences(out, "<li>"));
    }

    @Test
    void externalLinkResetInsideListItemKeepsContentTogether() {
        String out = html(
                "$(li)$(l:https://en.wikipedia.org/wiki/Felsic)Felsic$() rocks are $(thing)Granite$().");
        assertTrue(out.contains("Felsic</a>"));
        assertTrue(out.contains(" rocks are "));
        assertFalse(out.contains("</li> rocks"), "text after link reset must stay inside li");
    }

    @Test
    void br2InsertsTwoBreaksOutsideList() {
        String out = html("Line one$(br2)Line two");
        assertTrue(out.contains("<br/>\n<br/>\n"));
    }

    @Test
    void br2InsideListDoesNotCloseList() {
        String out = html("$(li)A$(br2)B$(li)C");
        assertTrue(out.contains("<li>A<br/>\n<br/>\nB</li>"));
        assertTrue(out.contains("<li>C</li>"));
    }

    @Test
    void li2OpensNestedIndentClass() {
        String out = html("$(li)one$(li2)two");
        assertTrue(out.contains("<li class=\"patchouli-li-2\">two</li>"));
    }

    @Test
    void tooltipWrapsLabelWithTitle() {
        String out = html("$(t:Chance per chunk)Rarity$(/t)");
        assertTrue(out.contains("<abbr class=\"patchouli-tooltip\" title=\"Chance per chunk\">Rarity</abbr>"));
    }

    @Test
    void clearEndsColoredText() {
        String out = html("Hold $(3)Shift$(clear) to view");
        assertTrue(out.contains("Hold <span style=\"color:#00AAAA;\">Shift</span> to view"));
        assertFalse(out.contains("$(clear)"));
    }

    @Test
    void thingMacroUsesPatchouliGreen() {
        String out = html("$(thing)Granite$()");
        assertTrue(out.contains("<span style=\"color:#449900;\">Granite</span>"));
    }

    @Test
    void shortHexColorExpandsLikePatchouli() {
        assertEquals("#449900", TextFormatter.normalizeHexColor("#490"));
        assertEquals("#bb00bb", TextFormatter.normalizeHexColor("#b0b"));
    }

    @Test
    void linkEndKeepsBoldOpenAfterLink() {
        String out = html("$(l:mechanics/fire_clay)$(bold)Bold$(/l) tail");
        assertEquals(1, countOccurrences(out, "<strong>"));
        assertEquals(1, countOccurrences(out, "</strong>"));
        assertTrue(out.contains(" tail</strong>"));
    }

    @Test
    void unknownCommandIsPreserved() {
        String out = html("before $(unknown) after");
        assertTrue(out.contains("before $(unknown) after"));
    }

    @Test
    void externalLinkShowsArrowOnClose() {
        String out = html("$(l:https://example.com)link$(/l)");
        assertTrue(out.contains("\u21AA"));
    }

    @Test
    void entryPageInternalLinkUsesParentPrefix() {
        String out = html("$(l:the_world/geology)Geology$()");
        assertTrue(out.contains("href=\"../the_world/geology.html\""));
    }

    @Test
    void categoryPageInternalLinkOmitsParentPrefix() {
        List<String> buffer = new ArrayList<>();
        TextFormatter.formatText(
                buffer,
                "$(l:the_world/geology)Geology$(), $(l:getting_started/finding_ores)Indicators$()",
                null,
                null,
                TextFormatter.CATEGORY_LINK_PREFIX);
        String out = String.join("", buffer);
        assertTrue(out.contains("href=\"the_world/geology.html\""));
        assertTrue(out.contains("href=\"getting_started/finding_ores.html\""));
        assertFalse(out.contains("href=\"../the_world/geology.html\""));
    }

    @Test
    void strikeAndPlayernameRender() {
        assertTrue(html("$(strike)gone$()").contains("<del>gone</del>"));
        assertTrue(html("Hello $(playername)").contains("<span class=\"patchouli-playername\">Player</span>"));
    }

    private static int countOccurrences(String haystack, String needle) {
        int count = 0;
        int idx = 0;
        while ((idx = haystack.indexOf(needle, idx)) >= 0) {
            count++;
            idx += needle.length();
        }
        return count;
    }
}
