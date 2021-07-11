package me.leon.trinity.clickgui.components.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.ButtonComponent;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.setting.rewrite.KeybindSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.Coloring;
import me.leon.trinity.utils.rendering.RenderUtils;
import org.lwjgl.input.Keyboard;

import java.awt.event.KeyEvent;

public class KeybindComponent extends SettingComponent<ModeSetting> {
	public KeybindSetting set;
	public ButtonComponent parent;
	public int offset;
	public boolean binding = false;

	@Override
	public void render() {
		RenderUtils.drawRect(this.parent.parent.x + ClickGui.width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, ClickGUI.backgroundColor.getValue());
		String name = set.getKeycode() == -1 ? "NONE" : Keyboard.getKeyName(set.getChar());
		FontUtil.drawString(this.binding ? "Listening" + determineDots() : "Key: " + Coloring.getWHITE() + Keyboard.getKeyName(set.getChar()), this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.nameColorSetting.getValue().getRGB());
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {

	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (isMouseOnButton(mouseX, mouseY) && this.parent.open) {
			if (button == 0) {
				this.binding = !binding;
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

	}

	@Override
	public int getParentHeight() {
		return 14;
	}

	@Override
	public void keyTyped(char typedChar, int key) {
		if (this.binding) {
			if (key == KeyEvent.VK_DELETE || key == 211) {
				this.set.setKeycode(-1);
			} else {
				this.set.setKeycode(key);
			}
			this.binding = false;
		}
	}

	@Override
	public void setOff(int newOff) {
		this.offset = newOff;
	}

	@Override
	public int getHeight() {
		return 14;
	}

	public boolean isMouseOnButton(int x, int y) {
		return x > this.parent.parent.x && x < this.parent.parent.x + ClickGui.width && y > this.parent.parent.y + this.parent.offset + 14 + this.offset && y < this.parent.parent.y + this.parent.offset + 14 + this.offset + 14;
	}

	private java.lang.String determineDots() {
		long s = System.currentTimeMillis() % 1500;
		if (s <= 500) {
			return ".";
		}
		if (s < 500 && s >= 1000) {
			return "..";
		}
		if (s < 1000 && s >= 1500) {
			return "...";
		}
		return "";
	}
}
