package me.leon.trinity.hacks.client;

import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public class HUDeditor extends Module {
	public static BooleanSetting clamp = new BooleanSetting("Clamp", true);
	public static BooleanSetting anchor = new BooleanSetting("Anchor", true);
	public static BooleanSetting background = new BooleanSetting("Background", true, true);
	public static ColorSetting color = new ColorSetting("Color", background, 97, 97, 97, 97, false);
	public static ColorSetting textColor = new ColorSetting("TextColor", 255, 255, 255, 255, false);
	@EventHandler
	private final Listener<EventModeChange> toggleListener = new Listener<>(event -> {
		ClickGUI.updateShader(event);
	});

	public HUDeditor() {
		super("HUDeditor", "Edit the HUD", Category.CLIENT);
	}

	@Override
	public void onEnable() {
		//mc.displayGuiScreen(Trinity.hudEditor);
		ClickGUI.loadShader();
		this.setEnabled(false);
	}
}
