package me.leon.trinity.utils.rendering;

import me.leon.trinity.utils.Util;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import static org.lwjgl.opengl.GL11.*;
import static net.minecraft.client.renderer.GlStateManager.*;

import java.awt.*;

public class Tessellator implements Util {
    private static net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
    private static BufferBuilder builder = tessellator.getBuffer();

    public static void drawBBOutline(BlockPos pos, float width, Color color) {
        AxisAlignedBB bb = new AxisAlignedBB(pos);
        pushMatrix();

        glDisable(3553);
        glDisable(2929);
        glDisable(2884);
        glDepthMask(false);
        glEnable(3042);
        glBlendFunc(770, 771);
        width(width);

        final int r = color.getRed();
        final int b = color.getBlue();
        final int g = color.getGreen();
        final int a = color.getAlpha();

        builder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        vertex(bb.minX,bb.minY,bb.minZ, r, g, b, a);
        vertex(bb.minX,bb.minY,bb.maxZ, r, g, b, a);
        vertex(bb.maxX,bb.minY,bb.maxZ, r, g, b, a);
        vertex(bb.maxX,bb.minY,bb.minZ, r, g, b, a);
        vertex(bb.minX,bb.minY,bb.minZ, r, g, b, a);
        vertex(bb.minX,bb.maxY,bb.minZ, r, g, b, a);
        vertex(bb.minX,bb.maxY,bb.maxZ, r, g, b, a);
        vertex(bb.minX,bb.minY,bb.maxZ, r, g, b, a);
        vertex(bb.maxX,bb.minY,bb.maxZ, r, g, b, a);
        vertex(bb.maxX,bb.maxY,bb.maxZ, r, g, b, a);
        vertex(bb.minX,bb.maxY,bb.maxZ, r, g, b, a);
        vertex(bb.maxX,bb.maxY,bb.maxZ, r, g, b, a);
        vertex(bb.maxX,bb.maxY,bb.minZ, r, g, b, a);
        vertex(bb.maxX,bb.minY,bb.minZ, r, g, b, a);
        vertex(bb.maxX,bb.maxY,bb.minZ, r, g, b, a);
        vertex(bb.minX,bb.maxY,bb.minZ, r, g, b, a);
        tessellator.draw();

        glDepthMask(true);
        glDisable(3042);
        glEnable(3553);
        glEnable(2929);
        glEnable(2884);

        GlStateManager.popMatrix();
    }

    public static void draw() {
        disableTexture2D();
        enableBlend();
        disableAlpha();
        tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        shadeModel(GL_SMOOTH);
        width(3);

        builder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        tessellator.draw();

        shadeModel(GL_FLAT);
        disableBlend();
        enableAlpha();
        enableTexture2D();
    }

    public static void drawGradientAlphaCubeOutline(BlockPos pos, float width, Color start) {
        AxisAlignedBB bb = new AxisAlignedBB(pos);

        disableTexture2D();
        enableBlend();
        disableAlpha();
        disableDepth();
        tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        shadeModel(GL_SMOOTH);
        width(width);

        final int r = start.getRed();
        final int b = start.getBlue();
        final int g = start.getGreen();
        final int a = start.getAlpha();

        builder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        vertex(bb.minX,bb.minY,bb.minZ, r, g, b, a);
        vertex(bb.minX,bb.minY,bb.maxZ, r, g, b, a);
        vertex(bb.maxX,bb.minY,bb.maxZ, r, g, b, a);
        vertex(bb.maxX,bb.minY,bb.minZ, r, g, b, a);
        vertex(bb.minX,bb.minY,bb.minZ, r, g, b, a);
        vertex(bb.minX,bb.maxY,bb.minZ, r, g, b, 0);
        vertex(bb.minX,bb.maxY,bb.maxZ, r, g, b, 0);
        vertex(bb.minX,bb.minY,bb.maxZ, r, g, b, a);
        vertex(bb.maxX,bb.minY,bb.maxZ, r, g, b, a);
        vertex(bb.maxX,bb.maxY,bb.maxZ, r, g, b, 0);
        vertex(bb.minX,bb.maxY,bb.maxZ, r, g, b, 0);
        vertex(bb.maxX,bb.maxY,bb.maxZ, r, g, b, 0);
        vertex(bb.maxX,bb.maxY,bb.minZ, r, g, b, 0);
        vertex(bb.maxX,bb.minY,bb.minZ, r, g, b, a);
        vertex(bb.maxX,bb.maxY,bb.minZ, r, g, b, 0);
        vertex(bb.minX,bb.maxY,bb.minZ, r, g, b, 0);
        tessellator.draw();

        enableDepth();
        shadeModel(GL_FLAT);
        disableBlend();
        enableAlpha();
        enableTexture2D();
    }

    private static void start(float width) {
        GlStateManager.pushMatrix();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glDepthMask(false);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        width(width);
    }

    private static void end() {
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        GlStateManager.popMatrix();
    }

    private static void width(float width) {
        GlStateManager.glLineWidth(width);
    }

    private static void vertex (double x, double y, double z, int r, int g, int b, int a) {
        builder.pos(x-mc.getRenderManager().viewerPosX,y-mc.getRenderManager().viewerPosY,z-mc.getRenderManager().viewerPosZ).color(r, g, b, a).endVertex();
    }

    private static void vertex (int r, int g, int b, int a) {
        builder.pos(0, mc.player.getEyeHeight(), 0).color(r, g, b, a).endVertex();
    }
}
