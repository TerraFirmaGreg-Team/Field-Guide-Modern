package team.terrafirmgreg.fieldguide.data.patchouli;

import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class BookPage {

    protected String type;

    protected String flag;

    protected String advancement;

    protected String anchor;

    private transient JsonObject jsonObject;
}
