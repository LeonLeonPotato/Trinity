package me.leon.trinity.hacks.combat.autocrystal;

import me.leon.trinity.hacks.combat.AutoCrystal;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.world.DamageUtils;
import me.leon.trinity.utils.world.RaytraceUtils;
import me.leon.trinity.utils.world.WorldUtils;
import me.zero.alpine.fork.listener.Listenable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static me.leon.trinity.hacks.combat.AutoCrystal.*;
import static me.leon.trinity.hacks.combat.autocrystal.Utility.*;

public class Placer implements Util, Listenable {
    private Mapper mapper;

    public Placer() {
        mapper = new Mapper(this);
    }

    public void placeCrystal() {
        if(nullCheck()) return;
        if(!Place.getValue()) return;

        if(threadedPlace.getValue()) {
            new Mapper(this).start();
        } else mapper.run();

        if(curPosPlace == null) return;
        if(EntityUtils.isInHole(target) && limit.getValue() && (target.hurtTime != 0)) return;
        if(!placeTimer.hasPassAndReset((int) placeDelay.getValue() * 50)) return;
        if(getSelfHealth() + 1 < pauseHealth.getValue() && (noSuicide.getValue().equalsIgnoreCase("Place") || noSuicide.getValue().equalsIgnoreCase("Both"))) return;

        final int oldslot = mc.player.inventory.currentItem;

        switch (switchPlace.getValue()) {
            case "None": {
                if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) return;
                mc.playerController.updateController();
                break;
            }
            case "Packet":
            case "Normal": {
                if(InventoryUtil.amountInHotbar(Items.END_CRYSTAL, false) <= 0) return;
                InventoryUtil.switchToSlot(InventoryUtil.find(Items.END_CRYSTAL));
            }
        }

        place(curPosPlace.getBase());
        swingHand(false);

        placedCrystals.add(curPosPlace);

