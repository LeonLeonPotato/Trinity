package me.leon.trinity.clickgui.components.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.ButtonComponent;
import me.leon.trinity.clickgui.components.FrameComponent;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;

public class BooleanComponent extends SettingComponent<BooleanSetting> {
	public BooleanComponent(BooleanSetting set, Component parent, ButtonComponent moduleParent, int offset, int xOffset) {
		super(set, parent, moduleParent, offset, xOffset);
	}

	@Override
	public void render() {
		final FrameComponent c = superParent.parent;
		RenderUtils.drawRect(c.x + ClickGui.width, c.y + offset, c.x, c.y + offset + 14, ClickGUI.backgroundColor.getValue());
		if (set.getValue()) {
			RenderUtils.drawRect(this.parent.parent.x + ClickGui.width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x + 5, this.parent.parent.y + this.parent.offset + this.offset + 14, this.set.getValue() ? ClickGUI.enabledBooleanColor.getValue() : ClickGUI.disabledBooleanColor.getValue());
		}
		FontUtil.drawString(this.set.getName(), this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.nameColorSetting.getValue().getRGB());
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {

	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (isWithinButton(mouseX, mouseY) && this.parent.open) {
			if (button == 0) {
				this.set.setValue(!this.set.getValue());
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

	}

	@Override
	public void setOff(int newOff) {
		this.offset = newOff;
	}

	public boolean isWithinButton(int x, int y) {
		return x > this.parent.parent.x && x < this.parent.parent.x + ClickGui.width && y > this.parent.parent.y + this.parent.offset + this.offset + 14 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14;
	}

	@Override
	public int getHeight() {
		return 14;
	}

	@Override
	public int getXOffset() {
		return parent.getXOffset() + 3;
	}

	@Override
	public int getYOffset() {
		return 0;
	}
}
