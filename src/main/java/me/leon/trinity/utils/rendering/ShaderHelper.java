package me.leon.trinity.utils.rendering;

import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.Util;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

/**
 * @author Kami Blue
 * <p>
 * yes i skidded now shut up
 */
public class ShaderHelper implements Util {

	private final HashMap<String, Framebuffer> frameBufferMap = new HashMap<>();
	public ShaderGroup shader;
	private boolean frameBuffersInitialized = false;

	public ShaderHelper(ResourceLocation location, String... names) {
		if (!OpenGlHelper.shadersSupported)
			Trinity.LOGGER.warn("Shaders are unsupported by OpenGL!");

		if (isIntegrated()) {
			Trinity.LOGGER.warn("Running on Intel Integrated Graphics!");
		}

		try {
			ShaderLinkHelper.setNewStaticShaderLinkHelper();

			shader = new ShaderGroup(mc.getTextureManager(), mc.resourceManager, mc.framebuffer, location);
			shader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);

		} catch (Exception e) {
			Trinity.LOGGER.warn("Failed to load shaders");
			e.printStackTrace();
		}

		for (String name : names) {
			frameBufferMap.put(name, shader.getFramebufferRaw(name));
		}

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onTick0(TickEvent.ClientTickEvent event) {
		if(shader == null) return;
		int w = 0;
		int h = 0;
		if (event.phase == TickEvent.Phase.START) {
			w = mc.displayWidth;
			h = mc.displayHeight;
		} else if (event.phase == TickEvent.Phase.END) {
			if (w != mc.displayWidth || h != mc.displayHeight) {
				shader.createBindFramebuffers(mc.displayWidth, mc.displayHeight); // this will not run if on Intel GPU or unsupported Shaders
			}
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (!frameBuffersInitialized && shader != null) {
			shader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);

			frameBuffersInitialized = true;
		}
	}

	public Framebuffer getFrameBuffer(String name) {
		return frameBufferMap.get(name);
	}

	private boolean isIntegrated() {
		return GlStateManager.glGetString(GL11.GL_VENDOR).contains("Intel");
	}
}
