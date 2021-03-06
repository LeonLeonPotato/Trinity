package me.leon.trinity.clickgui.components.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.ButtonComponent;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.Coloring;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderComponent extends Component {
	public SliderSetting set;
	public ButtonComponent parent;
	public int offset;
	public boolean hovered = false;
	public double renderWidth = 0;
	public boolean dragging = false;

	public SliderComponent(SliderSetting set, ButtonComponent parent, int offset) {
		this.set = set;
		this.parent = parent;
		this.offset = offset;
	}

	@Override
	public void render() {
		RenderUtils.drawRect(this.parent.parent.x + ClickGui.width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, ClickGUI.backgroundColor.getValue());
		RenderUtils.drawRect((float) (this.parent.parent.x + MathUtils.clamp(0, ClickGUI.width.getValue(), renderWidth) + 5), this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x + 5, this.parent.parent.y + this.parent.offset + this.offset + 14, ClickGUI.sliderColor.getValue());
		FontUtil.drawString(this.set.getName() + ": " + Coloring.getWHITE() + this.set.getValue(), this.parent.parent.x + 7, this.parent.parent.y + this.offset + this.parent.offset + 14 + ((14 - FontUtil.getFontHeight())) / 2f, ClickGUI.nameColorSetting.getValue().getRGB());
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (isMouseOnButton(mouseX, mouseY) && this.parent.open && this.parent.parent.open) {
			if (button == 0) {
				this.dragging = true;
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		this.dragging = false;
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

	@Override
	public int getHeight() {
		return 14;
	}

	@Override
	public int getXOffset() {
		return 0;
	}

	@Override
	public int getYOffset() {
		return 0;
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButton(mouseX, mouseY);

		double diff = Math.min(ClickGui.width - 5, Math.max(0, (mouseX - 5) - this.parent.parent.x));

		double min = set.getMin();
		double max = set.getMax();

		renderWidth = (int) ((ClickGui.width - 5) * (set.getValue() - min) / (max - min));

		if (dragging) {
			if (diff == 0) {
				set.setValue(set.getMin());
			} else {
				double newValue = MathUtils.roundToPlace(((diff / (ClickGui.width - 5)) * (max - min) + min), set.isOnlyInt() ? 0 : 2);
				set.setValue(MathUtils.clamp(min, max, newValue));
			}
		}
	}

	public boolean isMouseOnButton(int x, int y) {
		return x > this.parent.parent.x + 7 && x < this.parent.parent.x + ClickGui.width && y > this.parent.parent.y + this.parent.offset + this.offset + 14 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14;
	}
}
