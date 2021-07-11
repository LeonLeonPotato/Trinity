package me.leon.trinity.hacks.movement;

import me.leon.trinity.events.main.MoveEvent;
import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
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
	public static ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal");
	public static BooleanSetting useTimer = new BooleanSetting("UseTimer", false, true);
	public static SliderSetting timerTime = new SliderSetting("Speed", useTimer, 0, 1.15, 2, false);
	public static SliderSetting speed = new SliderSetting("Speed", 0, 1, 1.5, false);
	public static SliderSetting slowSpeed = new SliderSetting("SlowSpeed", 50, 159, 300, true);
	public static SliderSetting groundSpeed = new SliderSetting("GroundFactor", 0, 1.9, 3, false);
	public static SliderSetting jumpheight = new SliderSetting("JumpHeight", 0, 0.4, 0.5, false);
	public static BooleanSetting vStep = new BooleanSetting("VStep", true, true);
	public static SliderSetting height = new SliderSetting("Height", vStep, 0, 2, 3, false);
	private static boolean slowDown = false;
	private static double strafeSpeed;

	public Speed() {
		super("Speed", "Make yourself faster", Category.MOVEMENT);
	}

	@Override
	public String getHudInfo() {
		return mode.getValue();
	}

	@EventHandler
	private final Listener<EventModeChange> changeListener = new Listener<>(event -> {
		if (event.getSet() == mode) {
			this.toggle();
			this.toggle();
		}
	});

	@EventHandler
	private final Listener<MoveEvent> mainListener = new Listener<>(event -> {
		if (nullCheck()) {
			return;
		}
		if(vStep.getValue() && !ModuleManager.getMod(Step.class).isEnabled()) {
			mc.player.stepHeight = (float) height.getValue();
		}

		if (mode.getValue().equalsIgnoreCase("Normal")) {
			double speedY = jumpheight.getValue();

			if (mc.player.onGround && MotionUtils.isMoving()) {
				if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
					speedY += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
				}

				event.y = (mc.player.motionY = speedY);
				final ArrayList<Block> blocks = EntityUtils.isColliding(0, -0.5, 0, mc.player);
				strafeSpeed = getBaseMoveSpeed() * ((blocks.contains(Blocks.WATER) || blocks.contains(Blocks.FLOWING_LAVA) || blocks.contains(Blocks.FLOWING_WATER) || blocks.contains(Blocks.LAVA)) && !EntityUtils.isInLiquid() ? 0.9 : groundSpeed.getValue());
				slowDown = true;
			} else {
				EntityUtils.resetTimer();
				if (slowDown || mc.player.collidedHorizontally) {
					final ArrayList<Block> blocks = EntityUtils.isColliding(0, -0.5, 0, mc.player);
					strafeSpeed -= ((blocks.contains(Blocks.WATER) || blocks.contains(Blocks.FLOWING_LAVA) || blocks.contains(Blocks.FLOWING_WATER) || blocks.contains(Blocks.LAVA) && !EntityUtils.isInLiquid()) ? 0.4 : 0.7 * (strafeSpeed = getBaseMoveSpeed()));
					slowDown = false;
				} else {
					strafeSpeed -= strafeSpeed / slowSpeed.getValue();
				}
			}
			strafeSpeed = Math.max(strafeSpeed, getBaseMoveSpeed());
		}

		if (useTimer.getValue()) {
			EntityUtils.setTimer((float) timerTime.getValue());
		}

		MotionUtils.doStrafe(event, (float) (strafeSpeed * speed.getValue()));
		event.cancel();
	});

	@Override
	public void onEnable() {
		if(nullCheck()) return;
		if(vStep.getValue())
			mc.player.stepHeight = (float) height.getValue();
	}

	@Override
	public void onDisable() {
		if(nullCheck()) return;
		EntityUtils.setTimer(1);
		if(!ModuleManager.getMod(Step.class).isEnabled())
			mc.player.stepHeight = 0.5f;
	}
}
