package me.leon.trinity.utils.math;

import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.Util;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class MathUtils implements Util {
    public static double clamp(double min, double max, double val) {
        if(val > max || min > max) {
            return max;
        }
        return Math.max(val, min);
    }

    public static double clamp(double min, double max, double val ,double height) {
        if(val <= min || max <= min) {
            return min;
        }
        if(val + height >= max || min >= max) {
            return max - height;
        }
        return val;
    }

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static Vec3d extrapolatePlayerPosition(Entity player, int ticks) {
        Vec3d lastPos = new Vec3d(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ);
        Vec3d currentPos = new Vec3d(player.posX, player.posY, player.posZ);
        double distance = multiply(player.motionX) + multiply(player.motionY) + multiply(player.motionZ);
        Vec3d tempVec = calculateLine(lastPos, currentPos, distance * ticks);
        return new Vec3d(tempVec.x, player.posY, tempVec.z);
    }

    public static double multiply(double one) {
        return one * one;
    }

    public static Vec3d calculateLine(Vec3d x1, Vec3d x2, double distance) {
        double length = Math.sqrt(multiply(x2.x - x1.x) + multiply(x2.y - x1.y) + multiply(x2.z - x1.z));
        if(length != 0.0) {
            double unitSlopeX = (x2.x - x1.x) / length;
            double unitSlopeY = (x2.y - x1.y) / length;
            double unitSlopeZ = (x2.z - x1.z) / length;
            double x = x1.x + unitSlopeX * distance;
            double y = x1.y + unitSlopeY * distance;
            double z = x1.z + unitSlopeZ * distance;
            return new Vec3d(x, y, z);
        } else {
            return x2;
        }
    }
}
