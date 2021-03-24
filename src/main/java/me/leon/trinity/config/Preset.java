package me.leon.trinity.config;

import java.io.File;

public class Preset {
    public String name;
    public File file;

    public File enabledModules;
    public File binds;
    public File settings;
    public File GUI;
    public File search;
    public File HUD;
    public File Friends;

    public Preset(File file) {
        this.name = file.getName();
        this.file = file;

        //paths for presets
        this.enabledModules = new File((file.getAbsolutePath() + "/EnabledModules.txt"));
        this.binds = new File(file.getAbsolutePath() + "/binds.txt");
        this.settings = new File(file.getAbsolutePath() + "/Settings.txt");
        this.GUI = new File(file.getAbsolutePath() + "/GUI.txt");
        this.search = new File(file.getAbsolutePath() + "/Search.txt");
        this.HUD = new File(file.getAbsolutePath() + "/HUD.txt");
        this.Friends = new File(file.getAbsolutePath() + "/Friends.txt");
    }
}
