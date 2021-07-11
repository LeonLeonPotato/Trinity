package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.entity.BlockUtils;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.MessageBus;
import me.leon.trinity.utils.world.HoleUtils;
import me.leon.trinity.utils.world.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class HoleFill extends Module {
	public static BooleanSetting doubleHoles = new BooleanSetting("DoubleHoles", false);
	public static SliderSetting bps = new SliderSetting("Blocks Per Tick", 1, 3, 8, true);
	public static SliderSetting range = new SliderSetting("Range", 1, 3, 8, true);
	public static BooleanSetting toggle = new BooleanSetting("Toggle", false);
	public static BooleanSetting packet = new BooleanSetting("Packet", false);
	public static BooleanSetting antiGlitch = new BooleanSetting("AntiGlitch", false);
	public static BooleanSetting rotate = new BooleanSetting("Rotate", false);
	public static BooleanSetting smart = new BooleanSetting("Smart", true, true);
	public static SliderSetting distance = new SliderSetting("Distance", smart, 0, 1, 3, false);
	public static BooleanSetting predict = new BooleanSetting("Predict", smart, true);
	public static SliderSetting ticks = new SliderSetting("Ticks", smart, 1, 3, 5, true);

	public HoleFill() {
		super("HoleFill", "Fills holes near you", Category.COMBAT);
	}

	public void onEnable() {
		if (nullCheck()) return;
	}

	public void onUpdate() {
		if (nullCheck()) return;
		int oldSlot = mc.player.inventory.currentItem;
		int placed = 0;
		final ArrayList<HoleUtils.Hole> holeList = HoleUtils.holes((float) range.getValue(), -1);

		if(smart.getValue()) {
			final EntityPlayer target = (EntityPlayer) EntityUtils.getTarget(true, false, false, false, false, 10f, EntityUtils.toMode("Closest"));
			if(target == null) return;
			final Vec3d vec = predict.getValue() ? target.getPositionVector() : MathUtils.extrapolatePlayerPosition(target, (int) ticks.getValue());
			holeList.removeIf(e -> {
				if(e instanceof HoleUtils.SingleHole) {
					if(vec.squareDistanceTo(new Vec3d(((HoleUtils.SingleHole) e).pos).add(0.5, 0.5, 0.5)) >= distance.getValue() * distance.getValue()) return true;
				} else {
					final HoleUtils.Hole hole = HoleUtils.getHole(EntityUtils.getEntityPosFloored(target), 1);
					if(hole instanceof HoleUtils.DoubleHole && holeList.contains(hole)) return true;
					if(vec.squareDistanceTo(new Vec3d(((HoleUtils.DoubleHole) e).pos).add(0.5, 0.5, 0.5)) >= distance.getValue() * distance.getValue()) return true;
					return vec.squareDistanceTo(new Vec3d(((HoleUtils.DoubleHole) e).pos1).add(0.5, 0.5, 0.5)) >= distance.getValue() * distance.getValue();
				}
				return false;
			});
		}

		if(holeList.isEmpty()) return;
		if(!matCheck()) return;

		for (final HoleUtils.Hole hole : holeList) {
			if (placed >= bps.getValue()) continue;

			if (hole instanceof HoleUtils.SingleHole && !EntityUtils.isIntercepted(((HoleUtils.SingleHole) hole).pos) && WorldUtils.empty.contains(WorldUtils.getBlock(((HoleUtils.SingleHole) hole).pos))) {
				BlockUtils.placeBlock(((HoleUtils.SingleHole) hole).pos, rotate.getValue(), false, true, packet.getValue(), true, antiGlitch.getValue());
				placed++;
			}
			if (placed >= bps.getValue()) continue;

			if (hole instanceof HoleUtils.DoubleHole && doubleHoles.getValue()) {
				if (placed >= bps.getValue()) continue;

				final HoleUtils.DoubleHole doubleH = ((HoleUtils.DoubleHole) hole);
				if (getDist(doubleH.pos) && !EntityUtils.isIntercepted(doubleH.pos) && WorldUtils.empty.contains(WorldUtils.getBlock(doubleH.pos))) {
					BlockUtils.placeBlock(doubleH.pos, rotate.getValue(), false, true, packet.getValue(), true, antiGlitch.getValue());
					placed++;
				}
				if (placed >= bps.getValue()) continue;

				if (getDist(doubleH.pos1) && !EntityUtils.isIntercepted(doubleH.pos1) && WorldUtils.empty.contains(WorldUtils.getBlock(doubleH.pos1))) {
					BlockUtils.placeBlock(doubleH.pos1, rotate.getValue(), false, true, packet.getValue(), true, antiGlitch.getValue());
					placed++;
				}
			}
		}

		InventoryUtil.switchToSlot(oldSlot);

		if (placed == 0 && holeList.isEmpty()) {
			if (toggle.getValue()) {
				toggle0("Finished Holefilling, toggling!");
			}
		}
	}

	private boolean getDist(BlockPos pos) {
		if(nullCheck() || pos == null) return false;
		return pos.add(0.5, 0.5, 0.5).distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) < Math.pow(range.getValue(), 2);
	}

	private boolean matCheck() {
		if (!InventoryUtil.switchTo(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
			if (!InventoryUtil.switchTo(Item.getItemFromBlock(Blocks.ENDER_CHEST))) {
				if(toggle.getValue()) {
					toggle0("Cannot find materials!");
				}
			}
		}
		return this.isEnabled();
	}

	private void toggle0(String message) {
		MessageBus.sendClientMessage(message, true);
		this.setEnabled(false);
	}
}
