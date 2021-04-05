package me.leon.trinity.hacks.client;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Color;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.sub.SubBoolean;
import me.leon.trinity.setting.settings.sub.SubColor;
import me.leon.trinity.setting.settings.sub.SubMode;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public class HUDeditor extends Module {
    public static Boolean clamp = new Boolean("Clamp", true);
    public static Boolean anchor = new Boolean("Anchor", true);
    public static SettingParent background = new SettingParent("Background", true, true);
    public static SubColor color = new SubColor("Color", background, 97, 97, 97, 97, false);
    public static Color textColor = new Color("TextColor", 255, 255, 255, 255, false);

    public static SettingParent MLC = new SettingParent("ModuleList", true, false);
    public static SubMode MLCMode = new SubMode("Mode", MLC, "AlphaPulse", "Rainbow", "AlphaPulse", "AlphaPulseStatic", "RainbowStatic", "Static");
    public static SubMode MLCBackground = new SubMode("MLCBackground", MLC, "Lines", "Lines", "None", "Fill");
    public static SubBoolean MLCLine = new SubBoolean("Line", MLC, true);
    public static SubColor MLCcolor = new SubColor("Color", MLC, 255, 255, 0, 255, true);
    public static SubColor MLCBackgroundColor = new SubColor("Background", MLC, 97, 97, 97, 97, false);

    public static SettingParent Coords = new SettingParent("Coords", true, false);
    public static SubBoolean netherCoords = new SubBoolean("Nether", Coords, true);

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
