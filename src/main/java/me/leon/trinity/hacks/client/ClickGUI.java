package me.leon.trinity.hacks.client;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventBooleanToggle;
import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.Color;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.Slider;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class ClickGUI extends Module {
    public static Mode background = new Mode("Background", "Blur", "None", "Blur", "Darken", "Both");
    public static Slider width = new Slider("Width", 100, 125, 200, true);

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
}
