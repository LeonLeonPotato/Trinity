package me.leon.trinity.hud.components;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.leon.trinity.hud.Component;
import me.leon.trinity.managers.TickrateManager;
import me.leon.trinity.utils.misc.FontUtil;

public class TPSComponent extends Component {
	public TPSComponent() {
		super("TPS");
		this.visible = true;
		this.x = 0;
		this.y = res.getScaledHeight() / 2f - 10;
	}

	@Override
	public void render() {
		drawBackground();
		FontUtil.drawString("TPS: " + ChatFormatting.WHITE + TickrateManager.getTPS(), this.x + 1, this.y, getTextColor());
	}

	@Override
	public float width() {
		return FontUtil.getStringWidth("TPS: " + ChatFormatting.WHITE + TickrateManager.getTPS()) + 3;
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
