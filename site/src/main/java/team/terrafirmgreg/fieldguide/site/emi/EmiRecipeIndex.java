package team.terrafirmgreg.fieldguide.site.emi;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import team.terrafirmgreg.fieldguide.gson.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Recipe ids present in an EMI export bundle ({@code emi/recipes/<namespace>/<path_safe>.json}).
 */
@Slf4j
public final class EmiRecipeIndex {

    private final Set<String> recipeIds;

    private EmiRecipeIndex(Set<String> recipeIds) {
        this.recipeIds = recipeIds;
    }

    public static EmiRecipeIndex load(Path emiRoot) {
        if (emiRoot == null || !Files.isDirectory(emiRoot)) {
            log.warn("EMI bundle missing at {} — recipe availability will not be checked at build time", emiRoot);
            return new EmiRecipeIndex(Set.of());
        }
        Path bundleJson = emiRoot.resolve("bundle.json");
        if (!Files.isRegularFile(bundleJson)) {
            log.warn("No bundle.json under {} — skipping EMI recipe index", emiRoot);
            return new EmiRecipeIndex(Set.of());
        }
        try {
            JsonObject bundle = JsonUtils.readFile(bundleJson.toFile(), JsonObject.class);
            if (bundle.has("schema") && bundle.get("schema").getAsInt() != 2) {
                log.warn("Unsupported EMI bundle schema at {}", bundleJson);
                return new EmiRecipeIndex(Set.of());
            }
        } catch (Exception e) {
            log.warn("Failed to read {}", bundleJson, e);
            return new EmiRecipeIndex(Set.of());
        }

        Path recipesDir = emiRoot.resolve("recipes");
        if (!Files.isDirectory(recipesDir)) {
            return new EmiRecipeIndex(Set.of());
        }

        Set<String> ids = new HashSet<>();
        try (Stream<Path> walk = Files.walk(recipesDir)) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .forEach(p -> recipeIdFromMetaPath(recipesDir, p).ifPresent(ids::add));
        } catch (IOException e) {
            log.warn("Failed to walk {}", recipesDir, e);
        }
        log.info("EMI recipe index: {} recipes under {}", ids.size(), emiRoot);
        return new EmiRecipeIndex(Collections.unmodifiableSet(ids));
    }

    private static Optional<String> recipeIdFromMetaPath(Path recipesDir, Path metaFile) {
        Path rel = recipesDir.relativize(metaFile);
        if (rel.getNameCount() < 2) {
            return Optional.empty();
        }
        String namespace = rel.getName(0).toString();
        String fileName = rel.getFileName().toString();
        if (!fileName.endsWith(".json")) {
            return Optional.empty();
        }
        String pathSafe = fileName.substring(0, fileName.length() - ".json".length());
        String path = pathSafe.replace('_', '/');
        return Optional.of(namespace + ":" + path);
    }

    public boolean contains(String recipeId) {
        if (recipeId == null || recipeId.isEmpty()) {
            return false;
        }
        if (recipeIds.isEmpty()) {
            return true;
        }
        return recipeIds.contains(recipeId);
    }

    public boolean isEmpty() {
        return recipeIds.isEmpty();
    }
}
