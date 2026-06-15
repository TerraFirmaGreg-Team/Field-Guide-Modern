package team.terrafirmgreg.fieldguide.render;

import team.terrafirmgreg.fieldguide.data.patchouli.BookEntry;
import team.terrafirmgreg.fieldguide.data.patchouli.BookPage;

import java.util.Map;

public interface PageRenderer {

    void setBookMacros(Map<String, String> bookMacros);

    void renderPage(BookEntry entry, BookPage page);
}
