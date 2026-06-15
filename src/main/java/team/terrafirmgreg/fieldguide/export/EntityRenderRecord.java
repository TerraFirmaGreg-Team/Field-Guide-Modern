package team.terrafirmgreg.fieldguide.export;

/**
 * One exported entity preview PNG from {@code meta.json} {@code entityRenders.<entityId>}.
 */
public record EntityRenderRecord(
        String entity,
        float scale,
        float offset,
        float defaultRotation,
        String path,
        int width,
        int height) {}
