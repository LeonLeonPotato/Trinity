package me.leon.trinity.setting.settings;

import me.leon.trinity.setting.Setting;

public abstract class SubSetting extends Setting {
    private final Setting parent;

    protected SubSetting(String name, Setting parent) {
        super(name);
        this.parent = parent;
    }

    public Setting getParent() {
        return parent;
    }

}
