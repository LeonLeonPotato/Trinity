package me.leon.trinity.hacks.render;

import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.rendering.ShaderHelper;
import me.leon.trinity.utils.rendering.Tessellator;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ESP extends Module {

	public static ModeSetting mode = new ModeSetting("Mode", "Shader", "Shader", "Glow", "Box");
	public static SliderSetting width = new SliderSetting("Width", 0.5, 1, 5, false);
	public static ColorSetting color = new ColorSetting("Color", 119, 0, 255, 200, false);

	public static BooleanSetting targeting = new BooleanSetting("Filters", true, false);
	public static SliderSetting range = new SliderSetting("Range", targeting, 10, 200, 300, true);
	public static BooleanSetting players = new BooleanSetting("Players", targeting, true);
	public static BooleanSetting mobs = new BooleanSetting("Mobs", targeting, true);
	public static BooleanSetting passive = new BooleanSetting("Passive", targeting, true);
	public static BooleanSetting neutral = new BooleanSetting("Neutral", targeting, true);
	public static BooleanSetting exp = new BooleanSetting("EXP", targeting, true);
	public static BooleanSetting items = new BooleanSetting("Items", targeting, true);
	public static BooleanSetting crystals = new BooleanSetting("Crystals", targeting, true);
	public static BooleanSetting misc = new BooleanSetting("Misc", targeting, true);

	@Override
	public String getHudInfo() {
		return mode.getValue();
	}

	@EventHandler
	private final Listener<EventModeChange> changeListener = new Listener<>(event -> {
		if (event.getSet() == mode) {
			resetGlow();
		}
	});

	private final LinkedHashSet<Entity> entityList = new LinkedHashSet<>();

	private final ShaderHelper shaderHelper = new ShaderHelper(new ResourceLocation("shaders/post/esp_outline.json"), "final");
	private final Framebuffer frameBuffer = shaderHelper.getFrameBuffer("final");

	public ESP() {
		super("ESP", "Highlights entities", Category.RENDER);
	}

	@SubscribeEvent
	public void onRender(EntityViewRenderEvent.FogColors event) {
		for (Framebuffer shader : shaderHelper.shader.listFramebuffers)
			shader.setFramebufferColor(event.getRed(), event.getGreen(), event.getBlue(), 0.0f);
	}

	@SubscribeEvent
	public void onRenderWorld(RenderWorldLastEvent event) {
		Tessellator.start2();
		switch (mode.getValue()) {
			case "Box": {
				for (Entity entity : entityList) {
					Tessellator.drawBBOutline(entity.getRenderBoundingBox().offset(0-entity.posX, 0-entity.posY, 0-entity.posZ).offset(EntityUtils.getInterpolatedPos(entity, event.getPartialTicks())), (float) width.getValue(), color.getValue());
				}
				break;
			}
			case "Shader": {
				drawEntities();
				drawShader();
				break;
			}
			default: {
				// Glow Mode
				break;
			}
		}
		Tessellator.end2();
	}

	private void drawEntities() {
		// Clean up the frame buffer and bind it
		frameBuffer.framebufferClear();
		frameBuffer.bindFramebuffer(false);

		boolean prevRenderOutlines = mc.renderManager.renderOutlines;

		GlStateManager.enableTexture2D();
		GlStateManager.enableCull();

		// Draw the entities into the framebuffer
		for (Entity entity : entityList) {
			GlStateManager.enableTexture2D();
			GlStateManager.enableCull();

			Render<Entity> renderer = mc.renderManager.getEntityRenderObject(entity);
			if (renderer == null) continue;

			float partialTicks = mc.getRenderPartialTicks();
			float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
			Vec3d pos = EntityUtils.getInterpolatedPos(entity, partialTicks)
					.subtract(mc.renderManager.renderPosX, mc.renderManager.renderPosY, mc.renderManager.renderPosZ);

			renderer.setRenderOutlines(true);
			renderer.doRender(entity, pos.x, pos.y, pos.z, yaw, partialTicks);
			renderer.setRenderOutlines(prevRenderOutlines);
		}

		GlStateManager.disableTexture2D();
	}

	private void drawShader() {
		// Push matrix
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();

		if (shaderHelper.shader != null)
			shaderHelper.shader.render(mc.getRenderPartialTicks());

		// Re-enable blend because shader rendering will disable it at the end
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();

		// Draw it on the main frame buffer
		mc.framebuffer.bindFramebuffer(false);
		if (frameBuffer != null)
			frameBuffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);

		// Revert states
		GlStateManager.enableBlend();
		GlStateManager.enableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GlStateManager.disableCull();

		// Revert matrix
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
	}

	@Override
	public void onUpdate() {
		if (nullCheck()) return;
		entityList.clear();
		entityList.addAll(getEntityList());

		switch (mode.getValue()) {
			case "Glow": {
				if (!entityList.isEmpty()) {
					for (Shader in : mc.renderGlobal.entityOutlineShader.listShaders) {
						if (in.getShaderManager().getShaderUniform("Radius") != null)
							in.getShaderManager().getShaderUniform("Radius").set((float) width.getValue());
					}

					for (Entity in : mc.world.loadedEntityList) { // Set glow for entities in the list. Remove glow for entities not in the list
						in.glowing = entityList.contains(in);
					}
				} else {
					resetGlow();
				}
				break;
			}
			case "Shader": {
				for (Shader shader : shaderHelper.shader.listShaders) {
					setShaderSettings(shader);
				}
				break;
			}
			default: {
				break;
			}
		}
	}

	private ArrayList<Entity> getEntityList() {
		return EntityUtils.getESPTargets(players.getValue(), neutral.getValue(), mobs.getValue(), exp.getValue(), passive.getValue(), items.getValue(), crystals.getValue(), range.getValue());
	}

	private void setShaderSettings(Shader shader) {
		if (shader.getShaderManager().getShaderUniform("color") != null)
			shader.getShaderManager().getShaderUniform("color").set(color.getValue().getRed() / 255f, color.getValue().getGreen() / 255f, color.getValue().getBlue() / 255f);

		if (shader.getShaderManager().getShaderUniform("outlineAlpha") != null)
			shader.getShaderManager().getShaderUniform("outlineAlpha").set(color.getA() / 255f);

		if (shader.getShaderManager().getShaderUniform("filledAlpha") != null)
			shader.getShaderManager().getShaderUniform("filledAlpha").set(0f);

		if (shader.getShaderManager().getShaderUniform("width") != null)
			shader.getShaderManager().getShaderUniform("width").set((float) width.getValue());

		if (shader.getShaderManager().getShaderUniform("Radius") != null)
			shader.getShaderManager().getShaderUniform("Radius").set(0f);
	}

	@Override
	public void onDisable() {
		resetGlow();
	}

	private void resetGlow() {
		try {
			for (Shader in : mc.renderGlobal.entityOutlineShader.listShaders) {
				if (in.getShaderManager().getShaderUniform("Radius") != null)
					in.getShaderManager().getShaderUniform("Radius").set(2f); // default radius
			}

			for (Entity in : mc.world.loadedEntityList) {
				in.glowing = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
