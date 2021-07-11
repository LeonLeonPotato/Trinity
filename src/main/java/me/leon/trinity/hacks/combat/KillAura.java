package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.managers.TickrateManager;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.misc.MessageBus;
import me.leon.trinity.utils.world.RaytraceUtils;
import me.leon.trinity.utils.world.Rotation.RotationUtils;
import me.leon.trinity.utils.world.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

public class KillAura extends Module {
	public static BooleanSetting main = new BooleanSetting("Main", true, false);
	public static BooleanSetting chat = new BooleanSetting("Chat", main, false);
	public static BooleanSetting packet = new BooleanSetting("Packet", main, true);
	public static BooleanSetting onlyHolding = new BooleanSetting("Only Holding", main, true);
	public static ModeSetting Switch = new ModeSetting("Switch", main, "None", "None", "Normal");

	public static BooleanSetting ranges = new BooleanSetting("Ranges", true, false);
	public static ModeSetting rayTraceMode = new ModeSetting("RayTrace Mode", ranges, "Leon", "Leon", "Simple", "Offset-Simple");
	public static SliderSetting off = new SliderSetting("Offset", ranges, 0, 1, 3, false);
	public static SliderSetting wallsRange = new SliderSetting("Wall Range", ranges, 0, 3.5, 5, false);
	public static SliderSetting normalRange = new SliderSetting("Normal Range", ranges, 1, 5, 6, false);

	public static BooleanSetting rotate = new BooleanSetting("Rotate", true, true);
	public static ModeSetting rotation = new ModeSetting("Mode", rotate, "Packet", "Packet", "Client", "Random");
	public static SliderSetting offset = new SliderSetting("Offset", rotate, 0, 1, 2, false);

	public static BooleanSetting delays = new BooleanSetting("Delays", true, false);
	public static ModeSetting delayMode = new ModeSetting("Mode", delays, "Ready", "Custom", "Ready");
	public static SliderSetting delay = new SliderSetting("Delay", delays, 10, 700, 2000, false);
	public static BooleanSetting sync = new BooleanSetting("Sync", delays, false);

	public static BooleanSetting experimental = new BooleanSetting("Experimental", true, false);
	public static BooleanSetting armorMelt = new BooleanSetting("Armor Melt", experimental, true);

	public static BooleanSetting targeting = new BooleanSetting("Targeting", true, false);
	public static ModeSetting targetingMode = new ModeSetting("Mode", targeting, "Closest", "Closest", "Lowest Health", "Highest Health");
	public static SliderSetting targetRange = new SliderSetting("Range", targeting, 0.5, 5, 10, false);
	public static BooleanSetting players = new BooleanSetting("Players", targeting, true);
	public static BooleanSetting friends = new BooleanSetting("Friends", targeting, true);
	public static BooleanSetting neutral = new BooleanSetting("Neutral", targeting, true);
	public static BooleanSetting passive = new BooleanSetting("Passive", targeting, true);
	public static BooleanSetting hostile = new BooleanSetting("Hostile", targeting, true);

	public static EntityLivingBase target = null;
	private final Timer timer;

	public KillAura() {
		super("KillAura", "Attacks Entities nearby", Category.COMBAT);
		this.timer = new Timer();
	}

	@Override
	public String getHudInfo() {
		return target == null ? null : target.getName();
	}

	@Override
	public void onEnable() {
		clean();
	}

	@Override
	public void onUpdate() {
		if (nullCheck()) return;
		EntityLivingBase target1 = target;
		target = EntityUtils.getTarget(players.getValue(), neutral.getValue(), friends.getValue(), hostile.getValue(), passive.getValue(), targetRange.getValue(), EntityUtils.toMode(targetingMode.getValue()));
		if (target == null) {
			clean();
			return;
		}
		if (target1 != target && chat.getValue()) {
			MessageBus.sendClientMessage("Found new target: " + target.getName(), true);
		}
		killAura();
	}

	private void killAura() {
		if (EntityUtils.getRange(target) > normalRange.getValue()) return;

		Vec3d vec = null;
		if (rayTraceMode.getValue().equalsIgnoreCase("Leon")) {
			vec = RaytraceUtils.rayTraceLeon(target);
			if (vec == null) {
				if (EntityUtils.getRange(target) > wallsRange.getValue()) {
					clean();
					return;
				} else {
					vec = target.getPositionVector().add(0, target.height / 2, 0);
				}
			}
		} else if (rayTraceMode.getValue().equalsIgnoreCase("Simple")) {
			vec = target.getPositionVector().add(0, target.height / 2, 0);
			if (!RaytraceUtils.rayTraceSimple(target)) {
				if (EntityUtils.getRange(target) > wallsRange.getValue()) {
					clean();
					return;
				}
			}
		} else if (rayTraceMode.getValue().equalsIgnoreCase("Offset-Simple")) {
			vec = target.getPositionVector().add(0, target.height / 2, 0);
			if (!RaytraceUtils.rayTraceSimple(target, off.getValue())) {
				if (EntityUtils.getRange(target) > wallsRange.getValue()) {
					clean();
					return;
				}
			}
		}

		int slot = InventoryUtil.find(ItemSword.class);
		int slot0 = InventoryUtil.find(ItemAxe.class);
		if (Switch.getValue().equalsIgnoreCase("Normal")) {
			if (slot != -1) {
				mc.player.inventory.currentItem = slot;
			} else if (slot0 != -1) {
				mc.player.inventory.currentItem = slot0;
			} else {
				clean();
				return;
			}
			mc.playerController.updateController();
		} else {
			if (slot != mc.player.inventory.currentItem && slot0 != mc.player.inventory.currentItem && onlyHolding.getValue()) {
				clean();
				return;
			}
		}

		if (rotate.getValue()) {
			if (rotation.getValue().equalsIgnoreCase("Random")) {
				RotationUtils.rotateRandom(true);
			} else {
				RotationUtils.rotateTowards(vec, rotation.getValue().equalsIgnoreCase("Packet"));
			}
		}

		if (delayMode.getValue().equalsIgnoreCase("Ready")) {
			if (armorMelt.getValue())
				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);

			attackEntity(target, true, sync.getValue(), packet.getValue());

			if (armorMelt.getValue()) {
				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
				attackEntity(target, false, sync.getValue(), packet.getValue());
			}
		} else if (delayMode.getValue().equalsIgnoreCase("Custom")) {
			if (timer.hasPassed((int) delay.getValue())) {
				this.timer.reset();

				attackEntity(target, false, sync.getValue(), packet.getValue());
			}
		}
	}

	private void attackEntity(Entity entity, boolean cooldown, boolean sync, boolean packet) {
		if (!cooldown || (mc.player.getCooledAttackStrength(sync ? (20 - TickrateManager.getTPS()) : 0) >= 1)) {
			if (packet)
				mc.player.connection.sendPacket(new CPacketUseEntity(entity));
			else
				mc.playerController.attackEntity(mc.player, entity);

			mc.player.swingArm(EnumHand.MAIN_HAND);
			mc.player.resetCooldown();
		}
	}

	private void clean() {
		this.timer.reset();
	}
}
