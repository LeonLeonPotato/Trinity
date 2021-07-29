package me.leon.trinity.hud.components;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.leon.trinity.hud.Component;
import me.leon.trinity.utils.misc.FontUtil;
import net.minecraft.client.Minecraft;

public class FPSComponent extends Component {

	public FPSComponent() {
		super("FPS");
		visible = true;
		this.x = 0;
		this.y = this.res.getScaledHeight() / 2f;
	}

	@Override
	public void render() {
		drawBackground();
		FontUtil.drawString("FPS: " + ChatFormatting.WHITE + Minecraft.getDebugFPS(), this.x + 1, this.y, getTextColor());
	}

	@Override
	public float width() {
		return FontUtil.getStringWidth("FPS: " + ChatFormatting.WHITE + Minecraft.getDebugFPS()) + 3;
	}

	@Override
	public float height() {
		return FontUtil.getFontHeight();
	}

	@Override
	public boolean ButtonCheck(float x, float y, float w, float h, int mX, int mY) {
		return mX >= x && mX <= (x + w) && mY >= y - 1 && mY <= (y + h);
	}
}
