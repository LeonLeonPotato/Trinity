package me.leon.trinity.config.rewrite;


import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventSaveConfig;
import me.leon.trinity.gui.ClickGui;
import me.leon.trinity.gui.frame.IFrame;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.rewrite.*;
import me.leon.trinity.utils.misc.FileUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.PrintWriter;

@SuppressWarnings({"unchecked", "null"}) // doesn't work?
public class SaveConfig extends Thread {
	public final static File mainDir = new File("Trinity");

	public static void runStatic() {
		try {
			Trinity.settingsDispatcher.post(new EventSaveConfig(EventStage.PRE, Trinity.currentPreset));
			savePreset();
			saveModules();
			saveHUD();
			saveGUI();
			saveHudEditor();
			Trinity.settingsDispatcher.post(new EventSaveConfig(EventStage.POST, Trinity.currentPreset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveHudEditor() {
		/*
		PrintWriter writer = FileUtils.writer(Trinity.currentPreset.getEditorDir(), "Editor.json");
		try {
			JSONObject object = new JSONObject();
			object.put("x", HUDeditor.frame.x);
			object.put("y", HUDeditor.frame.y);
			object.put("open", HUDeditor.frame.open);
			writer.write(FileUtils.prettyPrint(object.toJSONString()));
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		 */
	}

	public static void saveModules() {
		for (Module mod : ModuleManager.modules) {
			try {
				PrintWriter writer = FileUtils.writer(Trinity.currentPreset.getModsFile(), mod.getName() + ".json");
				JSONObject object = new JSONObject();
				object.put("enabled", mod.isEnabled());
				object.put("visible", mod.isVisible());
				object.put("bind", mod.getKey());

				JSONObject settings = new JSONObject();

				for (Setting set : mod.getSettings()) {
					settings.put(set.getName(), set.getJsonString());
				}

				object.put("settings", settings);
				writer.write(FileUtils.prettyPrint(object.toString()));
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void saveGUI() {
		for (IFrame c : ClickGui.getFrames()) {
			try {
				PrintWriter writer = FileUtils.writer(Trinity.currentPreset.getGuiFile(), c.getCategory().name() + ".json");
				JSONObject object = new JSONObject();
				object.put("x", c.getX());
				object.put("y", c.getY());
				object.put("open", c.isOpen());
				writer.write(FileUtils.prettyPrint(object.toJSONString()));
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void saveHUD() {
		for (me.leon.trinity.hud.Component c : Trinity.hudManager.comps) {
			try {
				PrintWriter writer = FileUtils.writer(Trinity.currentPreset.getHudFile(), c.name + ".json");
				JSONObject object = new JSONObject();
				JSONObject settings = new JSONObject();
				object.put("x", c.x);
				object.put("y", c.y);
				object.put("anchor", c.anchorPoint == null ? null : c.anchorPoint.name());
				object.put("visible", c.visible);

				for (Setting set : c.getSettings()) {
					settings.put(set.getName(), set.getJsonString());
				}

				object.put("settings", settings);
				writer.write(FileUtils.prettyPrint(object.toJSONString()));
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void savePreset() {
		try {
			PrintWriter writer = FileUtils.writer("Trinity", "Main.txt");
			writer.write(Trinity.currentPreset.name + "\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		runStatic();
	}
}
