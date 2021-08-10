package me.leon.trinity.config.rewrite;

import me.leon.trinity.utils.misc.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class PresetManager {
	public static ArrayList<Preset> presets = new ArrayList<>();

	public PresetManager() {
		FileUtils.makeIfDoesntExist(SaveConfig.mainDir);
		for (File file : SaveConfig.mainDir.listFiles()) {
			if (file.isDirectory()) {
				presets.add(new Preset(file.getName()));
			}
		}
	}

	public static Preset find(String name) {
		for (Preset pre : presets) {
			if (pre.name.equals(name)) {
				return pre;
			}
		}
		return null;
	}
}
