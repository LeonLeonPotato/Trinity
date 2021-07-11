package me.leon.trinity.clickgui.components.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.ButtonComponent;
import me.leon.trinity.clickgui.components.FrameComponent;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventInputEnter;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.rewrite.TextBoxSetting;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;

public class TextBoxComponent extends Component {
	public TextBoxSetting set;
	public ButtonComponent parent;
	public int offset;
	boolean typing = false;
	private int timer = 0;

	public TextBoxComponent(TextBoxSetting set, ButtonComponent parent, int offset) {
		this.set = set;
		this.parent = parent;
		this.offset = offset;
	}

	@Override
	public void render() {
		RenderUtils.drawRect(this.parent.parent.x + ClickGui.width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, ClickGUI.backgroundColor.getValue());
		RenderUtils.drawBorder(this.parent.parent.x + ClickGui.width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, 2, new Color(ClickGUI.backgroundColor.r, ClickGUI.backgroundColor.g, ClickGUI.backgroundColor.b, (int) MathUtils.clamp(0, 255, ClickGUI.backgroundColor.a + 70)));
		FontUtil.drawString(this.set.getValue(), this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - FontUtil.getFontHeight()) / 2f), 0xa9b7c6);
		if (this.typing) {
			this.timer++;
			if (timer > 20 && timer <= 40) {
				int width = (int) FontUtil.getStringWidth(this.set.getValue());
				RenderUtils.drawRect(this.parent.parent.x + 7 + width + 2, this.parent.parent.y + this.parent.offset + this.offset + 14 + 12, this.parent.parent.x + 7 + width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 2, new Color(0xa9b7c6));
			} else if (timer > 40) {
				this.timer = 0;
			}
		}
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		if (isMouseOnButton(mouseX, mouseY) && this.parent.open && this.parent.parent.open) {
			FontUtil.drawString(this.set.getValue(), this.parent.parent.x + (ClickGui.width - FontUtil.getStringWidth(this.set.getName())), this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - FontUtil.getFontHeight()) / 2f), RenderUtils.lower(ClickGUI.nameColorSetting.getValue(), 70).getRGB());
		}
		if (!this.parent.open || !this.parent.parent.open) {
			this.typing = false;
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (isMouseOnButton(mouseX, mouseY) && this.parent.open) {
			if (button == 0) {
				for (FrameComponent f : ClickGui.frames) {
					for (ButtonComponent b : f.comps) {
						for (Component c : b.subs) {
							if (c instanceof TextBoxComponent) {
								((TextBoxComponent) c).typing = false;
							}
						}
					}
				}
				this.typing = true;
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
		if (this.typing) {
			java.lang.String charString = java.lang.String.valueOf(typedChar);

			boolean isntTypable = Character.isISOControl(typedChar);

			if ((int) typedChar == 13) {
				this.typing = false;
				Trinity.settingsDispatcher.post(new EventInputEnter(EventStage.PRE, this.set));
			}

			if (isntTypable) {
				if ((int) typedChar == 8) {
					if (this.set.getValue().length() >= 1) {
						this.set.getValue() = this.set.getValue().substring(0, this.set.val.length() - 1);
					}
				}
			} else {
				this.set.setValue(this.set.getValue().concat(charString));
			}
		}
	}

	@Override
	public void setOff(int newOff) {
		this.offset = newOff;
	}

	public boolean isMouseOnButton(int x, int y) {
		return x > this.parent.parent.x && x < this.parent.parent.x + ClickGui.width && y > this.parent.parent.y + this.parent.offset + 14 + this.offset && y < this.parent.parent.y + this.parent.offset + 14 + this.offset + 14;
	}

	@Override
	public int getHeight() {
		return 14;
	}

	@Override
	public int getXOffset() {
		return 0;
	}
}
