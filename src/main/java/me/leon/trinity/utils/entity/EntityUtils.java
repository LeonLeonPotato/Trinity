package me.leon.trinity.utils.entity;

import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;

public class EntityUtils implements Util {
    public static EntityLivingBase getTarget(boolean players, boolean neutral, boolean friends, boolean hostile, boolean passive, double range, int mode) {
        EntityLivingBase entity = null;

        if(mode == 0) {
            entity = (EntityLivingBase) mc.world.loadedEntityList.stream()
                    .filter(entity1 -> isValid(entity1, players, neutral, friends, hostile, passive, range))
                    .min(Comparator.comparing(entity1 -> mc.player.getPositionVector().distanceTo(entity1.getPositionVector())))
                    .orElse(null);
        } else
        if(mode == 1) {
            entity = mc.world.loadedEntityList.stream()
                    .filter(entity1 -> isValid(entity1, players, neutral, friends, hostile, passive, range))
                    .map(entity1 -> (EntityLivingBase) entity1)
                    .min(Comparator.comparing(EntityLivingBase::getHealth))
                    .orElse(null);
        } else
        if(mode == 2) {
            entity = mc.world.loadedEntityList.stream()
                    .filter(entity1 -> isValid(entity1, players, neutral, friends, hostile, passive, range))
                    .map(entity1 -> (EntityLivingBase) entity1)
                    .max(Comparator.comparing(EntityLivingBase::getHealth))
                    .orElse(null);
        }
        return entity;
    }

    public static ArrayList<EntityLivingBase> getTargets(boolean players, boolean neutral, boolean friends, boolean hostile, boolean passive, double range, int mode) {
        ArrayList<EntityLivingBase> toReturn = new ArrayList<>();

        if(mode == 0) {
            toReturn.add(mc.world.loadedEntityList.stream()
                    .filter(entity1 -> isValid(entity1, players, neutral, friends, hostile, passive, range))
                    .map(entity1 -> (EntityLivingBase) entity1)
                    .min(Comparator.comparing(entity1 -> mc.player.getPositionVector().distanceTo(entity1.getPositionVector())))
                    .orElse(null));
        } else
        if(mode == 1) {
            toReturn.add(mc.world.loadedEntityList.stream()
                    .filter(entity1 -> isValid(entity1, players, neutral, friends, hostile, passive, range))
                    .map(entity1 -> (EntityLivingBase) entity1)
                    .min(Comparator.comparing(EntityLivingBase::getHealth))
                    .orElse(null));
        } else
        if(mode == 2) {
            toReturn.add(mc.world.loadedEntityList.stream()
                    .filter(entity1 -> isValid(entity1, players, neutral, friends, hostile, passive, range))
                    .map(entity1 -> (EntityLivingBase) entity1)
                    .max(Comparator.comparing(EntityLivingBase::getHealth))
                    .orElse(null));
        }
        return toReturn;
    }

    private static boolean isValid(Entity entity, boolean players, boolean neutral, boolean friends, boolean hostile, boolean passive, double range) {
        if(entity instanceof EntityLivingBase && entity != mc.player) {
            if(entity.getDistance(mc.player) <= range) {
                if(entity instanceof EntityPlayer && players) {
                    if(!friends) {
                        return !Trinity.friendManager.isFriend((EntityPlayer) entity);
                    } else {
                        return true;
                    }
                }
                if(isHostileMob(entity) && hostile) return true;
                if(isNeutralMob(entity) && neutral) return true;
                return isPassive(entity) && passive;
            }
        }
        return false;
    }

    public static boolean isPassive(Entity entity) {
        if (entity instanceof EntityWolf && ((EntityWolf) entity).isAngry())
            return false;

        if (entity instanceof EntityAgeable || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid)
            return true;

        return entity instanceof EntityIronGolem && ((EntityIronGolem) entity).getRevengeTarget() == null;
    }

    public static boolean isVehicle(Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart;
    }

    public static boolean isHostileMob(Entity entity) {
        return (entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity)) || entity instanceof EntitySpider;
    }

    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }

    public static int toMode(String mode) {
        if(mode.equalsIgnoreCase("Closest")) {
            return 0;
        } else
        if(mode.equalsIgnoreCase("Lowest Health")) {
            return 1;
        } else
        if(mode.equalsIgnoreCase("Highest Health")) {
            return 2;
        } else throw new IllegalArgumentException(mode);
    }

    public static double getRange(Entity entity) {
        return mc.player.getPositionVector().add(0, mc.player.eyeHeight, 0).distanceTo(entity.getPositionVector().add(0, entity.height / 2d, 0));
    }

    public static Vec3d interpolateEntity(Entity entity, float n) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * n, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * n, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * n);
    }

    public static Vec3d interpolateEntityTime(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) time);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
        return getInterpolatedAmount(entity, vec.x, vec.y, vec.z);
    }

    public static double calculateDistanceWithPartialTicks(double originalPos, double finalPos, float renderPartialTicks) {
        return finalPos + (originalPos - finalPos) * mc.getRenderPartialTicks();
    }

    public static Vec3d interpolateEntityByTicks(Entity entity, float renderPartialTicks) {
        return new Vec3d (calculateDistanceWithPartialTicks(entity.posX, entity.lastTickPosX, renderPartialTicks) - mc.getRenderManager().renderPosX, calculateDistanceWithPartialTicks(entity.posY, entity.lastTickPosY, renderPartialTicks) - mc.getRenderManager().renderPosY, calculateDistanceWithPartialTicks(entity.posZ, entity.lastTickPosZ, renderPartialTicks) - mc.getRenderManager().renderPosZ);
    }

    public static double getDistance(double x, double y, double z, double finalX, double finalY, double finalZ) {
        double interpolationX = x - finalX;
        double interpolationY = y - finalY;
        double interpolationZ = z - finalZ;
        return MathHelper.sqrt(interpolationX * interpolationX + interpolationY * interpolationY + interpolationZ * interpolationZ);
    }

    public static BlockPos getEntityPosFloored(Entity entity) {
        return new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ));
    }
}
