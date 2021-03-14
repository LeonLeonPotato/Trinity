package me.leon.trinity.setting.settings.sub;

import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.SubSetting;

public class SubColor extends SubSetting {
    public int r, g, b, a;
    public boolean rainbow;

    public SubColor(String name, Setting parent, int r, int g, int b, int a, boolean rainbow) {
        super(name, parent);
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
