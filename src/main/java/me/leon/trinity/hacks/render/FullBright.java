package me.leon.trinity.hacks.render;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Mode;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class FullBright extends Module {
    Mode mode = new Mode("Mode", "Gamma", "Gamma", "Potion");

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
                mc.player.addPotionEffect(new PotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 80950, 1, false, false)));
                break;
            }
        }
    }
}
