package me.leon.trinity.hud;

import me.leon.trinity.hacks.client.HUDeditor;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.utils.rendering.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;

public abstract class Component {
	public String name;
	public float x, y;
	public boolean dragging;
	public boolean visible;
	public AnchorPoint anchorPoint = null;
	protected Minecraft mc = Minecraft.getMinecraft();
	public ScaledResolution res = new ScaledResolution(mc);
	private ArrayList<Setting> settings;

	public Component(String name) {
		this.name = name;
		this.settings = new ArrayList<>();
	}

	public abstract void render();

	public abstract float width();

	public abstract float height();

	public boolean onButton(int x, int y) {
		return ButtonCheck(this.x, this.y, width(), height(), x, y);
	}

	protected boolean pCheck() {
		return mc.player == null;
	}

	protected boolean wCheck() {
		return mc.world == null;
	}

	protected boolean nullCheck() {
		return (mc.world == null && mc.player == null);
	}

	public boolean ButtonCheck(float x, float y, float w, float h, int mX, int mY) {
		return mX >= x && mX <= (x + w) && mY >= y && mY <= (y + h);
	}

	public void drawBackground() {
		if (HUDeditor.background.getValue())
			drawBox((int) (this.x + width()) + 1, (int) (this.y + height() + 1), (int) this.x - 1, (int) this.y - 1, HUDeditor.color.getValue());
	}

	protected int getTextColor() {
		return HUDeditor.textColor.getValue().getRGB();
	}

	public void drawBox(float x, float y, float x1, float y1, Color color) {
		RenderUtils.drawRect(x, y, x1, y1, color);
	}

	public void addSetting(Setting setting) {
		settings.add(setting);
	}

	public ArrayList<Setting> getSettings() {
		return settings;
	}

	public Setting getSetting(String name) {
		return settings.stream().filter(e -> e.getName().equals(name)).findFirst().get();
	}
}
