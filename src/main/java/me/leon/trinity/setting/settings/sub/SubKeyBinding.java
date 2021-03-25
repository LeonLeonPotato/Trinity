package me.leon.trinity.setting.settings.sub;

import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.SubSetting;

public class SubKeyBinding extends SubSetting {
    public int Char;

    public SubKeyBinding(String name, Setting parent, int Char) {
        super(name, parent);
        this.Char = Char;
    }
}
