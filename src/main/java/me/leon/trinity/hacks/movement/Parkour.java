package me.leon.trinity.hacks.movement;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;

public class Parkour extends Module {
	public Parkour() {
		super("Parkour", "Automatically jumps for you", Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		if (nullCheck())
			return;

		if (mc.player.onGround && !mc.player.isSneaking() && !mc.gameSettings.keyBindSneak.isPressed() && !mc.gameSettings.keyBindJump.isPressed() && mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)).isEmpty())
			mc.player.jump();
	}
}
