package team.terrafirmgreg.fieldguide.data.patchouli.page;

import team.terrafirmgreg.fieldguide.data.patchouli.BookPage;
import lombok.Data;

import java.util.List;

@Data
public class PageImage extends BookPage {

    private List<String> images;

    private String title;

    private Boolean border = false;

    private String text;
}
