package team.terrafirmgreg.fieldguide.export;

public record EntityRenderRecord(
        String entity,
        float scale,
        float offset,
        float defaultRotation,
        String path,
        int width,
        int height) {}
