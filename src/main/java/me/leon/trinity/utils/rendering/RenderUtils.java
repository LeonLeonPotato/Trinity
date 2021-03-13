package me.leon.trinity.utils.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;

public class RenderUtils {
    public static void drawRect(float x, float y, float w, float h, Color color)
    {
        float alpha = (float) color.getAlpha() / 255;
        float red = (float) color.getRed() / 255;
        float green = (float) color.getGreen() / 255;
        float blue = (float) color.getBlue() / 255;
        final net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, y, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRainbowRectHorizontal(float x, float y, float w, float h, int speed, int speed2, int alpha) {
        final net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        Rainbow rainbow = new Rainbow();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        int w0 = (int) x;
        for(; w0 >= x-w; w0 -= speed) {
            rainbow.updateColor(speed2);
            bufferbuilder.pos((w0 + speed), h, 0.0D).color(rainbow.getColor().getRed(), rainbow.getColor().getGreen(), rainbow.getColor().getBlue(), alpha).endVertex();
            bufferbuilder.pos(w0, h, 0.0D).color(rainbow.getColor().getRed(), rainbow.getColor().getGreen(), rainbow.getColor().getBlue(), alpha).endVertex();
            bufferbuilder.pos(w0, y, 0.0D).color(rainbow.getColor().getRed(), rainbow.getColor().getGreen(), rainbow.getColor().getBlue(), alpha).endVertex();
            bufferbuilder.pos((w0 + speed), y, 0.0D).color(rainbow.getColor().getRed(), rainbow.getColor().getGreen(), rainbow.getColor().getBlue(), alpha).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRainbowRectVertical(float x, float y, float w, float h, int speed, int speed2, int alpha) {
        final net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        Rainbow rainbow = new Rainbow();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        int h0 = (int) y;
        for(; h0 <= y + h; h0 += speed) {
            rainbow.updateColor(speed2);

            int red = rainbow.getColor().getRed();
            int green = rainbow.getColor().getGreen();
            int blue = rainbow.getColor().getBlue();

            bufferbuilder.pos(x, h0 - speed, 0.0D).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(w, h0 - speed, 0.0D).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(w, h0, 0.0D).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x, h0, 0.0D).color(red, green, blue, alpha).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawColorPickerSquare(float x, float y, float w, float h, int hue, int alpha) {
        final net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        final float oneW = w / 100f;
        final float oneY = h / 100f;

        for(int a = 0; a <= 100; a++) {
            for(int b = 0; b <= 100; b++) {
                Color color = new Color(Color.HSBtoRGB(hue / 360f, a / 100f, b / 100f));

                int red = color.getRed();
                int blue = color.getBlue();
                int green = color.getGreen();

                final float curW = ((a / 100f) * w);
                final float curH = ((b / 100f) * h);

                bufferbuilder.pos(x + curW, y + curH - oneY, 0.0D).color(red, green, blue, alpha).endVertex();
                bufferbuilder.pos(x + curW - oneW, y + curH - oneY, 0.0D).color(red, green, blue, alpha).endVertex();
                bufferbuilder.pos(x + curW - oneW, y + curH, 0.0D).color(red, green, blue, alpha).endVertex();
                bufferbuilder.pos(x + curW, y + curH, 0.0D).color(red, green, blue, alpha).endVertex();
            }
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawAlphaRect(float x, float y, float w, float h, Color color) {
        final net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        final float one = w / 255f;
        final float red = color.getRed() / 255f;
        final float green = color.getGreen() / 255f;
        final float blue = color.getBlue() / 255f;

        for(int a = 0; a < 256; a++) {
            final float alpha = a / 255f;
            final float cur = (a / 255f) * w;

            bufferbuilder.pos((x + cur) - one , y + h, 0.0D).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x + cur, y + h, 0.0D).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x + cur, y, 0.0D).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos((x + cur) - one, y, 0.0D).color(red, green, blue, alpha).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawHueRect(float x, float y, float w, float h) {
        final net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        final float oneY = h / 360f;

        for(int a = 0; a <= 360; a++) {
            Color color = new Color(Color.HSBtoRGB(a / 360f, 1f, 1f));

            int red = color.getRed();
            int blue = color.getBlue();
            int green = color.getGreen();
            int alpha = color.getAlpha();

            final float cur = (a / 360f) * h;

            bufferbuilder.pos(x + w, y + (cur - oneY), 0.0D).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x, y + (cur - oneY), 0.0D).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x, y + cur, 0.0D).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(x + w, y + cur, 0.0D).color(red, green, blue, alpha).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawCircle(float x, float y, float r, float w, Color color) {
        final net.minecraft.client.renderer.Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GL11.glLineWidth(w);
        bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        final int red = color.getRed();
        final int blue = color.getBlue();
        final int green = color.getGreen();
        final int alpha = color.getAlpha();

        for(int a = 0; a < 360; a++) {
            final double x1 = x + (r * Math.sin(Math.toRadians(a)));
            final double z1 = y + (r * Math.cos(Math.toRadians(a)));

            bufferbuilder.pos(x1, z1, 0.0f).color(red, green, blue, alpha).endVertex();
        }

        tessellator.draw();
        GlStateManager.shadeModel(GL_FLAT);
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
