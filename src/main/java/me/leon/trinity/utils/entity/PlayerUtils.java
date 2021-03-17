package me.leon.trinity.utils.entity;

import me.leon.trinity.utils.Util;
import net.minecraft.util.math.BlockPos;

public class PlayerUtils implements Util {
    public static BlockPos getPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }
}
