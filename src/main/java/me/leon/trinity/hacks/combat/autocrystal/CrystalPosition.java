package me.leon.trinity.hacks.combat.autocrystal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CrystalPosition {
    private float targetDamage;
    private float selfDamage;
    private BlockPos base;

    public CrystalPosition() { /* empty */ }

    public CrystalPosition(float targetDamage, float selfDamage, BlockPos base) {
        this.targetDamage = targetDamage;
        this.selfDamage = selfDamage;
        this.base = base;
    }

    public float getTargetDamage() {
        return targetDamage;
    }

    public void setTargetDamage(float targetDamage) {
        this.targetDamage = targetDamage;
    }

    public float getSelfDamage() {
        return selfDamage;
    }

    public void setSelfDamage(float selfDamage) {
        this.selfDamage = selfDamage;
    }

    public BlockPos getBase() {
        return base;
    }

    public void setBase(BlockPos base) {
        this.base = base;
    }

    public Vec3d getCenterVec() {
        return new Vec3d(base.x + 0.5D, base.y + 0.5D, base.z + 0.5D);
    }

    public Vec3d getCrystalVec() {
        return new Vec3d(base.x + 0.5D, base.y + 1.0D, base.z + 0.5D);
    }
}
