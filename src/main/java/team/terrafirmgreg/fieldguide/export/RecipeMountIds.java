package team.terrafirmgreg.fieldguide.export;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handbook recipe id → EMI mount id from guide-export {@code meta.json} refs.
 */
public final class RecipeMountIds {

    private RecipeMountIds() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> fromMeta(Map<String, Object> meta) {
        if (meta == null || meta.isEmpty()) {
            return Map.of();
        }
        Object refs = meta.get("refs");
        if (!(refs instanceof Map<?, ?> refsMap)) {
            return Map.of();
        }
        Object raw = refsMap.get("recipeMountIds");
        if (!(raw instanceof Map<?, ?> mountMap)) {
            return Map.of();
        }
        Map<String, String> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : mountMap.entrySet()) {
            if (entry.getKey() instanceof String handbookId
                    && entry.getValue() instanceof String mountId
                    && !handbookId.isBlank()
                    && !mountId.isBlank()) {
                out.put(handbookId, mountId);
            }
        }
        return Map.copyOf(out);
    }
}
