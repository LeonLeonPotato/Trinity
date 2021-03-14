package me.leon.trinity.managers;

import me.leon.trinity.config.Preset;
import me.leon.trinity.main.Trinity;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class PresetManager {
    public ArrayList<Preset> presets = new ArrayList<>();

    public PresetManager() {
        for(File file : Objects.requireNonNull(me.leon.trinity.config.saveConfig.confDir.listFiles())) {
            if(file.isDirectory()) {
                presets.add(new Preset(file));
            }
        }
    }

    public Preset getPreset(String name) {
        for(Preset preset : presets) {
            if(preset.name.equals(name)) {
                return preset;
            }
        }
        return null;
    }

    public void updateList() {
        String oldpresetname = Trinity.curPreset.name;

        presets.clear();
        for(File file : Objects.requireNonNull(me.leon.trinity.config.saveConfig.confDir.listFiles())) {
            if(file.isDirectory()) {
                presets.add(new Preset(file));
            }
        }

        Trinity.curPreset = getPreset(oldpresetname);
    }

    public void refresh() {
        presets.clear();
        for(File file : Objects.requireNonNull(me.leon.trinity.config.saveConfig.confDir.listFiles())) {
            if(file.isDirectory()) {
                presets.add(new Preset(file));
            }
        }
    }
}
