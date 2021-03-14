package me.leon.trinity.hacks.client;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;

public class ClickGUI extends Module {
    public ClickGUI() {
        super("ClickGUI", "The ClickGUI of the client", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(null);
        mc.displayGuiScreen(Trinity.clickGui);
    }
}
