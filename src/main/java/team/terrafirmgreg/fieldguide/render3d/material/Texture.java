package team.terrafirmgreg.fieldguide.render3d.material;

import lombok.Getter;

import java.awt.image.BufferedImage;

@Getter
public class Texture {

    private String name;
    private final BufferedImage image;

    public Texture(BufferedImage image) {
        this.image = image;
    }

    public Texture(BufferedImage image, String name) {
        this.image = image;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
