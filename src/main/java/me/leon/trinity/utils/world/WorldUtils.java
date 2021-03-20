package me.leon.trinity.utils.world;

import me.leon.trinity.utils.Util;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class WorldUtils implements Util {
    public static Block getBlock(BlockPos block) {
        return mc.world.getBlockState(block).getBlock();
    }
}
