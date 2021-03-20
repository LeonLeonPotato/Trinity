package me.leon.trinity.hacks.movement;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Slider;

public class ReverseStep extends Module {
    public ReverseStep(){
        super("ReverseStep","moves down", Category.MOVEMENT);
    }
	//gamesense
    public static Slider height = new Slider("Height", 2.5, 0.5, 10, true);

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