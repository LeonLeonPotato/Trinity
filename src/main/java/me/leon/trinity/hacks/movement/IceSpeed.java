package me.leon.trinity.hacks.movement;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import net.minecraft.init.Blocks;

public class IceSpeed extends Module {
	public IceSpeed() {
		super("IceSpeed", "Speeds you up on Ice", Category.MOVEMENT);
	}

	public void onUpdate() {
		if (nullCheck())
			return;
		Blocks.ICE.setDefaultSlipperiness(0.4f);
		Blocks.PACKED_ICE.setDefaultSlipperiness(0.4f);
		Blocks.FROSTED_ICE.setDefaultSlipperiness(0.4f);
	}

	public void onDisable() {
		Blocks.ICE.setDefaultSlipperiness(0.98f);
		Blocks.PACKED_ICE.setDefaultSlipperiness(0.98f);
		Blocks.FROSTED_ICE.setDefaultSlipperiness(0.98f);
	}
}
