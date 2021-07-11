package me.leon.trinity.hacks.client;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.misc.FontUtil;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Font extends Module {
	public static SliderSetting scale = new SliderSetting("Scale", 5, 18, 30, true);
	public static BooleanSetting vanilla = new BooleanSetting("Vanilla", true);
	public static BooleanSetting shadow = new BooleanSetting("Shadow", true);
	public static ModeSetting style = new ModeSetting("Style", "Plain", "Plain", "Bold", "Italic", "Bold-Italic");
	public static ModeSetting families = new ModeSetting("Family", "Comfortaa");

	public Font() {
		super("Font", "Customize font settings", Category.CLIENT);
		this.setEnabled(true);
		final ArrayList<String> fontNames = new ArrayList<>(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
		if(!fontNames.contains("Comfortaa")) {
			fontNames.add("Comfortaa");
		}
		if(!fontNames.contains("Ubuntu")) {
			fontNames.add("Ubuntu");
		}
		families.setValues(fontNames);
	}

	public static boolean enabled() {
		return ModuleManager.getMod(Font.class).isEnabled();
	}
}
