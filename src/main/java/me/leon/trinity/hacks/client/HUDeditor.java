package me.leon.trinity.hacks.client;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Color;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.sub.SubColor;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public class HUDeditor extends Module {
    public static Boolean clamp = new Boolean("Clamp", true);
    public static Boolean anchor = new Boolean("Anchor", true);
    public static SettingParent background = new SettingParent("Background", true, true);
    public static SubColor color = new SubColor("Color", background, 97, 97, 97, 97, false);
    public static Color textColor = new Color("TextColor", 255, 255, 255, 255, false);

    public HUDeditor() {
        super("HUDeditor", "Edit the HUD", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Trinity.hudEditor);
        ClickGUI.loadShader();
        this.setEnabled(false);
    }

    @EventHandler
    private final Listener<EventModeChange> toggleListener = new Listener<>(event -> {
        ClickGUI.updateShader(event);
    });
}
