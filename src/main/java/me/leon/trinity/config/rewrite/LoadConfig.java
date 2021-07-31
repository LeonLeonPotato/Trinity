package me.leon.trinity.config.rewrite;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventLoadConfig;
import me.leon.trinity.gui.frame.IFrame;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hud.AnchorPoint;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.utils.misc.FileUtils;
import me.leon.trinity.utils.rendering.GuiUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Set;

public class LoadConfig {
	public static void load() {
		try {
			Trinity.settingsDispatcher.post(new EventLoadConfig(EventStage.PRE, Trinity.currentPreset));
			loadHUD();
			loadGUI();
			loadModules();
			loadHudEditor();
			Trinity.settingsDispatcher.post(new EventLoadConfig(EventStage.POST, Trinity.currentPreset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadHudEditor() {
		/*
		FileUtils.makeIfDoesntExist(Trinity.currentPreset.getEditorDir());
		try {
			final File editor = new File(Trinity.currentPreset.getEditorDir().getAbsolutePath() + "Editor.json");
			if(!editor.exists()) {
				return;
			}
			final JSONObject object = (JSONObject) new JSONParser().parse(new FileReaders(editor));
			HUDeditor.frame.y = ((Long) object.get("y")).intValue();
			HUDeditor.frame.open = (Boolean) object.get("open");
		} catch (Exception e) {
			e.printStackTrace();
		}

		 */
	}

	public static void loadModules() {
		FileUtils.makeIfDoesntExist(Trinity.currentPreset.getModsFile());
		for (File file : Trinity.currentPreset.getModsFile().listFiles()) {
			try {
				Module mod = ModuleManager.getMod(file.getName().replace(".json", ""));
				JSONParser p = new JSONParser();
				FileReader f = new FileReader(file);
				JSONObject main = (JSONObject) p.parse(f);

				mod.setEnabled((Boolean) main.get("enabled"));
				mod.setVisible((Boolean) main.get("visible"));
				mod.setKey(((Long) main.get("bind")).intValue());
				JSONObject settings = (JSONObject) main.get("settings");

				for(String key : (Set<String>) settings.keySet()) {
					Setting s = mod.getSetting(key);
					Object val = settings.get(key);

					s.parseJson(main, key, val);
				}

				f.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void loadGUI() {
		FileUtils.makeIfDoesntExist(Trinity.currentPreset.getGuiFile());
		for (File file : Trinity.currentPreset.getGuiFile().listFiles()) {
			try {
				IFrame c = GuiUtils.findFrame(file.getName().replaceAll(".json", ""));
				JSONParser parser = new JSONParser();
				FileReader reader = new FileReader(file);
				JSONObject obj = (JSONObject) parser.parse(reader);
				reader.close();
				assert c != null;
				c.setX(((Double) obj.get("x")).floatValue());
				c.setY(((Double) obj.get("y")).floatValue());
				c.setOpen((Boolean) obj.get("open"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void loadHUD() {
		FileUtils.makeIfDoesntExist(Trinity.currentPreset.getHudFile());
		for (File file : Trinity.currentPreset.getHudFile().listFiles()) {
			try {
				if (file.isDirectory()) continue;
				me.leon.trinity.hud.Component c = Trinity.hudManager.getComponentByName(file.getName().replaceAll(".json", ""));
				JSONParser parser = new JSONParser();
				FileReader reader = new FileReader(file);
				JSONObject obj = (JSONObject) parser.parse(reader);
				reader.close();
				c.x = ((Double) obj.get("x")).floatValue();
				c.y = ((Double) obj.get("y")).floatValue();
				c.visible = (boolean) obj.get("visible");
				String anchor = (String) obj.get("anchor");
				c.anchorPoint = anchor == null ? null : AnchorPoint.valueOf(anchor);
				JSONObject settings = (JSONObject) obj.get("settings");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void loadPreset() {
		FileUtils.makeIfDoesntExist("Trinity/");
		final File main = new File("Trinity/Main.txt");
		if (!main.exists()) {
			PrintWriter writer = FileUtils.writer("Trinity", "Main.txt");
			writer.write("Default");
			writer.close();
		}
		try {
			Scanner scanner = new Scanner(new File("Trinity/Main.txt"));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				final PresetObj obj = PresetManager.find(line);
				if (obj == null) {
					PresetObj obj1 = new PresetObj("Default");
					Trinity.currentPreset = obj1;
					PresetManager.presets.add(obj1);
					SaveConfig.runStatic();
				} else {
					Trinity.currentPreset = obj;
				}
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
