package me.leon.trinity.hacks.client;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.sub.SubBoolean;
import me.leon.trinity.setting.settings.sub.SubColor;
import me.leon.trinity.setting.settings.sub.SubMode;
import me.leon.trinity.setting.settings.sub.SubSlider;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.util.ResourceLocation;

public class ClickGUI extends Module {
    public static SettingParent main = new SettingParent("Main", true, false);
    public static SubMode background = new SubMode("Background", main, "Blur", "None", "Blur", "Darken", "Both");
    public static SubBoolean scroll = new SubBoolean("Scroll", main, true);
    public static SubSlider speed = new SubSlider("Scroll speed", main, 0, 10, 50, true);
    public static SubBoolean pause = new SubBoolean("Pause Game", main, false);

    public static SettingParent frame = new SettingParent("Frame", true, false);
    public static SubColor frameColor = new SubColor("Frame Color", frame, 60, 63, 65, 255, false);
    public static SubColor nameColorFrame = new SubColor("Frame Name", frame, 169, 183, 198, 255, false);
    public static SubBoolean rainbow = new SubBoolean("Rainbow Bar", frame, true);
    public static SubSlider width = new SubSlider("Width", frame, 100, 125, 200, true);

    public static SettingParent button = new SettingParent("Button", true, false);
    public static SubColor nameColorButton = new SubColor("Button Name", button, 169, 183, 198, 255, false);
    public static SubColor disabledColor = new SubColor("Disabled Color", button, 43, 43, 43, 200, false);
    public static SubColor enabledColor = new SubColor("Enabled Color", button, 97, 97, 97, 200, true);

    public static SettingParent settings = new SettingParent("Settings", true, false);
    public static SubColor nameColorSetting = new SubColor("Setting Name", settings, 169, 183, 198, 255, false);
    public static SubColor disabledBooleanColor = new SubColor("Disabled Color", settings, 43, 43, 43, 200, false);
    public static SubColor enabledBooleanColor = new SubColor("Enabled Color", settings, 97, 97, 97, 200, true);
    public static SubColor sliderColor = new SubColor("Slider Color", settings, 43, 43, 43, 200, false);
    public static SubMode barMode = new SubMode("Bar Mode", settings, "Rainbow", "Rainbow", "Static", "None");
    public static SubColor barColor = new SubColor("Bar Color", settings, 217, 217, 217, 200, true);

    public ClickGUI() {
        super("ClickGUI", "The ClickGUI of the client", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Trinity.clickGui);
        if(background.getValue().equalsIgnoreCase("Blur") || background.getValue().equalsIgnoreCase("Both")) {
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
    }

    @Override
    public void onDisable() {
        mc.displayGuiScreen(null);
        if(background.getValue().equalsIgnoreCase("Blur") || background.getValue().equalsIgnoreCase("Both")) {
            mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
    }

    @EventHandler
    private final Listener<EventModeChange> toggleListener = new Listener<>(event -> {
        if(event.getStage() != EventStage.POST) return;
        if(event.getSet() == background) {
            if(!background.getValue().equalsIgnoreCase("Blur") && !background.getValue().equalsIgnoreCase("Both")) {
                mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            } else
            if(background.getValue().equalsIgnoreCase("Blur") || background.getValue().equalsIgnoreCase("Both")) {
                mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
            }
        }
    });

    @Override
    public boolean shouldSave() {
        return false;
    }
}
