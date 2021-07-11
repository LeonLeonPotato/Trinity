package me.leon.trinity.hacks.movement;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import net.minecraft.client.settings.KeyBinding;

public class AutoWalk extends Module {
	public AutoWalk() {
		super("AutoWalk", "automatically walks", Category.MOVEMENT);
	}

	@Override
	public void onUpdate() {
		if (nullCheck())
			return;

		KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
	}
}
