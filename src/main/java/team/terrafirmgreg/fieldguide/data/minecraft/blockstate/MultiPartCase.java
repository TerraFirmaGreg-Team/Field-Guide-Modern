package team.terrafirmgreg.fieldguide.data.minecraft.blockstate;

import com.google.gson.annotations.JsonAdapter;
import team.terrafirmgreg.fieldguide.gson.BlockStateVariantListAdapter;
import team.terrafirmgreg.fieldguide.gson.BlockStateConditionAdapter;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MultiPartCase {
    @JsonAdapter(BlockStateConditionAdapter.class)
    private Condition when;
    @JsonAdapter(BlockStateVariantListAdapter.class)
    private List<Variant> apply;

    public boolean check(Map<String, String> properties) {
        
        if (when == null) {
            return true;
        }
        return when.check(properties);
    }
}