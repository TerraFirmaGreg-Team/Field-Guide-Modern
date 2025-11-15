package io.github.tfgcn.fieldguide.asset;

import io.github.tfgcn.fieldguide.renderer.Block3DRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * desc:
 *
 * @author yanmaoyuan
 */
public class Block3DRendererTest {

    public static void main(String[] args) throws IOException {
        String modpackPath = "Modpack-Modern";
        AssetLoader assetLoader = new AssetLoader(Paths.get(modpackPath));
        Block3DRenderer renderer = new Block3DRenderer(assetLoader, 256, 256);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            renderer.render("beneath:block/unposter");
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("time:" + time);
    }
}
