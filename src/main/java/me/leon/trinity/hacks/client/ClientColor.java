package me.leon.trinity.hacks.client;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.ColorSetting;

public class ClientColor extends Module {
	public static ColorSetting color = new ColorSetting("Color", 255, 255, 255, 255, true);

	public ClientColor() {
		super("ClientColor", "Customize the client's colors", Category.CLIENT);
	}
}
