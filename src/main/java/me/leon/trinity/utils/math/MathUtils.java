package me.leon.trinity.utils.math;

import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.entity.MotionUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils implements Util {
	public static double clamp(double min, double max, double val) {
		if (val > max || min > max) {
			return max;
		}
		return Math.max(val, min);
	}

	public static double clamp(double min, double max, double val, double height) {
		if (val <= min || max <= min) {
			return min;
		}
		if (val + height >= max || min >= max) {
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
		if (length != 0.0) {
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

	public static Vec3d interpolate(float y, float p, float delta, float one, float dist) {
		final float d1 = (delta / one) * dist;
		final double[] calc = calc3d(p, y, d1);
		return new Vec3d(calc[0], calc[1], calc[2]);
	}

	public static Vec3d interpolate(float p, float delta, float one, float dist) {
		final float d1 = (delta / one) * dist;
		final double[] calc = calc2d(p, d1);
		return new Vec3d(calc[0], 0, calc[1]);
	}

	public static Vec3d interpolateStrafe(float p, float delta, float one, float dist, float mvF, float mvS) {
		final float d1 = (delta / one) * dist;
		final double[] calc = MotionUtils.getMotion(d1, mvF, mvS, p - 180);
		return new Vec3d(calc[0], 0, calc[1]);
	}

	public static Vec3d interpolateStrafe(float p, float delta, float one, float dist, float mvF, float mvS, float up) {
		final float d1 = (delta / one) * dist;
		final double[] calc = MotionUtils.getMotion(d1, mvF, mvS, p - 180);
		return new Vec3d(calc[0], up * d1, calc[1]);
	}

	public static double[] calc2d(float p, float dist) {
		final double x = dist * (Math.cos(Math.toRadians(p)));
		final double y = dist * (Math.sin(Math.toRadians(p)));

		return new double[]{x, y};
	}

	public static double[] calc3d(float p, float y, float dist) {
		double x = dist * Math.cos(Math.toRadians(p));
		double z = dist * Math.sin(Math.toRadians(p));
		double dist1 = Math.abs(0 - x);

		double x1 = dist1 * Math.cos(Math.toRadians(y));
		double z1 = dist1 * Math.sin(Math.toRadians(y));

		return new double[]{x1, z, z1};
	}

	public static double roundToPlace(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
