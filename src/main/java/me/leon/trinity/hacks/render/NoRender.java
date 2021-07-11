package me.leon.trinity.hacks.render;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRender extends Module {
	public static BooleanSetting items = new BooleanSetting("Items", false);
	public static BooleanSetting hurtCam = new BooleanSetting("HurtCam", true);
	public static BooleanSetting bosslist = new BooleanSetting("BossList", false);
	public static BooleanSetting fire = new BooleanSetting("Fire", true);
	public static BooleanSetting water = new BooleanSetting("Water", true);
	public static BooleanSetting signs = new BooleanSetting("Signs", false);
	public static BooleanSetting armor = new BooleanSetting("Armor", false);
	public static BooleanSetting weather = new BooleanSetting("Weather", false);
	public static BooleanSetting noCluster = new BooleanSetting("NoCluster", true);
	public static BooleanSetting storage = new BooleanSetting("Storage", true, false);
	public static BooleanSetting shulkers = new BooleanSetting("Shulkers", storage, false);
	public static BooleanSetting chests = new BooleanSetting("Chests", storage, false);
	public static BooleanSetting EChests = new BooleanSetting("Ender Chests", storage, false);

	public NoRender() {
		super("NoRender", "Prevents rendering of certain stuff", Category.RENDER);
	}

	@SubscribeEvent
	public void onRenderBlock(RenderBlockOverlayEvent event) {
		if (fire.getValue() && event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.FIRE)) {
			event.setCanceled(true);
		}

		if (water.getValue() && event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.WATER)) {
			event.setCanceled(true);
		}
	}
}
