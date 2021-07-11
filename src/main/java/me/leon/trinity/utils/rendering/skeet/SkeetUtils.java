package me.leon.trinity.utils.rendering.skeet;

import me.leon.trinity.mixins.IMixin;
import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;

public class SkeetUtils implements Util {
    /**
     * renders the classic Skeet Box
     * @param quad
     * @return the inside of the box
     */
    public static Quad renderSkeetBox(Quad quad) {
        return renderSkeetBox(quad, 255);
    }

    public static Quad renderSkeetBox(Quad quad, int alpha) {
        SkeetRenderer.drawBox(quad, new Color(25, 26, 26, alpha));
        quad.shrink(0.5f);
        SkeetRenderer.drawBox(quad, new Color(45, 45, 45, alpha));
        quad.shrink(0.5f);
        SkeetRenderer.drawBox(quad, new Color(51, 51, 51, alpha));
        quad.shrink(0.5f);
        SkeetRenderer.drawBox(quad, new Color(40, 40, 40, alpha));
        quad.shrink(1.5f);
        SkeetRenderer.drawBox(quad, new Color(51, 51, 51, alpha));
        quad.shrink(0.5f);
        SkeetRenderer.drawBox(quad, new Color(45, 45, 45, alpha));
        quad.shrink(0.5f);
        SkeetRenderer.drawBox(quad, new Color(16, 16, 16, alpha));

        final Quad rainbow = new Quad(quad.getX(), quad.getY(), quad.getX1(), quad.getY() + 0.5f);
        final Quad rainbow0 = new Quad(quad.getX(), quad.getY() + 0.5f, quad.getX1(), quad.getY() + 1.0f);
        SkeetRenderer.drawRainbowX(rainbow, 193f, 0.5f, 0.87f, 1f, 2f, alpha);
        SkeetRenderer.drawRainbowX(rainbow0, 193f, 0.5f, 0.5f, 1f, 2f, alpha);

        return quad;
    }
}