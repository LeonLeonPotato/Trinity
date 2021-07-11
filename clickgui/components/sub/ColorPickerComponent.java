package me.leon.trinity.clickgui.components.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.ButtonComponent;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventLoadConfig;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class ColorPickerComponent extends Component implements Listenable {
	public ColorSetting set;
	public ButtonComponent parent;
	public int offset;
	public boolean open = false;
	public boolean draggingColor = false;
	public boolean draggingHue = false;
	public boolean draggingAlpha = false;
	public boolean draggingSpeed = false;
	public float renderAtHue;
	public float renderAtSpeed;
	public float renderAtAlpha;
	/**
	 * [0] = x, [1] = y
	 */
	public float[] circlePos;
	@EventHandler
	private final Listener<EventLoadConfig> loadConfigListener = new Listener<>(event -> {
		if (event.getStage() != EventStage.POST) return;
		this.renderAtSpeed = (float) ((this.set.speed / 5f) * 60f);
		this.renderAtHue = Color.RGBtoHSB(this.set.r, this.set.g, this.set.b, new float[]{0, 0, 0})[0] * 60;
		this.circlePos = new float[]{Color.RGBtoHSB(this.set.r, this.set.g, this.set.b, new float[]{0, 0, 0})[1] * 60, Color.RGBtoHSB(this.set.r, this.set.g, this.set.b, new float[]{0, 0, 0})[2] * 60};
		this.renderAtAlpha = (this.set.a / 255f) * 60;
		final Color color = new Color(Color.HSBtoRGB((renderAtHue * 6) / 360, (circlePos[0] / 60), (circlePos[1] / 60)));

		this.set.r = (color.getRed());
		this.set.g = (color.getGreen());
		this.set.b = (color.getBlue());
		this.set.a = ((int) ((renderAtAlpha / 60) * 255));
		this.set.br = (circlePos[1] / 60);
		this.set.s = (circlePos[0] / 60);
	});

	public ColorPickerComponent(ColorSetting set, ButtonComponent parent, int offset) {
		Trinity.settingsDispatcher.subscribe(this);
		this.set = set;
		this.parent = parent;
		this.offset = offset;
		this.renderAtSpeed = (float) ((this.set.speed / 5f) * 60f);
		this.renderAtHue = Color.RGBtoHSB(this.set.r, this.set.g, this.set.b, new float[]{0, 0, 0})[0] * 60;
		this.circlePos = new float[]{Color.RGBtoHSB(this.set.r, this.set.g, this.set.b, new float[]{0, 0, 0})[1] * 60, Color.RGBtoHSB(this.set.r, this.set.g, this.set.b, new float[]{0, 0, 0})[2] * 60};
		this.renderAtAlpha = (this.set.a / 255f) * 60;
	}

	@Override
	public void render() {
		final int trueY = this.parent.parent.y + this.parent.offset + this.offset + 14 + 14;

		RenderUtils.drawRect(this.parent.parent.x + ClickGui.width, trueY, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, ClickGUI.backgroundColor.getValue());
		FontUtil.drawString(this.set.getName(), this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + this.offset + 14 + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.nameColorSetting.getValue().getRGB());
		RenderUtils.drawRect((float) (this.parent.parent.x + (ClickGUI.width.getValue() - 5)), this.parent.parent.y + this.parent.offset + this.offset + 14 + 12, (float) (this.parent.parent.x + (ClickGUI.width.getValue() - 15)), this.parent.parent.y + this.parent.offset + this.offset + 14 + 2, this.set.getValue());
		if (this.open) {
			RenderUtils.drawRect(this.parent.parent.x + ClickGui.width, trueY + 135, this.parent.parent.x, trueY, ClickGUI.backgroundColor.getValue());

			RenderUtils.drawAlphaRect(this.parent.parent.x + 8, trueY + 70, 67, 10, this.set.getValue());
			RenderUtils.drawRect(this.parent.parent.x + 15 + renderAtAlpha + 1, trueY + 81, this.parent.parent.x + 15 + renderAtAlpha - 1, trueY + 69, new Color(0xa9b7c6));

			RenderUtils.drawRect(this.parent.parent.x + 75, trueY + 110, this.parent.parent.x + 15, trueY + 100, new Color(0x454545));
			RenderUtils.drawRect(this.parent.parent.x + 15 + renderAtSpeed, trueY + 110, this.parent.parent.x + 15, trueY + 100, ClickGUI.sliderColor.getValue());
			FontUtil.drawString("Speed: " + this.set.speed, this.parent.parent.x + 17, trueY + 100 + ((10 - FontUtil.getFontHeight()) / 2f), ClickGUI.nameColorSetting.getValue().getRGB());

			RenderUtils.drawColorPickerSquare(this.parent.parent.x + 15, trueY + 3, 60, 60, (int) (this.renderAtHue * 6f), this.set.a);
			RenderUtils.drawCircle(this.parent.parent.x + 15 + circlePos[0], trueY + 3 + circlePos[1], 2f, 0.2f, new Color(255, 255, 255, 255));

			RenderUtils.drawHueRect(this.parent.parent.x + 80, trueY + 3, 10, 60);
			RenderUtils.drawRect(this.parent.parent.x + 92, trueY + 3 + renderAtHue + 1, this.parent.parent.x + 78, trueY + 3 + renderAtHue - 1, new Color(0xa9b7c6));

			RenderUtils.drawRect(this.parent.parent.x + 25, trueY + 95, this.parent.parent.x + 15, trueY + 85, new Color(0xa9b7c6));
			FontUtil.drawString("Rainbow", this.parent.parent.x + 30, trueY + 85 + ((10 - FontUtil.getFontHeight()) / 2f), 0xa9b7c6);
			if (this.set.rainbow) {
				RenderUtils.drawRect(this.parent.parent.x + 23, trueY + 93, this.parent.parent.x + 17, trueY + 87, new Color(50, 243, 50));
			}

			RenderUtils.drawRect(this.parent.parent.x + 25, trueY + 125, this.parent.parent.x + 15, trueY + 115, new Color(0xa9b7c6));
			FontUtil.drawString("Sync", this.parent.parent.x + 30, trueY + 115 + ((10 - FontUtil.getFontHeight()) / 2f), 0xa9b7c6);
			if (this.set.sync) {
				RenderUtils.drawRect(this.parent.parent.x + 23, trueY + 123, this.parent.parent.x + 17, trueY + 117, new Color(50, 243, 50));
			}

			if (ClickGUI.barMode.getValue().equals("Rainbow")) {
				RenderUtils.drawRainbowRectVertical(this.parent.parent.x + 10, this.parent.parent.y + this.parent.offset + this.offset + 28, this.parent.parent.x + 8, 133, 3, 6, 200);
			} else if (ClickGUI.barMode.getValue().equals("Static")) {
				RenderUtils.drawRect(this.parent.parent.x + 10, this.parent.parent.y + this.parent.offset + this.offset + 135 + 28, this.parent.parent.x + 8, this.parent.parent.y + this.parent.offset + this.offset + 28, ClickGUI.barColor.getValue());
			}
		}
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		if (draggingHue) {
			this.renderAtHue = Math.min(60, Math.max(0, mouseY - (this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 3)));
		}
		if (draggingColor) {
			double y = Math.min(60, Math.max(0, mouseY - (this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 3)));
			double x = Math.min(60, Math.max(0, mouseX - (this.parent.parent.x + 15)));

			this.circlePos[0] = (float) x;
			this.circlePos[1] = (float) y;
		}
		if (draggingAlpha) {
			this.renderAtAlpha = Math.min(60, Math.max(0, mouseX - (this.parent.parent.x + 15)));
			this.set.a = ((int) (((renderAtAlpha) / 60) * 255));
		}
		if (draggingSpeed) {
			this.renderAtSpeed = Math.min(60, Math.max(0, mouseX - (this.parent.parent.x + 15)));
			this.set.speed = MathUtils.roundToPlace((renderAtSpeed / 60) * 5, 2);
		}
		if ((draggingColor || draggingHue)) {
			final Color color = new Color(Color.HSBtoRGB((renderAtHue * 6) / 360, (circlePos[0] / 60), (circlePos[1] / 60)));

			this.set.r = (color.getRed());
			this.set.g = (color.getGreen());
			this.set.b = (color.getBlue());
			this.set.a = ((int) ((renderAtAlpha / 60) * 255));
			this.set.br = (circlePos[1] / 60);
			this.set.s = (circlePos[0] / 60);
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (isMouseOnButtonMain(mouseX, mouseY) && this.parent.open) {
			if (button == 1) {
				this.open = !open;
				this.parent.refresh();
				this.parent.parent.refresh();
			}
		}
		if (isMouseOnButtonColor(mouseX, mouseY) && this.parent.open && this.open) {
			if (button == 0) {
				this.draggingColor = true;
			}
		}
		if (isMouseOnButtonHue(mouseX, mouseY) && this.parent.open && this.open) {
			if (button == 0) {
				this.draggingHue = true;
			}
		}
		if (isMouseOnButtonAlpha(mouseX, mouseY) && this.parent.open && this.open) {
			if (button == 0) {
				this.draggingAlpha = true;
			}
		}
		if (isMouseOnButtonRainbow(mouseX, mouseY) && this.parent.open && this.open) {
			if (button == 0) {
				this.set.rainbow = !this.set.rainbow;
			}
		}
		if (isMouseOnButtonSpeed(mouseX, mouseY) && this.parent.open && this.open) {
			if (button == 0) {
				this.draggingSpeed = true;
			}
		}
		if (isButtonOnSync(mouseX, mouseY) && this.parent.open && this.open) {
			if (button == 0) {
				this.set.sync = !this.set.sync;
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		this.draggingAlpha = this.draggingHue = this.draggingColor = this.draggingSpeed = false;
	}

	@Override
	public int getParentHeight() {
		return 14;
	}

	@Override
	public void keyTyped(char typedChar, int key) {

	}

	public boolean isMouseOnButtonMain(int x, int y) {
		return x > this.parent.parent.x && x < this.parent.parent.x + ClickGui.width && y > this.parent.parent.y + this.parent.offset + this.offset + 14 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14;
	}

	public boolean isMouseOnButtonHue(int x, int y) {
		return x > this.parent.parent.x + 80 && x < this.parent.parent.x + 90 && y > this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 3 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 3 + 60;
	}

	public boolean isMouseOnButtonColor(int x, int y) {
		return x > this.parent.parent.x + 15 && x < this.parent.parent.x + 75 && y > this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 3 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 3 + 60;
	}

	public boolean isMouseOnButtonAlpha(int x, int y) {
		return x > this.parent.parent.x + 14 && x < this.parent.parent.x + 75 && y > this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 70 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 80;
	}

	public boolean isMouseOnButtonRainbow(int x, int y) {
		return x > this.parent.parent.x + 15 && x < this.parent.parent.x + 25 && y > this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 85 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 95;
	}

	public boolean isMouseOnButtonSpeed(int x, int y) {
		return x > this.parent.parent.x + 15 && x < this.parent.parent.x + 75 && y > this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 100 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 110;
	}

	public boolean isButtonOnSync(int x, int y) {
		return x > this.parent.parent.x + 15 && x < this.parent.parent.x + 25 && y > this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 115 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14 + 125;
	}

	@Override
	public void setOff(int newOff) {
		this.offset = newOff;
	}

	@Override
	public int getHeight() {
		if (this.open) {
			return 14 + 135;
		}
		return 14;
	}

	@Override
	public int getXOffset() {
		return offset;
	}

	@Override
	public int getYOffset() {
		return 0;
	}
}
