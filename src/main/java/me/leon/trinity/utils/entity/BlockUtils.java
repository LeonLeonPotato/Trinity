package me.leon.trinity.utils.entity;

import me.leon.trinity.managers.RotationManager;
import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.world.Rotation.Rotation;
import me.leon.trinity.utils.world.Rotation.RotationPriority;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;


public class BlockUtils implements Util {

    public static boolean isInterceptedByOther(BlockPos blockPos) {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity.equals(mc.player))
                continue;

            if (new AxisAlignedBB(blockPos).intersects(entity.getEntityBoundingBox()))
                return true;
        }

        return false;
    }


    public static boolean isCollidedBlocks(BlockPos pos) {
        return getBlockResistance(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) == BlockResistance.Resistant || isInterceptedByOther(pos) || InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN) == -1;
    }

    public static void placeBlock(BlockPos pos, boolean rotate, boolean strict, boolean raytrace, boolean packet, boolean swingArm, boolean antiGlitch) {
        for (EnumFacing enumFacing : EnumFacing.values()) {
            if (!(getBlockResistance(pos.offset(enumFacing)) == BlockResistance.Blank) && !EntityUtils.isIntercepted(pos)) {
                Vec3d vec = new Vec3d(pos.getX() + 0.5D + (double) enumFacing.getXOffset() * 0.5D, pos.getY() + 0.5D + (double) enumFacing.getYOffset() * 0.5D, pos.getZ() + 0.5D + (double) enumFacing.getZOffset() * 0.5D);

                float[] old = new float[] {
                        mc.player.rotationYaw, mc.player.rotationPitch
                };

                if (strict)
                    RotationManager.rotationQueue.add(new Rotation((float) Math.toDegrees(Math.atan2((vec.z - mc.player.posZ), (vec.x - mc.player.posX))) - 90.0F, (float) (-Math.toDegrees(Math.atan2((vec.y - (mc.player.posY + (double) mc.player.getEyeHeight())), (Math.sqrt((vec.x - mc.player.posX) * (vec.x - mc.player.posX) + (vec.z - mc.player.posZ) * (vec.z - mc.player.posZ)))))), Rotation.RotationMode.Packet, RotationPriority.High));

                if (rotate)
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation((float) Math.toDegrees(Math.atan2((vec.z - mc.player.posZ), (vec.x - mc.player.posX))) - 90.0F, (float) (-Math.toDegrees(Math.atan2((vec.y - (mc.player.posY + (double) mc.player.getEyeHeight())), (Math.sqrt((vec.x - mc.player.posX) * (vec.x - mc.player.posX) + (vec.z - mc.player.posZ) * (vec.z - mc.player.posZ)))))), mc.player.onGround));

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

                if (packet)
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, raytrace ? enumFacing : EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
                else
                    mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(enumFacing), raytrace ? enumFacing.getOpposite() : EnumFacing.UP, new Vec3d(pos), EnumHand.MAIN_HAND);

                if (swingArm)
                    mc.player.swingArm(EnumHand.MAIN_HAND);

                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

                if (rotate)
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(old[0], old[1], mc.player.onGround));

                if (antiGlitch)
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos.offset(enumFacing), enumFacing.getOpposite()));

                return;
            }
        }
    }


    public static BlockResistance getBlockResistance(BlockPos block) {
        if (mc.world.isAirBlock(block))
            return BlockResistance.Blank;

        else if (mc.world.getBlockState(block).getBlock().getBlockHardness(mc.world.getBlockState(block), mc.world, block) != -1 && !(mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST)))
            return BlockResistance.Breakable;

        else if (mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST))
            return BlockResistance.Resistant;

        else if (mc.world.getBlockState(block).getBlock().equals(Blocks.BEDROCK))
            return BlockResistance.Unbreakable;

        return null;
    }
    public enum BlockResistance {
        Blank,
        Breakable,
        Resistant,
        Unbreakable
    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        if (!mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
            sneaking = true;
        }

        if (rotate) {
            faceVector(hitVec, true);
        }

        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4; //?
        return sneaking || isSneaking;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        List<EnumFacing> facings = new ArrayList<>();
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                IBlockState blockState = mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        for (EnumFacing facing : getPossibleSides(pos)) {
            return facing;
        }
        return null;
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
        };
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = getLegitRotations(vec);
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], mc.player.onGround));
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.x - (double) pos.getX());
            float f1 = (float) (vec.y - (double) pos.getY());
            float f2 = (float) (vec.z - (double) pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4; //?
    }
}