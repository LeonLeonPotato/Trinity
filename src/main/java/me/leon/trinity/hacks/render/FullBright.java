package me.leon.trinity.hacks.render;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Mode;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class FullBright extends Module {
    private final Mode mode = new Mode("Mode", "Gamma", "Gamma", "Potion");
    private float gamma = 0;

    public FullBright() {
        super("FullBright", "Makes your world bright", Category.RENDER);
    }

    @SuppressWarnings("all")
    public void onUpdate() {
        if(nullCheck()) return;

        switch (mode.getValue()) {
            case "Gamma": {
                mc.gameSettings.gammaSetting = 999f;
                break;
            }
            case "Potion": {
                mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 80950, 1, false, false));
                break;
            }
        }
    }

    @EventHandler
    private final Listener<EventModeChange> onModeChange = new Listener<>(event -> {
        if(event.getStage() != EventStage.POST) return;
        if(event.getSet() == mode) {
            if(mode.getValue().equalsIgnoreCase("Gamma")) {
                mc.player.removeActivePotionEffect(MobEffects.NIGHT_VISION);
            }
        }
    });

    @Override
    public void onDisable() {
        if(nullCheck()) return;
        mc.gameSettings.gammaSetting = gamma;
        mc.player.removeActivePotionEffect(MobEffects.NIGHT_VISION);
    }

    @Override
    public void onEnable() {
        this.gamma = mc.gameSettings.gammaSetting;
    }
}
