package me.leon.trinity.utils.world.Rotation;

import me.leon.trinity.events.main.RotationEvent;
import me.leon.trinity.managers.RotationManager;
import me.leon.trinity.utils.Util;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class RotationUtils implements Util {
    private static final Random random = new Random();

    // override vanilla packet sending here, we replace them with our own custom values
    public static void updateRotationPackets(RotationEvent event) {
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

        double updatedPosX = mc.player.posX - mc.player.lastReportedPosX;
        double updatedPosY = mc.player.getEntityBoundingBox().minY - mc.player.lastReportedPosY;
        double updatedPosZ = mc.player.posZ - mc.player.lastReportedPosZ;

        double updatedRotationYaw = event.yaw - mc.player.lastReportedYaw;
        double updatedRotationPitch = event.pitch - mc.player.lastReportedPitch;

        mc.player.positionUpdateTicks++;

        boolean positionUpdate = updatedPosX * updatedPosX + updatedPosY * updatedPosY + updatedPosZ * updatedPosZ > 9.0E-4D || mc.player.positionUpdateTicks >= 20;
        boolean rotationUpdate = updatedRotationYaw != 0.0D || updatedRotationPitch != 0.0D;

        if (mc.player.isRiding()) {
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.motionX, -999.0D, mc.player.motionZ, event.yaw, event.pitch, mc.player.onGround));
            positionUpdate = false;
        }

        else if (positionUpdate && rotationUpdate)
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ, event.yaw, event.pitch, mc.player.onGround));
        else if (positionUpdate)
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ, mc.player.onGround));
        else if (rotationUpdate)
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(event.yaw, event.pitch, mc.player.onGround));
        else if (mc.player.prevOnGround != mc.player.onGround)
            mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));

        if (positionUpdate) {
            mc.player.lastReportedPosX = mc.player.posX;
            mc.player.lastReportedPosY = mc.player.getEntityBoundingBox().minY;
            mc.player.lastReportedPosZ = mc.player.posZ;
            mc.player.positionUpdateTicks = 0;
        }

        if (rotationUpdate) {
            mc.player.lastReportedYaw = event.yaw;
            mc.player.lastReportedPitch = event.pitch;
        }

        mc.player.prevOnGround = mc.player.onGround;
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

        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[] {
                mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
        };
    }

    public static void rotateTowards(Vec3d vec) {
        float[] rots = getNeededRotations(vec);
        Rotation rot = new Rotation(rots[1], rots[0], true);
        RotationManager.rotationQueue.add(rot);
    }

    public static void rotateTowards(Vec3d vec, boolean packet) {
        float[] rots = getNeededRotations(vec);
        Rotation rot = new Rotation(rots[1], rots[0], packet, RotationPriority.Highest);
        RotationManager.rotationQueue.add(rot);
    }

    public static void rotateTowards(Vec3d vec, boolean packet, RotationPriority prio) {
        float[] rots = getNeededRotations(vec);
        Rotation rot = new Rotation(rots[1], rots[0], packet, prio);
        RotationManager.rotationQueue.add(rot);
    }

    public static void rotateRandom(boolean packet, RotationPriority prio) {
        Rotation rot = new Rotation((random.nextFloat() * 360) - 180, (random.nextFloat() * 360) - 180, packet, prio);
        RotationManager.rotationQueue.add(rot);
    }

    public static void rotateRandom(boolean packet) {
        Rotation rot = new Rotation((random.nextFloat() * 360) - 180, (random.nextFloat() * 360) - 180, packet, RotationPriority.Highest);
        RotationManager.rotationQueue.add(rot);
    }

    public static void rotateRandom() {
        Rotation rot = new Rotation((random.nextFloat() * 360) - 180, (random.nextFloat() * 360) - 180, true, RotationPriority.Highest);
        RotationManager.rotationQueue.add(rot);
    }
}
