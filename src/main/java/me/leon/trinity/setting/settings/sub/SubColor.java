package me.leon.trinity.setting.settings.sub;

import me.leon.trinity.hacks.client.ClientColor;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.SubSetting;

public class SubColor extends SubSetting {
    public int r, g, b, a;
    public int speed;
    public boolean sync;
    public boolean rainbow;

    public SubColor(String name, Setting parent, int r, int g, int b, int a, boolean rainbow) {
        super(name, parent);
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.speed = 3;
        this.sync = false;
        this.rainbow = rainbow;
    }

    public java.awt.Color getValue() {
        if(sync) {
            return new java.awt.Color(ClientColor.color.r, ClientColor.color.b, ClientColor.color.b, ClientColor.color.a);
        }
        return ClientColor.sync.getValue() ? new java.awt.Color(ClientColor.color.r, ClientColor.color.b, ClientColor.color.b, ClientColor.color.a) : new java.awt.Color(r, g, b, a);
    }
}
