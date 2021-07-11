package me.leon.trinity.hacks.combat.autocrystal;

import me.leon.trinity.utils.Util;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;

import static me.leon.trinity.hacks.combat.autocrystal.Utility.*;
import static me.leon.trinity.hacks.combat.AutoCrystal.*;

/**
 * gives me aids
 */
public class Crystal implements Util {
    private float selfDamage;
    private float targetDamage;
    private EntityEnderCrystal crystal;
    private BlockPos base;

    public Crystal() {

    }

    public Crystal(final EntityEnderCrystal crystal) {
        this (calcDamage(crystal, mc.player), calcDamage(crystal, target), crystal);
    }

    public Crystal(final float selfDamage, final float targetDamage, final EntityEnderCrystal crystal) {
        this.crystal = crystal;
        this.base = crystal.getPosition().down();
        this.selfDamage = selfDamage;
        this.targetDamage = targetDamage;
    }

    public float getSelfDamage() {
        return selfDamage;
    }

    public void setSelfDamage(float selfDamage) {
        this.selfDamage = selfDamage;
    }

    public float getTargetDamage() {
        return targetDamage;
    }

    public void setTargetDamage(float targetDamage) {
        this.targetDamage = targetDamage;
    }

    public EntityEnderCrystal getCrystal() {
        return crystal;
    }

    public void setCrystal(EntityEnderCrystal crystal) {
        this.crystal = crystal;
    }

    public BlockPos getBase() {
        return base;
    }

    public void setBase(BlockPos base) {
        this.base = base;
    }
}
