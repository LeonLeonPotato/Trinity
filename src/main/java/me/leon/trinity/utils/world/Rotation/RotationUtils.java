package me.leon.trinity.utils.world.Rotation;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.LocationSpoofEvent;
import me.leon.trinity.events.main.SpoofEvent;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.SpoofingManager;
import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.world.Priority;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class RotationUtils implements Util {
	private static final Random random = new Random();

	// override vanilla packet sending here, we replace them with our own custom values
	public static void updateRotationPackets(SpoofEvent event) {
		if (mc.player.isSprinting() != mc.player.serverSprintState) {
			if (mc.player.isSprinting())
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
			else
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));

			mc.player.serverSprintState = mc.player.isSprinting();
		}

		if (mc.player.isSneaking() != mc.player.serverSneakState) {
			if (mc.player.isSneaking())
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			else
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

			mc.player.serverSneakState = mc.player.isSneaking();
		}

		if(!event.hasLocation) {
			event.posX = mc.player.posX;
			event.posY = mc.player.posY;
			event.posZ = mc.player.posZ;
			event.onGround = mc.player.onGround;
		}
		if(!event.hasRotation) {
			event.pitch = mc.player.rotationPitch;
			event.yaw = mc.player.rotationYaw;
		}

		double updatedPosX = event.posX - mc.player.lastReportedPosX;
		double updatedPosY = event.posY - mc.player.lastReportedPosY;
		double updatedPosZ = event.posZ - mc.player.lastReportedPosZ;

		double updatedRotationYaw = event.yaw - mc.player.lastReportedYaw;
		double updatedRotationPitch = event.pitch - mc.player.lastReportedPitch;

		mc.player.positionUpdateTicks++;

		boolean positionUpdate = updatedPosX * updatedPosX + updatedPosY * updatedPosY + updatedPosZ * updatedPosZ > 9.0E-4D || mc.player.positionUpdateTicks >= 20 || event.hasLocation;
		boolean rotationUpdate = updatedRotationYaw != 0.0D || updatedRotationPitch != 0.0D || event.hasRotation;

		if (mc.player.isRiding()) {
			mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.motionX, -999.0D, mc.player.motionZ, event.yaw, event.pitch, event.onGround));
			positionUpdate = false;
		} else if (positionUpdate && rotationUpdate)
			mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(event.posX, event.posY, mc.player.posZ, event.yaw, event.pitch, event.onGround));
		else if (positionUpdate)
			mc.player.connection.sendPacket(new CPacketPlayer.Position(event.posX, event.posY, event.posZ, event.onGround));
		else if (rotationUpdate)
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation(event.yaw, event.pitch, event.onGround));
		else if (mc.player.prevOnGround != event.onGround)
			mc.player.connection.sendPacket(new CPacketPlayer(event.onGround));

		if(event.hasLocation)
			Trinity.dispatcher.post(new LocationSpoofEvent(EventStage.POST, event));

		if (positionUpdate) {
			mc.player.lastReportedPosX = event.posX;
			mc.player.lastReportedPosY = event.posY;
			mc.player.lastReportedPosZ = event.posZ;
			mc.player.positionUpdateTicks = 0;
		}

		if (rotationUpdate) {
			mc.player.lastReportedYaw = event.yaw;
			mc.player.lastReportedPitch = event.pitch;
		}

		mc.player.prevOnGround = event.onGround;
		mc.player.autoJumpEnabled = mc.player.mc.gameSettings.autoJump;
	}

	/**
	 * rot[0] = yaw, rot[1] = pitch
	 */
	public static float[] getNeededRotations(Vec3d vec) {
		Vec3d eyesPos = mc.player.getPositionVector().add(0, mc.player.eyeHeight, 0);

		double diffX = vec.x - eyesPos.x;
		double diffY = vec.y - eyesPos.y;
		double diffZ = vec.z - eyesPos.z;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

		return new float[]{
				mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
				mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
		};
	}

	public static void rotateTowards(Vec3d vec) {
		float[] rots = getNeededRotations(vec);
		Rotation rot = new Rotation(rots[1], rots[0], true);
		SpoofingManager.rotationQueue.add(rot);
	}

	public static void rotateTowards(Vec3d vec, boolean packet) {
		float[] rots = getNeededRotations(vec);
		Rotation rot = new Rotation(rots[1], rots[0], packet, Priority.Highest);
		SpoofingManager.rotationQueue.add(rot);
	}

	public static void rotateTowards(Vec3d vec, boolean packet, Priority prio) {
		float[] rots = getNeededRotations(vec);
		Rotation rot = new Rotation(rots[1], rots[0], packet, prio);
		SpoofingManager.rotationQueue.add(rot);
	}

	public static void rotateRandom(boolean packet, Priority prio) {
		Rotation rot = new Rotation((random.nextFloat() * 360) - 180, (random.nextFloat() * 360) - 180, packet, prio);
		SpoofingManager.rotationQueue.add(rot);
	}

	public static void rotateRandom(boolean packet) {
		Rotation rot = new Rotation((random.nextFloat() * 360) - 180, (random.nextFloat() * 360) - 180, packet, Priority.Highest);
		SpoofingManager.rotationQueue.add(rot);
	}

	public static void rotateRandom() {
		Rotation rot = new Rotation((random.nextFloat() * 360) - 180, (random.nextFloat() * 360) - 180, true, Priority.Highest);
		SpoofingManager.rotationQueue.add(rot);
	}
}
