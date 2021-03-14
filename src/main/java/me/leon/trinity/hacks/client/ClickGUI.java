package me.leon.trinity.hacks.client;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.Color;

public class ClickGUI extends Module {
    Color picker = new Color("aaa", 255, 255, 255, 255, false);

    public ClickGUI() {
        super("ClickGUI", "The ClickGUI of the client", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Trinity.clickGui);
    }
}
