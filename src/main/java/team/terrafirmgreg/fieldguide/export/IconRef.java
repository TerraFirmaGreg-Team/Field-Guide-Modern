package team.terrafirmgreg.fieldguide.export;

public record IconRef(
        String atlasKind,
        String cssClass,
        String registryId,
        int cellSize,
        int page,
        int x,
        int y) {

    public String dataAttribute() {
        return "data-item";
    }

    public String atlasFileName() {
        return "atlas-%03d.png".formatted(page);
    }

    public String relativeAtlasPath() {
        return "assets/icons/" + atlasFileName();
    }

    public String toImgStyle() {
        return "width:%dpx;height:%dpx;image-rendering:pixelated;image-rendering:crisp-edges;"
                .formatted(cellSize, cellSize)
                + "object-fit:none;object-position:-%dpx -%dpx;".formatted(x, y);
    }
}
