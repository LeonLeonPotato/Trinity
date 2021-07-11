package me.leon.trinity.hud.components;

import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.HUDeditor;
import me.leon.trinity.hud.AnchorPoint;
import me.leon.trinity.hud.Component;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.Coloring;
import me.leon.trinity.utils.rendering.Rainbow;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.List;
import java.util.*;

public class ModuleListComponent extends Component {
	public static ModeSetting mode = new ModeSetting("Mode", "AlphaPulse", "Rainbow", "AlphaPulse", "AlphaPulseStatic", "RainbowStatic");
	public static ModeSetting background = new ModeSetting("Background", "Lines", "Lines", "None", "Fill");
	public static BooleanSetting line = new BooleanSetting("Line", true);
	public static ColorSetting color = new ColorSetting("Color", 255, 255, 0, 255, true);
	public static ColorSetting BColor = new ColorSetting("Back Color", 97, 97, 97, 97, false);
	
	private static int yL = 0;
	private final Alpha alpha = new Alpha();
	private HashMap<Module, Float> map = new HashMap<>();
	private boolean updated = false;
	
	public ModuleListComponent() {
		super("ModuleList");
		this.visible = true;
		this.x = res.getScaledWidth();
		this.y = 0;
		this.anchorPoint = AnchorPoint.TOPRIGHT;
	}

