package team.terrafirmgreg.fieldguide.data.tfc.page;

import team.terrafirmgreg.fieldguide.data.patchouli.page.IPageWithText;
import lombok.Data;

import java.util.List;

@Data
public class PageMultiMultiblock extends IPageWithText {

    private List<TFCMultiblockData> multiblocks;
}
