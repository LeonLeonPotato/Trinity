package me.leon.trinity.hacks.movement;

import me.leon.trinity.events.main.MoveEvent;
import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.exploits.PacketFly;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.setting.settings.sub.SubSlider;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.MotionUtils;
import me.leon.trinity.utils.math.MathUtils;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;

import java.util.ArrayList;

import static me.leon.trinity.utils.entity.MotionUtils.getBaseMoveSpeed;

public class Speed extends Module {
    public static Mode mode = new Mode("Mode", "Normal", "Normal", "Weird", "Weird2", "Phobos");
    public static SettingParent useTimer = new SettingParent("UseTimer", false, true);
    public static SubSlider timerTime = new SubSlider("Speed", useTimer, 0, 1.15, 2, false);
    public static Slider speed = new Slider("Speed", 0, 1, 1.5, false);
    public static Slider jumpheight = new Slider("JumpHeight", 0, 0.4, 0.5, false);
    public static SettingParent limit = new SettingParent("Limit", true, true);
    public static SubSlider speedLimit = new SubSlider("SpeedLimit", limit, 0, 27, 40, false);

    public Speed() {
        super("Speed", "Make yourself faster", Category.MOVEMENT);
    }

    private static int mode0 = 0;
    private static int jumps = 0;
    private static boolean slowDown = false;
    private static double strafeSpeed;

    @Override
    public void onEnable() {
        mode0 = 0;
        jumps = 0;
    }

    @Override
    public void onDisable() {
        jumps = 0;
        mode0 = 0;
        EntityUtils.setTimer(1);
    }

    @EventHandler
    private final Listener<EventModeChange> changeListener = new Listener<>(event -> {
        if(event.getSet() == mode) {
            this.toggle();
            this.toggle();
        }
    });

    @EventHandler
    private final Listener<MoveEvent> mainListener = new Listener<>(event -> {
        if(nullCheck()) {
            mode0 = 0;
            return;
        }
        double lastDist = Math.sqrt(Math.pow(mc.player.posX - mc.player.prevPosX, 2) + Math.pow(mc.player.posZ - mc.player.prevPosZ, 2));

        if(mode.getValue().equalsIgnoreCase("Weird")) {
            if(mode0 == 0) {
                if(!MotionUtils.isMoving()) return;
                if(mc.player.onGround)
                    event.y = mc.player.motionY = jumpheight.getValue();
                strafeSpeed = (getBaseMoveSpeed() * 1.38f);
                mode0++;
            } else
            if(mode0 == 1) {
                strafeSpeed = lastDist - (0.66 * (lastDist - Speed.speed.getValue()));
                if(mc.player.motionY <= 0) {
                    mode0++;
                }
            } else
            if(mode0 == 2) {
                strafeSpeed = lastDist - (lastDist / 159.0);
                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically)
                {
                    mode0 = 0;
                    strafeSpeed = getBaseMoveSpeed();
                }
            }

            strafeSpeed = Math.min(Math.max(strafeSpeed, Speed.speed.getValue()), 0.551);
        } else if (mode.getValue().equalsIgnoreCase("Normal")) {
            double speedY = jumpheight.getValue();

            if (mc.player.onGround && MotionUtils.isMoving()) {
                if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    speedY += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
                }

                event.y = (mc.player.motionY = speedY);
                final ArrayList<Block> blocks = EntityUtils.isColliding(0, -0.5, 0, mc.player);
                strafeSpeed = getBaseMoveSpeed() * ((blocks.contains(Blocks.WATER) || blocks.contains(Blocks.FLOWING_LAVA) || blocks.contains(Blocks.FLOWING_WATER) || blocks.contains(Blocks.LAVA)) && !EntityUtils.isInLiquid() ? 0.9 : 1.901);
                slowDown = true;
            }
            else {
                EntityUtils.resetTimer();
                if (slowDown || mc.player.collidedHorizontally) {
                    final ArrayList<Block> blocks = EntityUtils.isColliding(0, -0.5, 0, mc.player);
                    strafeSpeed -= ((blocks.contains(Blocks.WATER) || blocks.contains(Blocks.FLOWING_LAVA) || blocks.contains(Blocks.FLOWING_WATER) || blocks.contains(Blocks.LAVA) && !EntityUtils.isInLiquid()) ? 0.4 : 0.7 * (strafeSpeed = getBaseMoveSpeed()));
                    slowDown = false;
                }
                else {
                    strafeSpeed -= strafeSpeed / 159.0;
                }
            }
            strafeSpeed = Math.max(strafeSpeed, getBaseMoveSpeed());
        } else if(mode.getValue().equalsIgnoreCase("Weird2")) {
            if(mode0 == 0) {
                if(!MotionUtils.isMoving()) {
                    jumps = 0;
                    mode0 = 0;
                    return;
                }
                if(mc.player.onGround) {
                    mc.player.motionY = event.y = jumpheight.getValue();
                    strafeSpeed = getBaseMoveSpeed();
                    jumps++;
                }
                jumps = (int) MathUtils.clamp(0, 5, jumps);
                mode0++;
            } else
            if(mode0 == 1) {
                strafeSpeed = getBaseMoveSpeed() * 1.91f * ((jumps / 10f) + 1);
                mode0++;
            } else
            if(mode0 == 2) {
                strafeSpeed -= strafeSpeed / 159;
                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically)
                {
                    mode0 = 0;
                    strafeSpeed = getBaseMoveSpeed() * 1.1;
                }
            }
        } else {
            if (!limit.getValue() && mc.player.onGround) {
                mode0 = 2;
            }
            switch (mode0) {
                case 0: {
                    mode0++;
                    lastDist = 0.0;
                    break;
                }
                case 2: {
                    double motionY = jumpheight.getValue();
                    if (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f || !mc.player.onGround) break;
                    if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                        motionY += ((float)(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f);
                    }
                    mc.player.motionY = motionY;
                    event.y = (mc.player.motionY);
                    strafeSpeed *= 2.149;
                    break;
                }
                case 3: {
                    strafeSpeed = strafeSpeed - 0.76 * (lastDist - getBaseMoveSpeed());
                    break;
                }
                default: {
                    if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically && mode0 > 0) {
                        mode0 = MotionUtils.getSpeed() >= speedLimit.getValue() ? 0 : (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f ? 1 : 0);
                    }
                    strafeSpeed = lastDist - lastDist / 159.0;
                }
            }
            strafeSpeed = Math.max(strafeSpeed, getBaseMoveSpeed());
            if(limit.getValue()) {
                strafeSpeed = Math.min(strafeSpeed, speedLimit.getValue());
            }
            mode0++;
        }

        if(useTimer.getValue()) {
            EntityUtils.setTimer((float) timerTime.getValue());
        }

        MotionUtils.doStrafe(event, (float) (strafeSpeed * speed.getValue()));
        event.cancel();
    });
}
