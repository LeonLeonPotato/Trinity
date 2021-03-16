package me.leon.trinity.hacks.client;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Color;

public class ClientColor extends Module {
    public static Boolean sync = new Boolean("Sync", false);
    public static Color color = new Color("Color", 255, 255, 255, 255, true);

    public ClientColor() {
        super("ClientColor", "Customize the client's colors", Category.CLIENT);
    }
}
