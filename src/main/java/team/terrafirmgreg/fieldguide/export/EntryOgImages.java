package team.terrafirmgreg.fieldguide.export;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EntryOgImages {

    private EntryOgImages() {}

    @SuppressWarnings("unchecked")
    public static Map<String, String> fromMeta(Map<String, Object> meta) {
        if (meta == null || meta.isEmpty()) {
            return Map.of();
        }
        Object raw = meta.get("entryOgImages");
        if (!(raw instanceof Map<?, ?> map)) {
            return Map.of();
        }
        Map<String, String> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() instanceof String entryId
                    && entry.getValue() instanceof String path
                    && !entryId.isBlank()
                    && !path.isBlank()) {
                out.put(entryId, path);
            }
        }
        return Map.copyOf(out);
    }
}
