package me.leon.trinity.hacks.movement;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.setting.settings.sub.SubBoolean;
import me.leon.trinity.setting.settings.sub.SubSlider;
import me.leon.trinity.utils.entity.PlayerUtils;
import net.minecraft.network.play.client.CPacketPlayer;

import java.awt.*;

/**
 * @author linustouchtips & seasnail8169
 * @since 12/03/2020
 *
 * skidded ez
 */

public class Step extends Module {
    public Step() {
        super("Step", "Increases player step height", Category.MOVEMENT);
    }

    public static Mode mode = new Mode("Mode", "Teleport", "Teleport", "Spider", "Vanilla");
    public static Slider height = new Slider("Height", 0.0D, 2.0D, 2.5D, true);

    public static Mode disable = new Mode("Disable", "Never", "Never", "Completion", "Unsafe");

    public static SettingParent useTimer = new SettingParent("Use Timer", false, true);
    public static SubSlider timerTicks = new SubSlider("Timer Speed", useTimer, 0.1D, 0.5D, 2.0D, false);

    public static Boolean entityStep = new Boolean("Entity Step", false);

    public static SettingParent pause = new SettingParent("Pause", true, false);
    public static SubBoolean sneakPause = new SubBoolean("When Sneaking", pause, false);
    public static SubBoolean waterPause = new SubBoolean("When in Liquid", pause, true);

    double[] forwardStep;
    double originalHeight;

    @Override
    public void onEnable() {
        if (nullCheck())
            return;

        originalHeight = mc.player.posY;
    }

    @Override
    public void onUpdate() {
        if (nullCheck())
            return;

        if (!mc.player.collidedHorizontally)
            return;

        if (mc.player.isOnLadder() || mc.player.movementInput.jump)
            return;

        if ((mc.player.isInWater() || mc.player.isInLava()) && waterPause.getValue())
            return;

        if (useTimer.getValue())
            mc.timer.tickLength = (float) (50.0f / timerTicks.getValue());

        if (entityStep.getValue() && mc.player.isRiding())
            mc.player.ridingEntity.stepHeight = (float) height.getValue();

        if (mc.player.isSneaking() && sneakPause.getValue())
            return;

        forwardStep = PlayerUtils.motion(0.1f);

        if (getStepHeight().equals(StepHeight.Unsafe)) {
            if (disable.getValue().equalsIgnoreCase("Unsafe"))
                this.setEnabled(false);

            return;
        }

        switch (mode.getValue()) {
            case "Teleport":
                stepTeleport();
                break;
            case "Spider":
                stepSpider();
                break;
            case "Vanilla":
                stepVanilla();
                break;
        }

        if (mc.player.posY > originalHeight + getStepHeight().height && disable.getValue().equalsIgnoreCase("Completion"))
            this.setEnabled(false);
    }

    public void stepTeleport() {
        updateStepPackets(getStepHeight().stepArray);
        mc.player.setPosition(mc.player.posX, mc.player.posY + getStepHeight().height, mc.player.posZ);
    }

    public void stepSpider() {
        updateStepPackets(getStepHeight().stepArray);

        mc.player.motionY = 0.2;
        mc.player.fallDistance = 0;
    }

    public void stepVanilla() {
        mc.player.setPosition(mc.player.posX, mc.player.posY + getStepHeight().height, mc.player.posZ);
    }

    public void updateStepPackets(double[] stepArray) {
        for (double v : stepArray) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
        }
    }

    public StepHeight getStepHeight() {
        if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forwardStep[0], 1.0, forwardStep[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forwardStep[0], 0.6, forwardStep[1])).isEmpty())
            return StepHeight.One;
        else if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forwardStep[0], 1.6, forwardStep[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forwardStep[0], 1.4, forwardStep[1])).isEmpty())
            return StepHeight.OneHalf;
        else if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forwardStep[0], 2.1, forwardStep[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forwardStep[0], 1.9, forwardStep[1])).isEmpty())
            return StepHeight.Two;
        else if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forwardStep[0], 2.6, forwardStep[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forwardStep[0], 2.4, forwardStep[1])).isEmpty())
            return StepHeight.TwoHalf;
        else
            return StepHeight.Unsafe;
    }

    public enum StepHeight {
        One(1, new double[] { 0.42, 0.753 }),
        OneHalf(1.5, new double[] { 0.42, 0.75, 1.0, 1.16, 1.23, 1.2 }),
        Two(2, new double[] { 0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43 }),
        TwoHalf(2.5, new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 }),
        Unsafe(3, new double[] { 0 });

        double[] stepArray;
        double height;

        StepHeight(double height, double[] stepArray) {
            this.height = height;
            this.stepArray = stepArray;
        }
    }
}
