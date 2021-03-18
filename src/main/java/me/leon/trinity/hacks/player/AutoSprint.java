package me.leon.trinity.hacks.player;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("Sprint", "Automatically sprints", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if(nullCheck())
            return;

        if (mc.player.movementInput.moveForward > 0 && !mc.player.isSneaking() && !mc.player.collidedHorizontally) {
            mc.player.setSprinting(true);
        }
    }
}
