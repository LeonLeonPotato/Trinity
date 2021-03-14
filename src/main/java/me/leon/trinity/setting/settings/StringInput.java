package me.leon.trinity.setting.settings;

import me.leon.trinity.setting.Setting;

public class StringInput extends Setting {
    public String val;

    public StringInput(String name, String val) {
        super(name);
        this.val = val;
    }
}
