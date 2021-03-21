package me.leon.trinity.hacks.render;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;

public class NoBob extends Module {
    public NoBob() {
        super("NoBob", "Removes bobbing animation", Category.RENDER);
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;
        mc.gameSettings.viewBobbing = false;
    }
}