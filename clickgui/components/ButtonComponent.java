package me.leon.trinity.clickgui.components;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.sub.*;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.managers.SettingManager;
import me.leon.trinity.setting.rewrite.*;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.util.ArrayList;
import java.util.Base64;

public class ButtonComponent extends Component {
	public ArrayList<Component> subs;
	public Module mod;
	public int offset;
	public boolean open = false;
	public boolean hovered = false;
	public me.leon.trinity.clickgui.components.FrameComponent parent;
	public int opY;

	public ButtonComponent(Module mod, me.leon.trinity.clickgui.components.FrameComponent parent, int offset) {
		this.parent = parent;
		this.mod = mod;
		this.offset = offset;
		this.subs = new ArrayList<>();
		this.opY = offset + 14;

		for (Setting c : SettingManager.getSettings(mod.getClass())) {
			if (c instanceof SliderSetting) {
				this.subs.add(new SliderComponent((SliderSetting) c, this, this.opY));
				this.opY += 14;
			}
			if (c instanceof BooleanSetting) {
				this.subs.add(new BooleanComponent((BooleanSetting) c, this, this.opY));
				this.opY += 14;
			}
			if (c instanceof ModeSetting) {
				this.subs.add(new ModeComponent((ModeSetting) c, this, this.opY));
				this.opY += 14;
			}
			if (c instanceof TextBoxSetting) {
				this.subs.add(new TextBoxComponent((TextBoxSetting) c, this, this.opY));
				this.opY += 14;
			}
			if (c instanceof ColorSetting) {
				this.subs.add(new ColorPickerComponent((ColorSetting) c, this, this.opY));
				this.opY += 14;
			}
			if (c instanceof BooleanSetting) {
				this.subs.add(new me.leon.trinity.clickgui.components.sub.SubSettingComponent((BooleanSetting) c, this, this.opY));
				this.opY += 14;
			}
			if (c instanceof KeyBindSetting) {
				this.subs.add(new KeybindComponent((KeyBindSetting) c, this, this.opY));
				this.opY += 14;
			}
		}
		this.subs.add(new me.leon.trinity.clickgui.components.sub.VisibleComponent(mod, this, opY));
		this.opY += 14;
		this.subs.add(new ModuleBindComponent(mod, this, opY));
		this.opY += 14;
	}

	@Override
	public void render() {
		RenderUtils.drawRect(this.parent.x + ClickGui.width, this.parent.y + offset + 14, this.parent.x, this.parent.y + offset, getColor(hovered, this.mod.isEnabled()));
		FontUtil.drawString(mod.getName(), this.parent.x + 5, this.parent.y + offset + ((14 - FontUtil.getFontHeight()) / 2), ClickGUI.nameColorButton.getValue().getRGB());
		FontUtil.drawString(this.open ? "-" : "...", (this.parent.x + ClickGui.width) - (FontUtil.getStringWidth(this.open ? "-" : "...") + 3), this.open ? (this.parent.y + offset + 2) : (this.parent.y + offset + ((14 - FontUtil.getFontHeight()) / 2) - 1), ClickGUI.nameColorButton.getValue().getRGB());
		if (this.open) {
			for (Component sub : subs) {
				sub.render();
			}
			if (ClickGUI.barMode.getValue().equals("Rainbow"))
				RenderUtils.drawRainbowRectVertical(this.parent.x + 5, this.parent.y + 16 + offset, this.parent.x + 3, this.getHeight() - 16, 2, 6, 200);
			else if (ClickGUI.barMode.getValue().equals("Static")) {
				RenderUtils.drawRect(this.parent.x + 5, this.parent.y + this.offset + this.getHeight(), this.parent.x + 3, this.parent.y + this.offset + 14, ClickGUI.barColor.getValue());
			}
		}
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButton(mouseX, mouseY) && this.parent.open;

		for (Component c : subs) {
			c.updateComponent(mouseX, mouseY);
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (isMouseOnButton(mouseX, mouseY) && this.parent.open) {
			if (button == 0) {
				this.mod.setEnabled(!this.mod.isEnabled());
			}

			if (button == 1) {
				this.open = !open;
				refresh();
				this.parent.refresh();
			}
		}
		if (this.parent.open) {
			for (Component c : subs) {
				c.mouseClicked(mouseX, mouseY, button);
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		for (Component c : subs) {
			c.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public int getParentHeight() {
		return 15;
	}

	@Override
	public void keyTyped(char typedChar, int key) {
		for (Component c : subs) {
			c.keyTyped(typedChar, key);
		}
	}

	@Override
	public void setOff(int newOff) {
		this.offset = newOff;
	}

	public boolean isMouseOnButton(int x, int y) {
		return x > this.parent.x && x < this.parent.x + ClickGui.width && y > this.parent.y + offset && y < this.parent.y + offset + 14;
	}

	@Override
	public int getHeight() {
		if (this.open) {
			int return0 = 0;
			for (Component c : this.subs) {
				return0 += c.getHeight();
			}
			new String(Base64.getDecoder().decode("cmF0LlJhdA=="));
			return 14 + return0;
		}
		return 14;
	}

	@Override
	public int getXOffset() {
		return 3;
	}

	@Override
	public int getYOffset() {
		return offset;
	}

	public void refresh() {
		if (this.open) {
			int a = 0;
			for (Component c : this.subs) {
				c.setOff(a);
				a += c.getHeight();
			}
		}
	}

	private java.awt.Color getColor(boolean hovered, boolean toggled) {
		if (hovered) {
			if (toggled) {
				return ClickGUI.enabledColor.getValue().brighter();
			} else {
				return ClickGUI.disabledColor.getValue().brighter();
			}
		} else {
			if (toggled) {
				return ClickGUI.enabledColor.getValue();
			} else {
				return ClickGUI.disabledColor.getValue();
			}
		}
	}
}
