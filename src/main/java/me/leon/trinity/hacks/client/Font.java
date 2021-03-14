package me.leon.trinity.hacks.client;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.utils.misc.FontUtil;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Font extends Module {
    public static Slider scale = new Slider("Scale", 20, 40, 80, false);
    public static Boolean shadow = new Boolean("Shadow", true);
    public static Mode families = new Mode("Family", "Comfortaa", "Lato", "Comfortaa", "Comic-Sans", "Verdana", "Ubuntu");

    public Font() {
        super("Font", "Customize font settings", Category.CLIENT);
    }
}
