package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.managers.SpoofingManager;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.KeybindSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.misc.MessageBus;
import me.leon.trinity.utils.world.Rotation.Rotation;
import me.leon.trinity.utils.world.Priority;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collection;

public class AutoMend extends Module {
	public static ModeSetting mode = new ModeSetting("Mode", "Hold", "Hold", "Toggle");
	public static BooleanSetting clientRotations = new BooleanSetting("ClientRotations", false);
	public static BooleanSetting predict = new BooleanSetting("Predict", true);
	public static BooleanSetting packet = new BooleanSetting("Packet", true);
	public static KeybindSetting macroBind = new KeybindSetting("Hold Bind", Keyboard.KEY_O);

	public static BooleanSetting stopVals = new BooleanSetting("StopVals", true, false);
	public static SliderSetting boots = new SliderSetting("Boots", stopVals, 0, 90, 100, true);
	public static SliderSetting leggings = new SliderSetting("Leggings", stopVals, 0, 90, 100, true);
	public static SliderSetting chestPlate = new SliderSetting("Chestplate", stopVals, 0, 90, 100, true);
	public static SliderSetting helmet = new SliderSetting("Helmet", stopVals, 0, 90, 100, true);
	private int oldslot;
	private boolean started;
	public AutoMend() {
		super("AutoMend", "Automatically mends armor", Category.COMBAT);
	}

	@Override
	public void onEnable() {
		if (nullCheck()) return;
		start(mode.getValue().equalsIgnoreCase("Toggle"));
	}

	@Override
	public void onDisable() {
		if (nullCheck()) return;
		end();
	}

	@Override
	public void onUpdate() {
		if (nullCheck()) return;
		if (mode.getValue().equalsIgnoreCase("Hold")) {
			if (!macroBind.down()) {
				if (started) {
					end();
					this.started = false;
				}
				return;
			} else {
				if (!started) {
					start(true);
					this.started = true;
				}
			}
		}

		final int xpSlot = InventoryUtil.find(Items.EXPERIENCE_BOTTLE);

		if (xpSlot == -1) {
			return;
		}

		if (clientRotations.getValue()) {
			mc.player.rotationPitch = 90;
		} else {
			SpoofingManager.rotationQueue.add(new Rotation(90, mc.player.rotationYaw, true, Priority.Normal));
		}

		if (packet.getValue()) {
			mc.player.connection.sendPacket(new CPacketHeldItemChange(xpSlot));
		} else {
			InventoryUtil.switchToSlot(xpSlot);
		}

		/** @author GameSense **/
		if (predict.getValue()) {
			int totalXp = mc.world.loadedEntityList.stream()
					.filter(entity -> entity instanceof EntityXPOrb)
					.filter(entity -> entity.getDistanceSq(mc.player) <= 1)
					.mapToInt(entity -> ((EntityXPOrb) entity).xpValue).sum();

			if ((totalXp * 2) < EntityUtils.getTotalArmor(mc.player)) {
				checkArmor();
				mend();
			} else {
				end();
				toggleWithMessage("Done Mending! Toggling!");
				return;
			}
		} else {
			checkArmor();
			mend();
		}

		if (EntityUtils.isNaked(mc.player)) {
			end();
			if (mode.getValue().equalsIgnoreCase("Toggle")) {
				toggleWithMessage("Done Mending! Toggling!");
			} else {
				started = false;
				MessageBus.sendClientMessage("Done Mending!", true);
			}
		}
	}

	private void checkArmor() {
		for (ItemStack stack : mc.player.getArmorInventoryList()) {
			if (stack == null || stack.getItem() == Items.AIR) {
				continue;
			}

			final float dmg = ((float) (stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage()) * 100f;
			final int foundSlot = findEmptySlot();

			if (stack.getItem() == Items.DIAMOND_BOOTS) {
				if (dmg >= boots.getValue()) {
					if (foundSlot != -1) {
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 8, 0, ClickType.PICKUP, mc.player);
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, foundSlot, 0, ClickType.PICKUP, mc.player);
					}
				}
			}
			if (stack.getItem() == Items.DIAMOND_CHESTPLATE) {
				if (dmg >= chestPlate.getValue()) {
					if (foundSlot != -1) {
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, foundSlot, 0, ClickType.PICKUP, mc.player);
					}
				}
			}
			if (stack.getItem() == Items.DIAMOND_LEGGINGS) {
				if (dmg >= leggings.getValue()) {
					if (foundSlot != -1) {
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 7, 0, ClickType.PICKUP, mc.player);
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, foundSlot, 0, ClickType.PICKUP, mc.player);
					}
				}
			}
			if (stack.getItem() == Items.DIAMOND_HELMET) {
				if (dmg >= helmet.getValue()) {
					if (foundSlot != -1) {
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 5, 0, ClickType.PICKUP, mc.player);
						mc.playerController.windowClick(mc.player.inventoryContainer.windowId, foundSlot, 0, ClickType.PICKUP, mc.player);
					}
				}
			}
		}
	}

	private void end() {
		AutoArmor.pause = false;
		AutoCrystal.pause = false;
		if (packet.getValue()) {
			mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
		} else {
			InventoryUtil.switchToSlot(oldslot);
		}
	}

	private void start(boolean hold) {
		AutoArmor.pause = true;
		AutoArmor.checkCrafting = true;
		if (hold) AutoCrystal.pause = true;
		this.oldslot = mc.player.inventory.currentItem;
	}

	private int findEmptySlot() {
		for (int a = 1; a < 5; a++) {
			final ItemStack stack = mc.player.inventoryContainer.getInventory().get(a);
			if (stack == ItemStack.EMPTY || stack == null || stack.getItem() == Items.AIR) {
				return a;
			}
		}
		for (int a = 9; a < 36; a++) {
			final ItemStack stack = mc.player.inventoryContainer.getInventory().get(a);
			if (stack == ItemStack.EMPTY || stack == null || stack.getItem() == Items.AIR) {
				return a;
			}
		}
		return -1;
	}

	private void mend() {
		mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
	}
}