	public static HashMap<Module, Float> sortByValue(HashMap<Module, Float> hm, boolean up) {
		List<Map.Entry<Module, Float>> list =
				new LinkedList<>(hm.entrySet());

		if (up) {
			list.sort(Map.Entry.comparingByValue());
		} else {
			list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
		}

		HashMap<Module, Float> temp = new LinkedHashMap<>();
		for (Map.Entry<Module, Float> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	@Override
	public void render() {
		if (background.getValue().equalsIgnoreCase("Fill"))
			this.drawBackground();
		yL = 0;
		map.clear();
		int alphaBefore = this.alpha.alpha;
		boolean up = this.alpha.up;
		//this.rainbow.push();
		for (Module mod : ModuleManager.modules) {
			if (mod.isEnabled() && mod.isVisible()) {
				String name = mod.getName();
				if(mod.getHudInfo() != null) {
					name = name.concat(" " + Coloring.getGREY() + "[" + Coloring.getRESET() + mod.getHudInfo() + Coloring.getGREY() + "]" + Coloring.getRESET());
				}
				map.put(mod, FontUtil.getStringWidth(name));
			}
		}
		switch (getQuad()) {
			case TOPRIGHT: {
				map = sortByValue(map, false);
				for (Module mod : map.keySet()) {
					final Color color = getColor(yL * 3);
					String name = mod.getName();
					if(mod.getHudInfo() != null) {
						name = name.concat(" " + Coloring.getGREY() + "[" + Coloring.getRESET() + mod.getHudInfo() + Coloring.getGREY() + "]" + Coloring.getRESET());
					}
					final float a = (this.x + this.width()) - FontUtil.getStringWidth(name);
					if (line.getValue()) {
						drawBox(a - 1, this.y + yL + FontUtil.getFontHeight(), a - 3, this.y + yL, color);
					}
					if (background.getValue().equalsIgnoreCase("Lines")) {
						drawBox((this.x + this.width()), this.y + yL + FontUtil.getFontHeight(), a - 1, this.y + yL, BColor.getValue());
					}
					FontUtil.drawString(name, a, this.y + yL, color.getRGB());
					yL += FontUtil.getFontHeight();
				}
				break;
			}
			case TOPLEFT: {
				map = sortByValue(map, false);
				for (Module mod : map.keySet()) {
					final Color color = getColor(yL * 3);
					String name = mod.getName();
					if(mod.getHudInfo() != null) {
						name = name.concat(" " + Coloring.getGREY() + "[" + Coloring.getRESET() + mod.getHudInfo() + Coloring.getGREY() + "]" + Coloring.getRESET());
					}
					final float a = this.x + FontUtil.getStringWidth(name);
					if (line.getValue()) {
						drawBox(a + 3, this.y + yL + FontUtil.getFontHeight(), a + 1, this.y + yL, color);
					}
					if (background.getValue().equalsIgnoreCase("Lines")) {
						drawBox(a + 1, this.y + yL + FontUtil.getFontHeight(), this.x, this.y + yL, BColor.getValue());
					}
					FontUtil.drawString(name, this.x, this.y + yL, color.getRGB());
					yL += FontUtil.getFontHeight();
				}
				break;
			}
			case BOTTOMRIGHT: {
				map = sortByValue(map, true);
				for (Module mod : map.keySet()) {
					final Color color = getColor(yL * 3);
					String name = mod.getName();
					if(mod.getHudInfo() != null) {
						name = name.concat(" " + Coloring.getGREY() + "[" + Coloring.getRESET() + mod.getHudInfo() + Coloring.getGREY() + "]" + Coloring.getRESET());
					}
					final float a = (this.x + this.width()) - FontUtil.getStringWidth(name);
					if (line.getValue()) {
						drawBox(a - 1, this.y + yL + FontUtil.getFontHeight(), a - 3, this.y + yL, color);
					}
					if (background.getValue().equalsIgnoreCase("Lines")) {
						drawBox((this.x + this.width()), this.y + yL + FontUtil.getFontHeight(), a - 1, this.y + yL, BColor.getValue());
					}
					FontUtil.drawString(name, (this.x + this.width()) - FontUtil.getStringWidth(name), this.y + yL, getColor(yL * 3).getRGB());
					yL += FontUtil.getFontHeight();
				}
				break;
			}
			case BOTTOMLEFT: {
				map = sortByValue(map, true);
				for (Module mod : map.keySet()) {
					final Color color = getColor(yL * 3);
					String name = mod.getName();
					if(mod.getHudInfo() != null) {
						name = name.concat(" " + Coloring.getGREY() + "[" + Coloring.getRESET() + mod.getHudInfo() + Coloring.getGREY() + "]" + Coloring.getRESET());
					}
					final float a = this.x + FontUtil.getStringWidth(name);
					if (line.getValue()) {
						drawBox(a + 3, this.y + yL + FontUtil.getFontHeight(), a + 1, this.y + yL, color);
					}
					if (background.getValue().equalsIgnoreCase("Lines")) {
						drawBox(a + 1, this.y + yL + FontUtil.getFontHeight(), this.x, this.y + yL, BColor.getValue());
					}
					FontUtil.drawString(name, this.x, this.y + yL, getColor(yL * 3).getRGB());
					yL += FontUtil.getFontHeight();
				}
				break;
			}
		}
		this.updated = false;
		if (mode.getValue().equalsIgnoreCase("AlphaPulse")) {
			this.alpha.up = up;
			this.alpha.alpha = alphaBefore;
			this.alpha.updateAlpha(3);
		}
	}

	@Override
	public float width() {
		return getMax();
	}

	@Override
	public float height() {
		return yL;
	}

	private quads getQuad() {
		if (this.anchorPoint != null) {
			switch (this.anchorPoint) {
				case TOPLEFT:
					return quads.TOPLEFT;
				case TOPRIGHT:
					return quads.TOPRIGHT;
				case BOTTOMLEFT:
					return quads.BOTTOMLEFT;
				case BOTTOMRIGHT:
					return quads.BOTTOMRIGHT;
			}
		} else {
			final ScaledResolution sr = new ScaledResolution(mc);
			final float a = sr.getScaledWidth() / 2f;
			final float b = sr.getScaledHeight() / 2f;
			if (this.x <= a && this.y <= b) {
				return quads.TOPLEFT;
			}
			if (this.x >= a && this.y <= b) {
				return quads.TOPRIGHT;
			}
			if (this.x <= a && this.y >= b) {
				return quads.BOTTOMLEFT;
			}
			if (this.x >= a && this.y >= b) {
				return quads.BOTTOMRIGHT;
			}
		}
		return quads.TOPRIGHT;
	}

	private float getMax() {
		float max = 0;
		for (Float a : map.values()) {
			if (a > max) {
				max = a;
			}
		}
		return max + (line.getValue() ? 3 : 0);
	}

	private Color getColor(int off) {
		if (!updated && mode.getValue().equalsIgnoreCase("AlphaPulseStatic")) {
			this.alpha.updateAlpha((int) color.speed);
			this.updated = true;
		}
		if (mode.getValue().equalsIgnoreCase("RainbowStatic")) {
			return color.getValue();
		} else if (mode.getValue().equalsIgnoreCase("Rainbow")) {
			return Rainbow.getColorStatic(off, (float) color.speed, color.s, color.br, color.getA());
		} else if (mode.getValue().equalsIgnoreCase("AlphaPulse")) {
			alpha.updateAlpha((int) (color.speed * 10));
			return new Color(color.getR(), color.getG(), color.getB(), alpha.alpha);
		} else if (mode.getValue().equalsIgnoreCase("AlphaPulseStatic")) {
			return new Color(color.getR(), color.getG(), color.getB(), alpha.alpha);
		}
		return null;
	}

	private enum quads {
		TOPRIGHT, TOPLEFT, BOTTOMRIGHT, BOTTOMLEFT
	}

	private final static class Alpha {
		private boolean up = true;
		private int alpha;

		private Alpha() {
			alpha = 100;
		}

		private void updateAlpha(int a) {
			if (alpha >= 255) {
				up = false;
			} else if (alpha <= 100) {
				up = true;
			}
			alpha = (int) MathUtils.clamp(100, 255, alpha + (up ? a : -a));
		}
	}
}
