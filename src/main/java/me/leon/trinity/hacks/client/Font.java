package me.leon.trinity.hacks.client;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.Slider;

public class Font extends Module {
    public static Slider scale = new Slider("Scale", 20, 40, 80, false);
    public static Boolean shadow = new Boolean("Shadow", true);
    public static Mode families = new Mode("Family", "Comfortaa", "Lato", "Comfortaa", "Comic-Sans", "Verdana", "Ubuntu");

    public Font() {
        super("Font", "Customize font settings", Category.CLIENT);
        this.setEnabled(true);
    }

    public static boolean enabled() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled();
    }
}
