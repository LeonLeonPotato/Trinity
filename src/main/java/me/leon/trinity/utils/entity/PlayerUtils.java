package me.leon.trinity.utils.entity;

import me.leon.trinity.utils.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlayerUtils implements Util {
    public static BlockPos getPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static Vec3d getCenter(double posX, double posY, double posZ) {
        return new Vec3d(Math.floor(posX) + 0.5D, Math.floor(posY), Math.floor(posZ) + 0.5D);
    }

    public static double getReach() {
        return Minecraft.getMinecraft().player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
    }

    public static void doStrafe(final float speed) {
        final float moveForward = mc.player.movementInput.moveForward;
        final float moveStrafe = mc.player.movementInput.moveStrafe;
        final float rotationYaw = mc.player.rotationYaw;

        mc.player.motionX = ((moveForward * speed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))) + (moveStrafe * speed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))));
        mc.player.motionZ = ((moveForward * speed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))) - (moveStrafe * speed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))));
    }

    public static double[] motion(final float speed) {
        final float moveForward = mc.player.movementInput.moveForward;
        final float moveStrafe = mc.player.movementInput.moveStrafe;
        final float rotationYaw = mc.player.rotationYaw;

        double x = ((moveForward * speed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))) + (moveStrafe * speed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))));
        double z = ((moveForward * speed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))) - (moveStrafe * speed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))));

        return new double[] {x, z};
    }
}
