package team.terrafirmgreg.fieldguide.export;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import team.terrafirmgreg.fieldguide.data.patchouli.page.PageEntity;
import team.terrafirmgreg.fieldguide.gson.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves {@code patchouli:entity} pages to exported preview PNG paths in {@code meta.json}.
 * Expects {@code entityRenders} as a map keyed by full entity id (including NBT).
 */
@Slf4j
public final class EntityRenderResolver {

    private static final TypeToken<Map<String, Object>> META_TYPE = new TypeToken<>() {};
    private static final TypeToken<List<Map<String, Object>>> RENDER_LIST_TYPE = new TypeToken<>() {};

    private final Map<String, EntityRenderRecord> byEntityId;

    private EntityRenderResolver(Map<String, EntityRenderRecord> byEntityId) {
        this.byEntityId = byEntityId;
    }

    public static EntityRenderResolver load(Path exportRoot) {
        Path metaFile = exportRoot.resolve("meta.json");
        if (!Files.isRegularFile(metaFile)) {
            return empty();
        }
        try {
            Map<String, Object> meta = JsonUtils.GSON.fromJson(Files.readString(metaFile), META_TYPE.getType());
            return fromMeta(meta != null ? meta : Map.of());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + metaFile, e);
        }
    }

    public static EntityRenderResolver fromMeta(Map<String, Object> meta) {
        if (meta == null || meta.isEmpty()) {
            return empty();
        }
        Object renders = meta.get("entityRenders");
        if (renders == null) {
            return empty();
        }
        Map<String, EntityRenderRecord> map = new LinkedHashMap<>();
        if (renders instanceof Map<?, ?> renderMap) {
            loadFromMap(renderMap, map);
        } else {
            loadFromLegacyArray(renders, map);
        }
        log.info("Loaded {} entity render(s) from meta.json", map.size());
        return new EntityRenderResolver(Collections.unmodifiableMap(map));
    }

    public Optional<EntityRenderRecord> resolve(PageEntity page) {
        if (page == null || page.getEntityId() == null || page.getEntityId().isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(byEntityId.get(page.getEntityId()));
    }

    public Optional<EntityRenderRecord> resolve(String entityId) {
        if (entityId == null || entityId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(byEntityId.get(entityId));
    }

    private static EntityRenderResolver empty() {
        return new EntityRenderResolver(Map.of());
    }

    private static void loadFromMap(Map<?, ?> renderMap, Map<String, EntityRenderRecord> target) {
        for (Map.Entry<?, ?> entry : renderMap.entrySet()) {
            if (!(entry.getKey() instanceof String entityId) || entityId.isBlank()) {
                continue;
            }
            if (!(entry.getValue() instanceof Map<?, ?> row)) {
                continue;
            }
            @SuppressWarnings("unchecked")
            EntityRenderRecord record = fromEntry(entityId, (Map<String, Object>) row);
            if (record != null) {
                target.put(entityId, record);
            }
        }
    }

    private static void loadFromLegacyArray(Object renders, Map<String, EntityRenderRecord> target) {
        String json = JsonUtils.GSON.toJson(renders);
        List<Map<String, Object>> list = JsonUtils.GSON.fromJson(json, RENDER_LIST_TYPE.getType());
        if (list == null) {
            return;
        }
        log.warn("meta.json entityRenders is a legacy array; re-export with field-guide-export for map format");
        for (Map<String, Object> row : list) {
            EntityRenderRecord record = fromLegacyRow(row);
            if (record != null) {
                target.put(record.entity(), record);
            }
        }
    }

    private static EntityRenderRecord fromEntry(String entityId, Map<String, Object> row) {
        String path = stringVal(row.get("path"));
        if (path == null) {
            return null;
        }
        float scale = floatVal(row.get("scale"), 1f);
        float offset = floatVal(row.get("offset"), 0f);
        float rotation = floatVal(row.get("defaultRotation"), -45f);
        int width = intVal(row.get("width"), 256);
        int height = intVal(row.get("height"), 256);
        return new EntityRenderRecord(entityId, scale, offset, rotation, path, width, height);
    }

    private static EntityRenderRecord fromLegacyRow(Map<String, Object> row) {
        String entity = stringVal(row.get("entity"));
        String path = stringVal(row.get("path"));
        if (entity == null || path == null) {
            return null;
        }
        return fromEntry(entity, row);
    }

    private static String stringVal(Object value) {
        if (value instanceof String s && !s.isBlank()) {
            return s.trim();
        }
        return null;
    }

    private static float floatVal(Object value, float defaultValue) {
        if (value instanceof Number number) {
            return number.floatValue();
        }
        return defaultValue;
    }

    private static int intVal(Object value, int defaultValue) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return defaultValue;
    }
}
