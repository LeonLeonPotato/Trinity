package me.leon.trinity.hud.components;

import me.leon.trinity.hud.Component;
import me.leon.trinity.utils.rendering.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

public class RagDollComponent extends Component {
	public RagDollComponent() {
		super("RagDoll");
		visible = true;
		this.x = 0;
		this.y = 0;
	}

	@Override
	public void render() {
		drawBackground();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

		RenderUtils.drawEntityOnScreen((int) this.x + 25, (int) this.y + 100, 50, this.y + 13, mc.player);

		GlStateManager.enableRescaleNormal();
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	}

	@Override
	public float width() {
		return 50;
	}

	@Override
	public float height() {
		return 105;
	}
}
