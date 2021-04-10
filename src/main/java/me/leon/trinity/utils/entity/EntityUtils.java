package me.leon.trinity.utils.entity;

import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.world.HoleUtils;
import me.leon.trinity.utils.world.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;

public class EntityUtils implements Util {
    public static double GetDistance(double p_X, double p_Y, double p_Z, double x, double y, double z)
    {
        double d0 = p_X - x;
        double d1 = p_Y - y;
        double d2 = p_Z - z;
        return (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

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

    public static ArrayList<Block> isColliding(double posX, double posY, double posZ, Entity entity) {
        ArrayList<Block> block = new ArrayList<>();
        if (entity != null) {
            final AxisAlignedBB bb = entity.ridingEntity != null ? entity.ridingEntity.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ) : entity.getEntityBoundingBox().contract(0.0d, 1d, 0.0d).offset(posX, posY, posZ);
            int y = (int) bb.minY;
            for (int x = (int) Math.floor(bb.minX); x < Math.floor(bb.maxX) + 1; x++) {
                for (int z = (int) Math.floor(bb.minZ); z < Math.floor(bb.maxZ) + 1; z++) {
                    block.add(mc.world.getBlockState(new BlockPos(x, y, z)).getBlock());
                }
            }
        }
        return block;
    }

    public static ArrayList<BlockPos> getPos(double posX, double posY, double posZ, Entity entity) {
        ArrayList<BlockPos> block = new ArrayList<>();
        if (entity != null) {
            final AxisAlignedBB bb = entity.ridingEntity != null ? entity.ridingEntity.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ) : entity.getEntityBoundingBox().contract(0.01d, 1d, 0.01d).offset(posX, posY, posZ);
            int y = (int) bb.minY;
            for (int x = (int) Math.floor(bb.minX); x < Math.floor(bb.maxX) + 1; x++) {
                for (int z = (int) Math.floor(bb.minZ); z < Math.floor(bb.maxZ) + 1; z++) {
                    block.add(new BlockPos(x, y, z));
                }
            }
        }
        return block;
    }

    public static boolean isTrapped(EntityLivingBase entity, boolean antiStep) {
        HoleUtils.Hole hole = HoleUtils.getHole(getEntityPosFloored(entity), -1);
        boolean isInHole = isInHole(entity);
        BlockPos entityPos = getEntityPosFloored(entity);
        if(isInHole && hole != null) {
            if(hole instanceof HoleUtils.DoubleHole) {
                final HoleUtils.DoubleHole dHole = (HoleUtils.DoubleHole) hole;
                final int[][] offsets = {
                        {1, 0, 0},
                        {0, 0, -1},
                        {0, 0, 1},
                        {-1, 0, 0}
                };

                int checked = 0;

                for(int[] off : offsets) {
                    final BlockPos pos = dHole.pos.add(off[0], off[1], off[2]);
                    if(pos.equals(dHole.pos1)) continue;

                    final Block block = WorldUtils.getBlock(pos);
                    if(!WorldUtils.empty.contains(block)) checked++;

                    final BlockPos pos0 = dHole.pos.add(off[0], off[1] + 1, off[2]);
                    if(pos0.equals(dHole.pos1.add(0, off[1] + 1, 0))) continue;

                    final Block block0 = WorldUtils.getBlock(pos0);
                    if(!WorldUtils.empty.contains(block0)) checked++;
                }

                for(int[] off : offsets) {
                    final BlockPos pos = dHole.pos1.add(off[0], off[1], off[2]);
                    if(pos.equals(dHole.pos)) continue;

                    final Block block = WorldUtils.getBlock(pos);
                    if(!WorldUtils.empty.contains(block)) checked++;

                    final BlockPos pos0 = dHole.pos1.add(off[0], off[1] + 1, off[2]);
                    if(pos0.equals(dHole.pos.add(0, off[1] + 1, 0))) continue;

                    final Block block0 = WorldUtils.getBlock(pos0);
                    if(!WorldUtils.empty.contains(block0)) checked++;
                }

                if(!WorldUtils.empty.contains(WorldUtils.getBlock(dHole.pos.add(0, 2, 0)))) checked++;
                if(!WorldUtils.empty.contains(WorldUtils.getBlock(dHole.pos1.add(0, 2, 0)))) checked++;
                if(antiStep) {
                    if(!WorldUtils.empty.contains(WorldUtils.getBlock(dHole.pos.add(0, 3, 0)))) checked++;
                    if(!WorldUtils.empty.contains(WorldUtils.getBlock(dHole.pos1.add(0, 3, 0)))) checked++;
                }
                if(checked == (antiStep ? 16 : 14)) return true;
            }
            if(hole instanceof HoleUtils.SingleHole) {
                int checked = 0;
                final int[][] offsets = {
                        {1, 0, 0},
                        {0, 0, -1},
                        {0, 0, 1},
                        {-1, 0, 0},
                        {1, 1, 0},
                        {0, 1, -1},
                        {0, 1, 1},
                        {-1, 1, 0},
                        {0, 2, 0}
                };

                for(int[] off : offsets) {
                    if(!WorldUtils.empty.contains(WorldUtils.getBlock(((HoleUtils.SingleHole) hole).pos.add(off[0], off[1], off[2])))) checked++;
                }

                if(antiStep) {
                    if (!WorldUtils.empty.contains(WorldUtils.getBlock(((HoleUtils.SingleHole) hole).pos.add(0, 3, 0)))) checked++;
                }
                return checked == (antiStep ? 10 : 9);
            }
        }
        return false;
    }

