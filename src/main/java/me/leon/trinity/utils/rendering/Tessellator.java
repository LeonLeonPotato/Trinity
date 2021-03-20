package me.leon.trinity.utils.rendering;

import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.world.WorldUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Tessellator implements Util {
    private static net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
    private static BufferBuilder builder = tessellator.getBuffer();

    public static void drawBBOutline(BlockPos pos, float width, Color color) {
        AxisAlignedBB bb = new AxisAlignedBB(pos);
        GlStateManager.pushMatrix();

        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(width);

        final int r = color.getRed();
        final int b = color.getGreen();
        final int g = color.getBlue();
        final int a = color.getAlpha();

        builder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
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

        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glEnable(2884);

        GlStateManager.popMatrix();
    }

    private static void vertex (double x, double y, double z, int r, int g, int b, int a) {
        builder.pos(x-mc.getRenderManager().viewerPosX,y-mc.getRenderManager().viewerPosY,z-mc.getRenderManager().viewerPosZ).color(r, g, b, a).endVertex();
    }
}
