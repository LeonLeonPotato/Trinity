package me.leon.trinity.setting.settings;

import me.leon.trinity.setting.Setting;

public class Color extends Setting {
    public int r, g, b, a;
    public boolean rainbow;

    protected Color(String name, int r, int g, int b, int a, boolean rainbow) {
        super(name);
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.rainbow = rainbow;
    }

    public java.awt.Color getValue() {
        return new java.awt.Color(r, g, b, a);
    }
}
