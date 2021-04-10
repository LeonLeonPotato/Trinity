package me.leon.trinity.hacks.movement;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("Sprint", "Automatically sprints", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if(nullCheck()) return;
    }

    @Override
    public void onUpdate() {
        if (nullCheck())
            return;

        if (mc.player.movementInput.moveForward > 0 && !mc.player.isSneaking() && !mc.player.collidedHorizontally) {
            mc.player.setSprinting(true);
        }
    }
}
