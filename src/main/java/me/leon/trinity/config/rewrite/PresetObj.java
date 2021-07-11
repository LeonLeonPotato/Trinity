package me.leon.trinity.config.rewrite;

import java.io.File;

public class PresetObj {
	public File dir;
	public String name;

	public PresetObj(String name) {
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
}
