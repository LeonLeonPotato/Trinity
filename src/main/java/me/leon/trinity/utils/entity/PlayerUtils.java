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
}
