package me.leon.trinity.hacks.movement;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;

public class Yaw extends Module {
	public Yaw() {
		super("Yaw", "locks your rotation", Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		if (nullCheck())
			return;

		mc.player.rotationYaw = Math.round(mc.player.rotationYaw / 45f) * 45f;;
	}
}