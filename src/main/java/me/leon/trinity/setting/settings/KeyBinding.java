package me.leon.trinity.setting.settings;

import me.leon.trinity.setting.Setting;

public class KeyBinding extends Setting {
    public int Char;

    public KeyBinding(String name, int Char) {
        super(name);
        this.Char = Char;
    }
}
