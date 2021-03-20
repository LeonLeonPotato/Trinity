package me.leon.trinity.hacks.movement;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;

public class Yaw extends Module {
    public Yaw(){
        super("Yaw","locks your rotation", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (nullCheck())
            return;

        int angle = 90;

        float yaw = mc.player.rotationYaw;

        yaw = Math.round(yaw / angle) * angle;

            mc.player.rotationYaw = yaw;
    }
}