package me.leon.trinity.utils.rendering;

import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.rendering.skeet.Quad;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtils implements Util {
	private static final Tessellator tessellator = Tessellator.getInstance();
	private static final BufferBuilder builder = tessellator.getBuffer();

	public static void drawRect(float x, float y, float w, float h, Color color) {
		final float alpha = color.getAlpha() / 255f;
		final float red   = color.getRed() / 255f;
		final float green = color.getGreen() / 255f;
		final float blue  = color.getBlue() / 255f;
		prepare(1, Mode.NORMAL);
		builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		builder.pos(x, h, 0.0D).color(red, green, blue, alpha).endVertex();
		builder.pos(w, h, 0.0D).color(red, green, blue, alpha).endVertex();
		builder.pos(w, y, 0.0D).color(red, green, blue, alpha).endVertex();
		builder.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
		tessellator.draw();
		release(Mode.NORMAL);
	}

	public static void drawBorder(float x, float y, float w, float h, float width, Color color) {
		final float alpha = (float) color.getAlpha() / 255;
		final float red = (float) color.getRed() / 255;
		final float green = (float) color.getGreen() / 255;
		final float blue = (float) color.getBlue() / 255;
		prepare(width, Mode.NORMAL);
		builder.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
		builder.pos(x, h, 0.0D).color(red, green, blue, alpha).endVertex();
		builder.pos(w, h, 0.0D).color(red, green, blue, alpha).endVertex();
		builder.pos(w, y, 0.0D).color(red, green, blue, alpha).endVertex();
		builder.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
		tessellator.draw();
		release(Mode.NORMAL);
	}

	public static void drawRainbowRectHorizontal(float x, float y, float w, float h, float speed, int alpha, Color starting) {
		Rainbow rainbow = new Rainbow(starting);

		prepare(1, Mode.NORMAL);
		builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		for (float i = x; i < w; i += 0.5) {
			rainbow.update(speed);

			final float red = (float) rainbow.getColor().getRed() / 255;
			final float green = (float) rainbow.getColor().getGreen() / 255;
			final float blue = (float) rainbow.getColor().getBlue() / 255;
			
			builder.pos(i + 0.5f, h, 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos(i, h, 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos(i, y, 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos(i + 0.5f, y, 0.0D).color(red, green, blue, alpha).endVertex();
		}
		tessellator.draw();
		release(Mode.NORMAL);
	}

	public static void drawRainbowRectVertical(float x, float y, float w, float h, int speed, int alpha, Color starting) {
		Rainbow rainbow = new Rainbow(starting);

		prepare(1, Mode.NORMAL);
		builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		for (float i = y; i < h; i += 0.5) {
			rainbow.update(speed);

			final float red = (float) rainbow.getColor().getRed() / 255;
			final float green = (float) rainbow.getColor().getGreen() / 255;
			final float blue = (float) rainbow.getColor().getBlue() / 255;

			builder.pos(x + w, i + 0.5f, 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos(x, i + 0.5f, 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos(x, i, 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos(x, i, 0.0D).color(red, green, blue, alpha).endVertex();
		}
		tessellator.draw();
		release(Mode.NORMAL);
	}

	public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseY, EntityLivingBase ent) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) posX, (float) posY, 50.0F);
		GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-((float) Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		mc.getRenderManager().setPlayerViewY(180.0F);
		mc.getRenderManager().setRenderShadow(false);
		mc.getRenderManager().renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		mc.getRenderManager().setRenderShadow(true);
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public static void drawColorPickerSquare(float x, float y, float w, float h, int hue, int alpha) {
		prepare(1, Mode.GRADIENT);
		builder.begin(7, DefaultVertexFormats.POSITION_COLOR);

		final Color color = new Color(Color.HSBtoRGB(hue, 1, 1));

		builder.pos(x + w, x + h, 0.0D).color(0, 0, 0, alpha).endVertex();
		builder.pos(x, y + h, 0.0D).color(0, 0, 0, alpha).endVertex();
		builder.pos(x, y, 0.0D).color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
		builder.pos(x + w, y, 0.0D).color(255, 255, 255, alpha).endVertex();

		tessellator.draw();
		release(Mode.GRADIENT);
	}

	public static void drawAlphaRect(float x, float y, float w, float h, Color color) {
		prepare(1, Mode.NORMAL);
		builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		final float one = w / 255f;
		final float red = color.getRed() / 255f;
		final float green = color.getGreen() / 255f;
		final float blue = color.getBlue() / 255f;

		for (int a = 0; a < 256; a++) {
			final float alpha = a / 255f;
			final float cur = (a / 255f) * w;

			builder.pos((x + cur) - one, y + h, 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos(x + cur, y + h, 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos(x + cur, y, 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos((x + cur) - one, y, 0.0D).color(red, green, blue, alpha).endVertex();
		}

		tessellator.draw();
		release(Mode.NORMAL);
	}

	public static void drawHueRect(float x, float y, float w, float h) {
		prepare(1, Mode.NORMAL);
		builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		final float oneY = h / 360f;

		for (int a = 0; a <= 360; a++) {
			Color color = new Color(Color.HSBtoRGB(a / 360f, 1f, 1f));

			int red = color.getRed();
			int blue = color.getBlue();
			int green = color.getGreen();
			int alpha = color.getAlpha();

			final float cur = (a / 360f) * h;

			builder.pos(x + w, y + (cur - oneY), 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos(x, y + (cur - oneY), 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos(x, y + cur, 0.0D).color(red, green, blue, alpha).endVertex();
			builder.pos(x + w, y + cur, 0.0D).color(red, green, blue, alpha).endVertex();
		}

		tessellator.draw();
		release(Mode.NORMAL);
	}

	public static void drawCircle(float x, float y, float r, float w, Color color) {
		prepare(w, Mode.NORMAL);
		glEnable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		builder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
		final int red = color.getRed();
		final int blue = color.getBlue();
		final int green = color.getGreen();
		final int alpha = color.getAlpha();

		for (int a = 0; a < 360; a++) {
			final double x1 = x + (r * Math.sin(Math.toRadians(a)));
			final double z1 = y + (r * Math.cos(Math.toRadians(a)));

			builder.pos(x1, z1, 0.0f).color(red, green, blue, alpha).endVertex();
		}

		tessellator.draw();
		GlStateManager.shadeModel(GL_FLAT);
		glDisable(GL_LINE_SMOOTH);
		release(Mode.NORMAL);
	}

	public static void scissor(Quad quad) {
		GL11.glPushAttrib(GL_SCISSOR_BIT);
		GL11.glEnable(GL_SCISSOR_TEST);
		final ScaledResolution res = new ScaledResolution(mc);
		GL11.glScissor((int) quad.getX() * res.getScaleFactor(), (res.getScaledHeight() - (int) quad.getY1()) * res.getScaleFactor(), (int) quad.width() * res.getScaleFactor(), (int) quad.height() * res.getScaleFactor());
	}

	public static void restoreScissor() {
		GL11.glPopAttrib();
		GL11.glDisable(GL_SCISSOR_TEST);
	}

	public static Color lower(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) MathUtils.clamp(0, 255, color.getAlpha() - alpha));
	}

	public static void glColor(Color color) {
		GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	private static void prepare(float width, Mode mode) {
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.glLineWidth(width);
		if(mode == Mode.GRADIENT) {
			GlStateManager.disableAlpha();
			GlStateManager.shadeModel(GL_SMOOTH);
		}
	}

	private static void release(Mode mode) {
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		if(mode == Mode.GRADIENT) {
			GlStateManager.shadeModel(GL_FLAT);
			GlStateManager.enableAlpha();
		}
	}

	private enum Mode {
		GRADIENT, NORMAL
	}
}
