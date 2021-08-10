package me.leon.trinity.config.rewrite;

import me.leon.trinity.main.Trinity;

import java.io.File;

public class Preset {
	public File dir;
	public String name;

	public Preset(String name) {
		this.dir = new File("Trinity/" + name);
		this.name = name;
	}

	public File getModsFile() {
		return new File(dir.getAbsolutePath() + "/mods/");
	}

	public File getEditorDir() {
		return new File(dir.getAbsolutePath() + "/hud/editor/");
	}

	public File getGuiFile() {
		return new File(dir.getAbsolutePath() + "/gui/");
	}

	public File getHudFile() {
		return new File(dir.getAbsolutePath() + "/hud/");
	}

	public void load() {
		SaveConfig.runStatic();
		Trinity.currentPreset = this;
		LoadConfig.load();
	}
}
