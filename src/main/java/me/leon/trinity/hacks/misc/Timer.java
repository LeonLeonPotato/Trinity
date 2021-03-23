package me.leon.trinity.hacks.misc;

import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;

public class Timer extends Module {
    public Timer() {
        super("Timer", "Spawns in a fakeplayer", Category.MISC);
    }

    private static final Slider speed = new Slider("Speed", 0, 3, 20, false);

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50.0f;
    }
    @Override
    public void onUpdate() {
        mc.timer.tickLength = 50.0F / ((float)this.speed.getValue() == 0.0F ? 0.1F : (float)this.speed.getValue());
    }
}
