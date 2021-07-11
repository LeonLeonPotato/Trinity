package me.leon.trinity.utils.world;

import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.entity.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldUtils implements Util {
	public static final ArrayList<Block> empty = new ArrayList<>(Arrays.asList(Blocks.AIR, Blocks.VINE, Blocks.SNOW_LAYER, Blocks.TALLGRASS, Blocks.FIRE, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.FLOWING_WATER, Blocks.WATER));

	public static final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER,
			Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);

	public static Block getBlock(BlockPos block) {
		return mc.world.getBlockState(block).getBlock();
	}

	public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
		List<BlockPos> circleblocks = new ArrayList<>();
		int cx = loc.getX();
		int cy = loc.getY();
		int cz = loc.getZ();
		for (int x = cx - (int) r; x <= cx + r; x++) {
			for (int z = cz - (int) r; z <= cz + r; z++) {
				for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
					if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
						circleblocks.add(new BlockPos(x, y + plus_y, z));
					}
				}
			}
		}
		return circleblocks;
	}

	public static EnumFacing getPlaceableSide(BlockPos pos) {

		for (EnumFacing side : EnumFacing.values()) {

			BlockPos neighbour = pos.offset(side);

			if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
				continue;
			}

			IBlockState blockState = mc.world.getBlockState(neighbour);
			if (!blockState.getBlock().getMaterial(blockState).isReplaceable()) {
				return side;
			}
		}

		return null;
	}

	public static double getRange(Vec3d vec) {
		return mc.player.getPositionVector().add(0, mc.player.eyeHeight, 0).distanceTo(vec);
	}

	public static EnumFacing getEnumFacing(boolean rayTrace, BlockPos placePosition) {
		RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(placePosition.getX() + 0.5, placePosition.getY() - 0.5, placePosition.getZ() + 0.5));

		if (placePosition.getY() == 255)
			return EnumFacing.DOWN;

		if (rayTrace) {
			return (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
		}

		return EnumFacing.UP;
	}

	public static boolean canBeClicked(BlockPos pos) {
		return mc.world.getBlockState(pos).getBlock().canCollideCheck(mc.world.getBlockState(pos), false);
	}

	public static boolean isWithin(double distance, Vec3d vec, Vec3d vec2) {
		return vec.squareDistanceTo(vec2) <= distance * distance;
	}

	public static boolean isOutside(double distance, Vec3d vec, Vec3d vec2) {
		return vec.squareDistanceTo(vec2) > distance * distance;
	}

	private boolean place(BlockPos pos) {
		boolean isSneaking = mc.player.isSneaking();

		Block block = mc.world.getBlockState(pos).getBlock();

		if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockFire)) {
			return false;
		}

		EnumFacing side = WorldUtils.getPlaceableSide(pos);

		if (side == null) {
			return false;
		}

		BlockPos neighbour = pos.offset(side);
		EnumFacing opposite = side.getOpposite();

		if (!WorldUtils.canBeClicked(neighbour)) {
			return false;
		}

		Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
		Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

		if (!isSneaking && WorldUtils.blackList.contains(neighbourBlock)) {
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			isSneaking = true;
		}

		mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
		mc.player.swingArm(EnumHand.MAIN_HAND);

		return true;
	}
}
