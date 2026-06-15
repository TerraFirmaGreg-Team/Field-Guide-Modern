package team.terrafirmgreg.fieldguide.data.minecraft.blockstate;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BlockVariant {
    private String block;
    private Map<String, String> properties;
    private Variant variant;
    private List<Variant> variants;

    public boolean hasProperties() {
        return properties != null && !properties.isEmpty();
    }
}
