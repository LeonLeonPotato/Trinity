package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.managers.SpoofingManager;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.world.Rotation.Rotation;
import me.leon.trinity.utils.world.Priority;
import me.leon.trinity.utils.world.Timer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Quiver extends Module {
	public static SliderSetting delay = new SliderSetting("Delay", 0, 200, 1000, true);
	public static SliderSetting holdLength = new SliderSetting("HoldLength", 0, 200, 1000, true);
	public static ModeSetting main = new ModeSetting("Main", "Strength", "Strength", "Speed", "None");
	public static ModeSetting secondary = new ModeSetting("Secondary", "Strength", "Strength", "Speed", "None");
	private final Timer delayTimer = new Timer();
	private final Timer holdTimer = new Timer();
	private int stage = 0;
	private ArrayList<Integer> map;
	private int strSlot = -1;
	private int speedSlot = -1;
	private int oldSlot = 1;

	public Quiver() {
		super("Quiver", "Shoots an arrow over you", Category.COMBAT);
	}

	@Override
	public void onEnable() {
		if (nullCheck()) return;
		InventoryUtil.switchTo(Items.BOW);
		clean();
		this.oldSlot = mc.player.inventory.currentItem;
		mc.gameSettings.keyBindUseItem.pressed = false;
	}

	@Override
	public void onDisable() {
		if (nullCheck()) return;
		InventoryUtil.switchToSlot(oldSlot);
		mc.gameSettings.keyBindUseItem.pressed = false;
		clean();
	}

	@Override
	public void onUpdate() {
		if (nullCheck()) return;
		if (mc.currentScreen != null) return;

		if (InventoryUtil.find(Items.BOW) == -1) {
			toggleWithMessage("Couldn't find bow in inventory! Toggling!");
			return;
		}

		SpoofingManager.rotationQueue.add(new Rotation(-90, mc.player.rotationYaw, true, Priority.High));

		if (stage == 0) { // stage one is mapping arrows
			this.map = mapArrows();
			for (int a : map) {
				final ItemStack arrow = mc.player.inventoryContainer.getInventory().get(a);
				if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_STRENGTH)) {
					if (strSlot == -1) {
						this.strSlot = a;
					}
				}
				if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_SWIFTNESS)) {
					if (speedSlot == -1) {
						this.speedSlot = a;
					}
				}
			}
			this.stage++;
		} else if (stage == 1) { // wait
			if (!delayTimer.hasPassAndReset((int) delay.getValue())) return;
			this.stage++;
		} else if (stage == 2) { // switch
			switchTo(main.getValue());
			this.stage++;
		} else if (stage == 3) { // wait
			if (!delayTimer.hasPassAndReset((int) delay.getValue())) return;
			this.stage++;
		} else if (stage == 4) { // charge
			mc.gameSettings.keyBindUseItem.pressed = true;
			holdTimer.reset();
			this.stage++;
		} else if (stage == 5) { // wait
			if (!holdTimer.hasPassed((int) holdLength.getValue())) return;
			holdTimer.reset();
			this.stage++;
		} else if (stage == 6) { // shoot
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
			mc.player.resetActiveHand();
			mc.gameSettings.keyBindUseItem.pressed = false;
			this.stage++;
		} else if (stage == 7) { // wait
			if (!delayTimer.hasPassAndReset((int) delay.getValue())) return;
			this.stage++;
		} else if (stage == 8) { // map again
			this.map = mapArrows();
			this.strSlot = -1;
			this.speedSlot = -1;
			for (int a : map) {
				final ItemStack arrow = mc.player.inventoryContainer.getInventory().get(a);
				if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_STRENGTH)) {
					if (strSlot == -1)
						this.strSlot = a;
				}
				if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_SWIFTNESS)) {
					if (speedSlot == -1)
						this.speedSlot = a;
				}
			}
			this.stage++;
		}
		if (stage == 9) { // switch
			switchTo(secondary.getValue());
			this.stage++;
		} else if (stage == 10) { // wait
			if (!delayTimer.hasPassAndReset((int) delay.getValue())) return;
			this.stage++;
		} else if (stage == 11) { // charge
			mc.gameSettings.keyBindUseItem.pressed = true;
			holdTimer.reset();
			this.stage++;
		} else if (stage == 12) { // wait
			if (!holdTimer.hasPassed((int) holdLength.getValue())) return;
			holdTimer.reset();
			this.stage++;
		} else if (stage == 13) { // shoot
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
			mc.player.resetActiveHand();
			mc.gameSettings.keyBindUseItem.pressed = false;
			this.stage++;
		} else if (stage == 14) { // fix for still holding item
			final ArrayList<Integer> map = mapEmpty();
			if(!map.isEmpty()) {
				final int a = map.get(0);
				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, a, 0, ClickType.PICKUP, mc.player);
			}
			this.stage++;
		} else if (stage == 15) { // clean up
			this.setEnabled(false);
		}
	}

	private void switchTo(String mode) {
		if (mode.equalsIgnoreCase("Strength")) {
			if (strSlot != -1) {
				switchTo(strSlot);
			}
		}
		if (mode.equalsIgnoreCase("Speed")) {
			if (speedSlot != -1) {
				switchTo(speedSlot);
			}
		}
	}

	private ArrayList<Integer> mapArrows() {
		ArrayList<Integer> map = new ArrayList<>();
		for (int a = 9; a < 45; a++) {
			if (mc.player.inventoryContainer.getInventory().get(a).getItem() instanceof ItemTippedArrow) {
				final ItemStack arrow = mc.player.inventoryContainer.getInventory().get(a);
				if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_STRENGTH)) {
					map.add(a);
				}
				if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_SWIFTNESS)) {
					map.add(a);
				}
			}
		}
		return map;
	}

	private ArrayList<Integer> mapEmpty() {
		ArrayList<Integer> map = new ArrayList<>();
		for (int a = 9; a < 45; a++) {
			if (mc.player.inventoryContainer.getInventory().get(a).getItem() instanceof ItemAir || mc.player.inventoryContainer.getInventory().get(a) == ItemStack.EMPTY) {
				map.add(a);
			}
		}
		return map;
	}

	private void switchTo(int from) {
		if(from == 9) return;
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
		mc.playerController.updateController();
	}

	private boolean hasEffect(String mode) {
		if (mode.equalsIgnoreCase("Strength")) {
			if (mc.player.isPotionActive(MobEffects.STRENGTH)) return true;
		}
		if (mode.equalsIgnoreCase("Speed")) {
            return mc.player.isPotionActive(MobEffects.SPEED);
		}
		return false;
	}

	private void clean() {
		this.holdTimer.reset();
		this.delayTimer.reset();
		this.map = null;
		this.speedSlot = -1;
		this.strSlot = -1;
		this.stage = 0;
	}
}
