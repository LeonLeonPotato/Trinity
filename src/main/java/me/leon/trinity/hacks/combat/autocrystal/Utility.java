package me.leon.trinity.hacks.combat.autocrystal;

import me.leon.trinity.hacks.combat.AutoCrystal;
import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.world.DamageUtils;
import me.leon.trinity.utils.world.Priority;
import me.leon.trinity.utils.world.Rotation.RotationUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import static me.leon.trinity.hacks.combat.AutoCrystal.*;

public class Utility implements Util {
    public static void swingHand(boolean Break) {
        if(!Break)
            switch (swingArmPlace.getValue()) {
                case "Mainhand": {
                    if (packetSwingPlace.getValue())
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    else mc.player.swingArm(EnumHand.MAIN_HAND);
                    break;
                }
                case "Offhand": {
                    if (packetSwingPlace.getValue())
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                    else mc.player.swingArm(EnumHand.OFF_HAND);
                    break;
                }
                case "Both": {
                    if (packetSwingPlace.getValue()) {
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    } else {
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.swingArm(EnumHand.OFF_HAND);
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        else
        if (curBreakCrystal != null) {
            switch (swingArmBreak.getValue()) {
                case "Mainhand": {
                    if (packetSwingBreak.getValue())
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    else
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                    break;
                }
                case "Offhand": {
                    if (packetSwingBreak.getValue())
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                    else
                        mc.player.swingArm(EnumHand.OFF_HAND);
                    break;
                }
                case "Both": {
                    if (packetSwingBreak.getValue()) {
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    } else {
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.swingArm(EnumHand.OFF_HAND);
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    public static void rotate(boolean Break, Vec3d vec) {
        if (Break && AutoCrystal.breakRotate.getValue()) {
            RotationUtils.rotateTowards(vec, !clientRotate.getValue(), Priority.Highest);
        }
        if (!Break && placeRotate.getValue()) {
            RotationUtils.rotateTowards(vec, !clientRotate.getValue(), Priority.Highest);
        }
    }

    /**
     * there has to be a way to simplify this...
     * @return EnumHand
     */
    public static EnumHand getCrystalHand() {
        if(mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            return EnumHand.MAIN_HAND;
        } else if(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL){
            return EnumHand.OFF_HAND;
        }
        return null;
    }

    public static EnumFacing getEnumFacing(boolean rayTrace, BlockPos placePosition) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(placePosition.getX() + 0.5, placePosition.getY() + 0.5, placePosition.getZ() + 0.5));

        if (placePosition.getY() == 255)
            return EnumFacing.DOWN;

        if (rayTrace) {
            return (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        }

        return EnumFacing.UP;
    }

    public static float calcDamage(EntityEnderCrystal crystal, EntityLivingBase entity) {
        return calcDamage(crystal.getPositionVector(), entity);
    }

    public static float calcDamage(BlockPos pos, EntityLivingBase entity) {
        return calcDamage(new Vec3d(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5), entity);
    }

    public static float calcDamage(Vec3d vec, EntityLivingBase entity) {
        if(entity == null) return 0.0f;
        if(entity == mc.player) {
            return DamageUtils.calculateDamage(vec, entity, selfPredict.getValue(), (int) selfPredictTicks.getValue());
        } else {
            return DamageUtils.calculateDamage(vec, entity, predict.getValue(), (int) predictTicks.getValue());
        }
    }

    public static float getSelfHealth() {
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    public static float getTargetHealth() {
        return target.getHealth() + target.getAbsorptionAmount();
    }

    public static boolean nullCheck() {
        return mc.player == null || mc.world == null;
    }

    public static Vec3d eyePos() {
        return EntityUtils.getEyePos(mc.player);
    }

    public static void reset() {
        placeTimer.reset();
        breakTimer.reset();
        placedCrystals.clear();
        curPosPlace = null;
        curBreakCrystal = null;
    }
}
