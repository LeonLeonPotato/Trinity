package me.leon.trinity.mixins.mixins;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.EventGetBlockReachDistance;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Entity.class)
public class MixinEntity implements IMixin {
    @Overwrite
    public RayTraceResult rayTrace(double blockReachDistance, float partialTicks) {
        EventGetBlockReachDistance event = new EventGetBlockReachDistance(EventStage.PRE, blockReachDistance);
        Trinity.dispatcher.post(event);

        Vec3d vec3d = ((Entity) (Object) this).getPositionEyes(partialTicks);
        Vec3d vec3d1 = ((Entity) (Object) this).getLook(partialTicks);
        Vec3d vec3d2;

        if(!event.isCancelled()) {
            vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        } else {
            vec3d2 = vec3d.add(vec3d1.x * event.reach, vec3d1.y * event.reach, vec3d1.z * event.reach);
        }
        return mc.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }
}
