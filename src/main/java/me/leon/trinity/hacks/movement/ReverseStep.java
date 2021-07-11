package me.leon.trinity.hacks.movement;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.SliderSetting;

/**
 * @author GameSense
 */
public class ReverseStep extends Module {
	public static SliderSetting height = new SliderSetting("Height", 0.5, 2.5, 10, true);

	public ReverseStep() {
		super("ReverseStep", "moves down", Category.MOVEMENT);
	}

	public void onUpdate() {
		if (mc.world == null || mc.player == null || mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder()
				|| mc.gameSettings.keyBindJump.isKeyDown()) {
			return;
		}
		if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder()) {
			for (double y = 0.0; y < height.getValue() + 0.5; y += 0.01) {
				if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
					mc.player.motionY = -10.0;
					break;
				}
			}
		}
	}
}
