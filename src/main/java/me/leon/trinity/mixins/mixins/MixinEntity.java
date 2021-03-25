package me.leon.trinity.mixins.mixins;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.EventGetBlockReachDistance;
import me.leon.trinity.hacks.movement.Velocity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(Entity.class)
public abstract class MixinEntity implements IMixin {
    @Redirect(method = "applyEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void velocity(Entity entity, double x, double y, double z) {
        if (!ModuleManager.getMod(Velocity.class).isEnabled()) {
            entity.motionX += x;
            entity.motionY += y;
            entity.motionZ += z;
            entity.isAirBorne = true;
        }
    }

    @Overwrite
    public RayTraceResult rayTrace(double blockReachDistance, float partialTicks) {
        EventGetBlockReachDistance event = new EventGetBlockReachDistance(EventStage.PRE, blockReachDistance);
        Vec3d vec3d = this.getPositionEyes(partialTicks);
        Vec3d vec3d1 = this.getLook(partialTicks);
        Vec3d vec3d2;
        if(event.isCancelled()) {
            vec3d2 = vec3d.add(vec3d1.x * event.reach, vec3d1.y * event.reach, vec3d1.z * event.reach);
        } else {
            vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        }
        return mc.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }

    @Shadow
    public abstract Vec3d getPositionEyes(float partialTicks);

    @Shadow
    public abstract Vec3d getLook(float partialTicks);

    @Shadow
    public abstract boolean equals(Object p_equals_1_);

    @Shadow
    public double posX;

    @Shadow
    public double posY;

    @Shadow
    public double posZ;

    @Shadow
    public double prevPosX;

    @Shadow
    public double prevPosY;

    @Shadow
    public double prevPosZ;

    @Shadow
    public double lastTickPosX;

    @Shadow
    public double lastTickPosY;

    @Shadow
    public double lastTickPosZ;

    @Shadow
    public float prevRotationYaw;

    @Shadow
    public float prevRotationPitch;

    @Shadow
    public float rotationPitch;

    @Shadow
    public float rotationYaw;

    @Shadow
    public boolean onGround;

    @Shadow
    public double motionX;

    @Shadow
    public double motionY;

    @Shadow
    public double motionZ;

    @Shadow
    public abstract boolean isSprinting();

    @Shadow
    public abstract boolean isRiding();

    @Shadow
    public void move(MoverType type, double x, double y, double z) {

    }
}

