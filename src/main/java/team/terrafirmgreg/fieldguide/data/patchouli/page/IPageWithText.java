package team.terrafirmgreg.fieldguide.data.patchouli.page;

import team.terrafirmgreg.fieldguide.data.patchouli.BookPage;
import lombok.Data;

@Data
public abstract class IPageWithText extends BookPage {

     protected String text;
}