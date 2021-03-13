package me.leon.trinity.setting;

import me.leon.trinity.hacks.Module;

public abstract class Setting {
    public String name;
    public Module parent;

    protected Setting(String name) {
        this.name = name;
    }
}
