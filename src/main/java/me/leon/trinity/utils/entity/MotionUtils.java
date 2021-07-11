package me.leon.trinity.utils.entity;

import me.leon.trinity.events.main.MoveEvent;
import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.math.MathUtils;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.MathHelper;

public class MotionUtils implements Util {
	public static void doStrafe(final float speed) {
		final double[] motion = getMotion(speed);
		mc.player.motionX = motion[0];
		mc.player.motionZ = motion[1];
	}

	public static MoveEvent doStrafe(MoveEvent event, float speed) {
		final double[] motion = getMotion(speed);
		event.x = motion[0];
		event.z = motion[1];
		return event;
	}

	public static double getBaseMoveSpeed() {
		double baseSpeed = 0.2873;
		if (mc.player != null && mc.player.isPotionActive(Potion.getPotionById(1))) {
			final int amplifier = mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
			baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
		}
		return baseSpeed;
	}

	public static boolean isMoving() {
		return !(mc.player.movementInput.moveForward == 0 && mc.player.movementInput.moveStrafe == 0);
	}

	public static double getSpeed() {
		double deltaX = mc.player.posX - mc.player.prevPosX;
		double deltaZ = mc.player.posZ - mc.player.prevPosZ;

		return MathUtils.roundAvoid((MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) / 1000.0f) / (0.05f / 3600.0f), 1);
	}

	/**
	 * [0] = x; [1] = z;
	 *
	 * @param speed the speed you want
	 * @return strafeSpeed - the speed of the strafe
	 */
	public static double[] getMotion(float speed) {
		final float moveForward = mc.player.movementInput.moveForward;
		final float moveStrafe = mc.player.movementInput.moveStrafe;

		return getMotion(speed, moveForward, moveStrafe, mc.player.rotationYaw);
	}

	public static double[] getMotion(float speed, float mvF, float mvS, float yaw) {
		double x = 0;
		double z = 0;

		if (mvF == 0 && mvS == 0) {
			return new double[]{x, z};
		}

		yaw += 90;
		if (mvF > 0) {
			yaw += mvS * -45;
		} else if (mvF < 0) {
			yaw += mvS * 45;
		} else if (mvF == 0) {
			yaw += mvS * -90;
		}

		if (mvF < 0) {
			yaw -= 180;
		}

		x = (speed * Math.cos(Math.toRadians(yaw)));
		z = (speed * Math.sin(Math.toRadians(yaw)));

		return new double[]{x, z};
	}
}