    public static boolean isBurrowed(EntityLivingBase entity) {
        return WorldUtils.getBlock(getEntityPosFloored(entity)) == Blocks.OBSIDIAN
                || WorldUtils.getBlock(getEntityPosFloored(entity)) == Blocks.BEDROCK
                || WorldUtils.getBlock(getEntityPosFloored(entity)) == Blocks.ENDER_CHEST;
    }

    public static boolean isInHole(EntityLivingBase entity) {
        final ArrayList<BlockPos> blocks = getPos(0, 0, 0, entity);
        if(blocks.size() == 2) {
            BlockPos pos2 = null;
            BlockPos pos = null;
            for(BlockPos block : blocks) {
                for(EnumFacing facing : EnumFacing.values()) {
                    if(facing == EnumFacing.DOWN || facing == EnumFacing.UP) continue;

                    if(WorldUtils.getBlock(block) == Blocks.AIR) {
                        if(WorldUtils.getBlock(block.offset(facing)) == Blocks.AIR) {
                            pos = block;
                            pos2 = block.offset(facing);
                            break;
                        }
                    }
                }
                if(pos != null) break;
            }

            if(pos == null) return false;

            BlockPos[] offsets = {
                    pos.north(), pos.south(), pos.east(), pos.west()
            };
            BlockPos[] offsets2 = {
                    pos2.north(), pos2.south(), pos2.east(), pos2.west()
            };

            int checked = 0;

            for(BlockPos off : offsets) {
                if(off == pos2) continue;
                Block block = WorldUtils.getBlock(off);
                if(block == Blocks.OBSIDIAN || (block == Blocks.BEDROCK)) {
                    checked++;
                }
            }
            for(BlockPos off : offsets2) {
                if(off == pos) continue;
                Block block = WorldUtils.getBlock(off);
                if(block == Blocks.OBSIDIAN || (block == Blocks.BEDROCK)) {
                    checked++;
                }
            }
            return checked >= 6;
        } else if(blocks.size() == 1) {
            BlockPos pos = getEntityPosFloored(entity);
            BlockPos[] offsets = {
                    pos.north(), pos.south(), pos.east(), pos.west()
            };

            int checked = 0;
            for(BlockPos pos0 : offsets) {
                Block block = WorldUtils.getBlock(pos0);
                if(block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) {
                    checked++;
                }
            }
            return checked >= 4;
        }
        return false;
    }

    /**
     * checks to see if the durability is <b>under</b> the provided durability
     * @param target
     * @param durability
     * @return
     */
    public static boolean getArmor(EntityPlayer target, double durability) {
        for (ItemStack stack : target.getArmorInventoryList()) {
            if (stack == null || stack.getItem() == Items.AIR)
                return true;

            if (durability >= ((float) (stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage()) * 100.0f)
                return true;
        }

        return false;
    }

    public static int getTotalArmor(EntityPlayer target) {
        int damage = 0;
        for (ItemStack stack : target.getArmorInventoryList()) {
            if (stack == null || stack.getItem() == Items.AIR)
                continue;

            damage += stack.getItemDamage();
        }

        return damage;
    }

    private static boolean isValid(Entity entity, boolean players, boolean neutral, boolean friends, boolean hostile, boolean passive, double range) {
        if(entity.isDead) return false;
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


    public static boolean isIntercepted(BlockPos pos) {
        for (Entity entity : mc.world.loadedEntityList) {
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox()))
                return true;
        }

        return false;
    }

    public static void setTimer(float speed) {
        mc.timer.tickLength = 50.0f / speed;
    }

    public static void resetTimer() {
        mc.timer.tickLength = 50;
    }

    public static boolean isInLiquid() {
        if (mc.player != null) {
            if (mc.player.fallDistance >= 3.0f) {
                return false;
            }
            boolean inLiquid = false;
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (!(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }
                        inLiquid = true;
                    }
                }
            }
            return inLiquid;
        }
        return false;
    }
}
