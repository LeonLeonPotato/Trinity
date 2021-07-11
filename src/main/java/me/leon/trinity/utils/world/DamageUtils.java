package me.leon.trinity.utils.world;

import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.math.MathUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

/**
 * @author phobos
 */
public class DamageUtils implements Util {
    public static boolean isArmorLow(EntityPlayer player, int durability) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if (piece == null) {
                return true;
            }
            if (getItemDamage(piece) >= durability) continue;
            return true;
        }
        return false;
    }

    public static boolean isNaked(EntityPlayer player) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if (piece == null || piece.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public static int getItemDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static float getDamageInPercent(ItemStack stack) {
        return (float)getItemDamage(stack) / (float)stack.getMaxDamage() * 100.0f;
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int)getDamageInPercent(stack);
    }

    public static boolean hasDurability(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }

    public static boolean canBreakWeakness(EntityPlayer player) {
        int strengthAmp = 0;
        PotionEffect effect = mc.player.getActivePotionEffect(MobEffects.STRENGTH);
        if (effect != null) {
            strengthAmp = effect.getAmplifier();
        }
        return !mc.player.isPotionActive(MobEffects.WEAKNESS) || strengthAmp >= 1 || mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe || mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe || mc.player.getHeldItemMainhand().getItem() instanceof ItemSpade;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity, boolean predict, int predictTicks) {
        AxisAlignedBB bb;
        Vec3d entityPos;
        if(predict) {
            entityPos = MathUtils.extrapolatePlayerPosition(entity, predictTicks);
            bb = entity.boundingBox.offset(-entity.posX, -entity.posY, -entity.posZ).offset(entityPos);
        } else {
            bb = entity.boundingBox;
            entityPos = entity.getPositionVector();
        }
        final float doubleExplosionSize = 12.0f;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        final double distancedsize = entityPos.distanceTo(vec3d) / (double) doubleExplosionSize;

        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, bb);
        }
        catch (Exception exception) {
            // empty catch block
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (float) ((v * v + v) / 2.0 * 7.0 * (double) doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase)entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float) finald;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        return calculateDamage(posX, posY, posZ, entity, false, 0);
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer)entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            }
            catch (Exception exception) {
                // empty catch block
            }
            float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float)entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(Entity crystal, Entity entity, boolean predict, int ticks) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity, predict, ticks);
    }

    public static float calculateDamage(BlockPos pos, Entity entity, boolean predict, int ticks) {
        return calculateDamage((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, entity, predict, ticks);
    }

    public static float calculateDamage(Vec3d pos, Entity entity, boolean predict, int ticks) {
        return calculateDamage(pos.x, pos.y, pos.z, entity, predict, ticks);
    }

    public static float calculateDamage(Entity crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    public static float calculateDamage(BlockPos pos, Entity entity) {
        return calculateDamage((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, entity);
    }

    public static float calculateDamage(Vec3d pos, Entity entity) {
        return calculateDamage(pos.x, pos.y, pos.z, entity);
    }

    public static boolean canTakeDamage(boolean suicide) {
        return !mc.player.capabilities.isCreativeMode && !suicide;
    }
}
