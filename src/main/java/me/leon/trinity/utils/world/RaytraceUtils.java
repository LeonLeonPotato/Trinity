package me.leon.trinity.utils.world;

import me.leon.trinity.utils.Util;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class RaytraceUtils implements Util {
    public static Vec3d rayTraceLeon(Entity entity) {
        Vec3d toReturn = null;
        AxisAlignedBB bb = entity.boundingBox;
        Vec3d og = mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0);
        Vec3d[] vecs = { // all vecs of the entity
                entity.getPositionVector().add(0, entity.height / 2, 0),
                new Vec3d(bb.minX,bb.minY,bb.minZ),
                new Vec3d(bb.minX,bb.minY,bb.maxZ),
                new Vec3d(bb.maxX,bb.minY,bb.maxZ),
                new Vec3d(bb.maxX,bb.minY,bb.minZ),
                new Vec3d(bb.minX,bb.minY,bb.minZ),
                new Vec3d(bb.minX,bb.maxY,bb.minZ),
                new Vec3d(bb.minX,bb.maxY,bb.maxZ),
                new Vec3d(bb.minX,bb.minY,bb.maxZ),
                new Vec3d(bb.maxX,bb.minY,bb.maxZ),
                new Vec3d(bb.maxX,bb.maxY,bb.maxZ),
                new Vec3d(bb.minX,bb.maxY,bb.maxZ),
                new Vec3d(bb.maxX,bb.maxY,bb.maxZ),
                new Vec3d(bb.maxX,bb.maxY,bb.minZ),
                new Vec3d(bb.maxX,bb.minY,bb.minZ),
                new Vec3d(bb.maxX,bb.maxY,bb.minZ),
                new Vec3d(bb.minX,bb.maxY,bb.minZ)
        };

        for(Vec3d vec : vecs) {
            if(mc.world.rayTraceBlocks(og, vec, false, true, false) == null) {
                toReturn = vec;
                break;
            }
        }
        return toReturn;
    }

    public static boolean rayTraceSimple(Entity entity) {
        return mc.world.rayTraceBlocks(new Vec3d(entity.posX, entity.posY + (entity.height / 2), entity.posZ), mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0)) == null;
    }

    public static boolean rayTraceSimple(Entity entity, double off) {
        return mc.world.rayTraceBlocks(new Vec3d(entity.posX, entity.posY + off, entity.posZ), mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0)) == null;
    }
}