        if(switchPlace.getValue().equalsIgnoreCase("Packet")) {
            mc.player.inventory.currentItem = oldslot;
            mc.playerController.updateController();
        }
    }

    private boolean filterReachable(BlockPos pos) {
        switch (rayTracePlaceMode.getValue()) {
            case "Leon": {
                final Vec3d vec = RaytraceUtils.rayTraceLeon(pos);
                final boolean walls = (vec == null);
                if(walls)
                {
                    final EnumFacing facing = getEnumFacing(true, pos);
                    final Vec3d rayTraceVec = new Vec3d(pos.x + 0.5D, pos.y + 0.5D, pos.z + 0.5D).add(new Vec3d(facing.getDirectionVec()).scale(0.51f));
                    if(WorldUtils.isWithin(placeRangeWalls.getValue(), eyePos(), rayTraceVec))
                        return true;
                } else
                {
                    if(WorldUtils.isWithin(placeRange.getValue(), eyePos(), vec) || !doubleCheck.getValue())
                        return true;
                }
                break;
            }
            case "Offset":
            case "Simple": {
                final EnumFacing facing = getEnumFacing(true, pos);
                final Vec3d rayTraceVec = new Vec3d(pos.x + 0.5D, pos.y + 0.5D, pos.z + 0.5D).add(new Vec3d(facing.getDirectionVec()).scale(0.51f));
                final boolean walls = RaytraceUtils.rayTraceSimple(rayTraceVec, rayTracePlaceMode.getValue().equalsIgnoreCase("Offset") ? offsetPlace.getValue() : 0.0D);
                if(walls) {
                    if(WorldUtils.isWithin(placeRangeWalls.getValue(), eyePos(), rayTraceVec))
                        return true;
                } else {
                    if(WorldUtils.isWithin(placeRange.getValue(), eyePos(), rayTraceVec) || !doubleCheck.getValue())
                        return true;
                }
                break;
            }
        }
        return false;
    }

    private boolean filterDamage(CrystalPosition possible) {
        if(eyePos().squareDistanceTo(new Vec3d(possible.getBase())) > placeRange.getValue() * placeRange.getValue()) return false;
        if (!EntityUtils.isInHole(target)) {
            if (possible.getSelfDamage() <= maxSelfDamagePlace.getValue()) {
                if (possible.getTargetDamage() > minTargetDamagePlace.getValue()) {
                    return true;
                }
            }
        } else if (facePlace.getValue()){
            if (!forceBind.down()) {
                if (target instanceof EntityPlayer) {
                    if ((EntityUtils.getArmor((EntityPlayer) target, armorBreakerScale.getValue()) && armorBreaker.getValue())
                            || getTargetHealth() < facePlaceMinHealth.getValue()
                            || (possible.getTargetDamage() > facePlaceMinDamage.getValue() && possible.getSelfDamage() <= facePlaceMinHealth.getValue()))
                    {
                        if (possible.getSelfDamage() <= facePlaceMinHealth.getValue()) {
                            return possible.getTargetDamage() > facePlaceMinDamage.getValue();
                        }
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean filterPlaceable(BlockPos blockPos) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final AxisAlignedBB bb = new AxisAlignedBB(boost).expand(0, 1, 0);
        if ((mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                && (version.getValue().equalsIgnoreCase("1.13+") || (mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(boost.up()).getBlock() == Blocks.AIR))) {
            for (final Entity en : mc.world.getEntitiesWithinAABB(Entity.class, bb)) {
                if (extraCalc.getValue()) {
                    if (multiPlace.getValue()) {
                        if (!en.isDead) {
                            return false;
                        }
                    } else {
                        if (!en.isDead && !(en instanceof EntityEnderCrystal)) {
                            return false;
                        } else if (en instanceof EntityEnderCrystal) {
                            if (!en.getPosition().down().equals(blockPos)) {
                                return false;
                            }
                        }
                    }
                } else {
                    if (multiPlace.getValue()) {
                        if (!en.isDead) {
                            return false;
                        }
                    } else {
                        if (!en.isDead && !(en instanceof EntityEnderCrystal)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void place(BlockPos pos) {
        final EnumFacing facing = getEnumFacing(bounds.getValue(), pos);
        final Vec3d vec = new Vec3d(facing.getDirectionVec()).scale(0.5D).add(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
        rotate(false, vec);

        if(packetPlace.getValue()) {
            final CPacketPlayerTryUseItemOnBlock packet = new CPacketPlayerTryUseItemOnBlock();
            packet.position = pos;
            packet.hand = getCrystalHand();
            packet.placedBlockDirection = facing;
            packet.facingX = (float) vec.x - pos.getX();
            packet.facingY = (float) vec.y - pos.getY();
            packet.facingZ = (float) vec.z - pos.getZ();
            mc.player.connection.sendPacket(packet);
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing, vec, getCrystalHand());
        }
    }

    private float heuristic(CrystalPosition pos) {
        switch (heuristic.getValue()) {
            case "Target": {
                return pos.getTargetDamage();
            }
            case "Self": {
                return pos.getSelfDamage();
            }
            case "Subtract": {
                return pos.getTargetDamage() - pos.getSelfDamage();
            }
            case "Atomic": {
                return (float) (pos.getTargetDamage() - (pos.getSelfDamage() + mc.player.getPositionVector().distanceTo(pos.getCrystalVec())));
            }
            default: {
                return 0.0f;
            }
        }
    }

    private static class Mapper extends Thread {
        private final Placer placer;

        private Mapper(Placer placer) {
            this.placer = placer;
        }

        @Override
        public void run() {
            if(nullCheck()) return;
            if(!Place.getValue()) return;

            if(target == null) {
                reset();
                return;
            }

            final List<CrystalPosition> filtered = WorldUtils.getSphere(EntityUtils.getEntityPosFloored(mc.player), (float) placeRange.getValue(), (int) placeRange.getValue(), false, true, 0)
                    .stream()
                    .filter(placer::filterPlaceable)
                    .filter(placer::filterReachable)
                    .map(e -> new CrystalPosition(calcDamage(e, target), calcDamage(e, mc.player), e))
                    .filter(placer::filterDamage)
                    .collect(Collectors.toList());

            CrystalPosition finalPosition;

            if(placeMode.getValue().equals("Damage")) {
                if(heuristic.getValue().equals("Self")) {
                    finalPosition = filtered.stream().min(Comparator.comparing(placer::heuristic)).orElse(null);
                } else {
                    finalPosition = filtered.stream().max(Comparator.comparing(placer::heuristic)).orElse(null);
                }
            } else {
                finalPosition = filtered.stream().min(Comparator.comparing(e -> e.getCrystalVec().squareDistanceTo(target.getPositionVector()))).orElse(null);
            }

            curPosPlace = finalPosition;
        }
    }
}
