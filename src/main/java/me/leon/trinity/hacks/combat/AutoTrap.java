package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.entity.BlockUtils;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.misc.MessageBus;
import me.leon.trinity.utils.rendering.Tessellator;
import me.leon.trinity.utils.world.HoleUtils;
import me.leon.trinity.utils.world.WorldUtils;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class AutoTrap extends Module {
	public static BooleanSetting antiStep = new BooleanSetting("AntiStep", true);
	public static SliderSetting bps = new SliderSetting("Blocks per Tick", 1, 4, 8, true);
	public static SliderSetting placeRange = new SliderSetting("PlaceRage", 0, 5.5, 7, false);
	public static BooleanSetting antiGlitch = new BooleanSetting("NoGlitch", true);
	public static BooleanSetting packet = new BooleanSetting("Packet", false);
	public static BooleanSetting rotate = new BooleanSetting("Rotate", false);
	public static BooleanSetting strict = new BooleanSetting("Strict", false);
	public static BooleanSetting onlyInHole = new BooleanSetting("OnlyInHole", true);
	public static BooleanSetting toggle = new BooleanSetting("Toggle", false);

	public static BooleanSetting draw = new BooleanSetting("Draw", true, true);
	public static ModeSetting drawMode = new ModeSetting("Mode", draw, "Fill", "Fill", "Outline", "Both", "Claw");
	public static SliderSetting drawWidth = new SliderSetting("Width", draw, 0.1, 2, 5, false);
	public static SliderSetting length = new SliderSetting("Length", draw, 0.1, 0.3, 0.5, false);
	public static ColorSetting fillColor = new ColorSetting("FillColor", draw, 255, 0, 255, 200, false);
	public static ColorSetting outlineColor = new ColorSetting("OutlineColor", draw, 255, 0, 255, 200, false);

	public static BooleanSetting targeting = new BooleanSetting("Targeting", true, false);
	public static ModeSetting targetingMode = new ModeSetting("Mode", targeting, "Closest", "Closest", "Lowest Health", "Highest Health");
	public static SliderSetting targetRange = new SliderSetting("Range", targeting, 0.5, 5, 12, false);
	public static BooleanSetting players = new BooleanSetting("Players", targeting, true);
	public static BooleanSetting friends = new BooleanSetting("Friends", targeting, true);
	public static BooleanSetting neutral = new BooleanSetting("Neutral", targeting, true);
	public static BooleanSetting passive = new BooleanSetting("Passive", targeting, true);
	public static BooleanSetting hostile = new BooleanSetting("Hostile", targeting, true);
	public static EntityLivingBase target;
	private final ArrayList<BlockPos> placed = new ArrayList<>();
	private final int[][] trapBlocks = {
			{0, 1, -1},
			{-1, 1, 0},
			{0, 1, 1},
			{1, 1, 0},
	};

	public AutoTrap() {
		super("AutoTrap", "Automatically traps your target", Category.COMBAT);
	}

	@Override
	public String getHudInfo() {
		return target == null ? null : target.getName();
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (!draw.getValue()) return;
		for (BlockPos pos : this.placed) {
			final AxisAlignedBB bb = new AxisAlignedBB(pos);
			if (drawMode.getValue().equalsIgnoreCase("Outline") || drawMode.getValue().equalsIgnoreCase("Both")) {
				Tessellator.drawBBOutline(bb, (float) drawWidth.getValue(), outlineColor.getValue());
			}
			if (drawMode.getValue().equalsIgnoreCase("Fill") || drawMode.getValue().equalsIgnoreCase("Both")) {
				Tessellator.drawBBFill(bb, fillColor.getValue());
			}
			if (drawMode.getValue().equalsIgnoreCase("Claw")) {
				Tessellator.drawBBClaw(bb, (float) drawWidth.getValue(), (float) length.getValue(), outlineColor.getValue());
			}
		}
	}

	@Override
	public void onUpdate() {
		if (nullCheck()) return;
		target = EntityUtils.getTarget(players.getValue(), neutral.getValue(), friends.getValue(), hostile.getValue(), passive.getValue(), targetRange.getValue(), EntityUtils.toMode(targetingMode.getValue()));

		if (target == null) {
			if (toggle.getValue()) toggle0("Target not found!");
			return;
		}
		this.placed.clear();
		autoTrap();
	}

	private void autoTrap() {
		final HoleUtils.Hole hole = HoleUtils.getHole(EntityUtils.getEntityPosFloored(target), -1);
		final boolean inHole = EntityUtils.isInHole(target);

		if (!inHole && onlyInHole.getValue() && hole == null) {
			if (toggle.getValue()) toggle0("Target not in Hole!");
			return;
		}

		int old = mc.player.inventory.currentItem;

		if (!InventoryUtil.switchTo(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
			if (!InventoryUtil.switchTo(Item.getItemFromBlock(Blocks.ENDER_CHEST))) {
				toggle0("Cannot find any blocks!");
				return;
			}
		}

		if (hole instanceof HoleUtils.DoubleHole || !onlyInHole.getValue() && EntityUtils.getPos(0, 0, 0, target).size() == 2) {
			BlockPos pos = null;
			BlockPos pos1 = null;
			if (!inHole) {
				final ArrayList<BlockPos> targetPoses = EntityUtils.getPos(0, 0, 0, target);
				if(targetPoses.size() <= 1) {
					return;
				}
				pos = targetPoses.get(0);
				pos1 = targetPoses.get(1);
			} else if (hole instanceof HoleUtils.DoubleHole) {
				pos = ((HoleUtils.DoubleHole) hole).pos1;
				pos1 = ((HoleUtils.DoubleHole) hole).pos;
			}

			if (pos == null) return;

			int placed = 0;
			BlockPos medPos = null;
			boolean need = true;

			ArrayList<BlockPos> possible = new ArrayList<>();

			for (int[] add : trapBlocks) {
				final BlockPos pos0 = pos.add(add[0], add[1], add[2]);
				final BlockPos pos3 = pos.add(add[0], add[1] + 1, add[2]);
				if (!pos3.equals(pos1.add(0, 2, 0))) {
					possible.add(pos3);
				}
				if (pos0.equals(pos1.add(0, 1, 0))) {
					continue;
				}
				if (WorldUtils.getBlock(pos0) instanceof BlockEnderChest || WorldUtils.getBlock(pos0) instanceof BlockObsidian) {
					continue;
				}
				if (placed >= bps.getValue()) {
					continue;
				}
				if (pos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) > Math.pow(placeRange.getValue(), 2))
					continue;
				BlockUtils.placeBlock(pos0, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
				this.placed.add(pos0);
				placed++;
			}

			if (placed >= bps.getValue()) return;

			for (int[] add : trapBlocks) {
				final BlockPos pos0 = pos1.add(add[0], add[1], add[2]);
				final BlockPos pos3 = pos1.add(add[0], add[1] + 1, add[2]);
				if (!pos3.equals(pos.add(0, 2, 0))) {
					possible.add(pos3);
				}
				if (pos0.equals(pos.add(0, 1, 0))) {
					continue;
				}
				if (WorldUtils.getBlock(pos0) instanceof BlockEnderChest || WorldUtils.getBlock(pos0) instanceof BlockObsidian) {
					continue;
				}
				if (placed >= bps.getValue()) {
					continue;
				}
				if (pos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) > Math.pow(placeRange.getValue(), 2))
					continue;
				BlockUtils.placeBlock(pos0, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
				this.placed.add(pos0);
				placed++;
			}

			if (placed >= bps.getValue()) return;

			for (BlockPos medPos0 : possible) {
				if (!WorldUtils.empty.contains(WorldUtils.getBlock(medPos0))) {
					need = false;
				} else if (!EntityUtils.isIntercepted(medPos0)) {
					if (medPos == null) {
						medPos = medPos0;
					} else {
						if (medPos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) < medPos.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ)) {
							medPos = medPos0;
						}
					}
				}
			}

			final BlockPos pos3 = pos.add(0, 2, 0);
			final BlockPos pos4 = pos1.add(0, 2, 0);

			if (need && medPos != null && WorldUtils.empty.contains(WorldUtils.getBlock(pos3)) && WorldUtils.empty.contains(WorldUtils.getBlock(pos4))) {
				if (medPos.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
					BlockUtils.placeBlock(medPos, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
					this.placed.add(medPos);
					placed++;
				}
			}

			if (placed >= bps.getValue()) return;

			if (!(WorldUtils.getBlock(pos3) instanceof BlockEnderChest) && !(WorldUtils.getBlock(pos3) instanceof BlockObsidian)) {
				if (pos3.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
					BlockUtils.placeBlock(pos3, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
					this.placed.add(pos3);
					placed++;
				}
			}

			if (placed >= bps.getValue()) return;

			if (!(WorldUtils.getBlock(pos4) instanceof BlockEnderChest) && !(WorldUtils.getBlock(pos4) instanceof BlockObsidian)) {
				if (pos4.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
					BlockUtils.placeBlock(pos4, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
					this.placed.add(pos4);
					placed++;
				}
			}

			if (!antiStep.getValue()) return;
			if (placed >= bps.getValue()) return;

			if (!(WorldUtils.getBlock(pos3.add(0, 1, 0)) instanceof BlockEnderChest) && !(WorldUtils.getBlock(pos3.add(0, 1, 0)) instanceof BlockObsidian)) {
				if (pos3.add(0, 1, 0).distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
					BlockUtils.placeBlock(pos3.add(0, 1, 0), rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
					this.placed.add(pos3.add(0, 1, 0));
					placed++;
				}
			}

			if (placed >= bps.getValue()) return;

			if (!(WorldUtils.getBlock(pos4.add(0, 1, 0)) instanceof BlockEnderChest) && !(WorldUtils.getBlock(pos4.add(0, 1, 0)) instanceof BlockObsidian)) {
				if (pos4.add(0, 1, 0).distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
					BlockUtils.placeBlock(pos4.add(0, 1, 0), rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
					this.placed.add(pos4.add(0, 1, 0));
				}
			}

		}
		if (hole instanceof HoleUtils.SingleHole || !onlyInHole.getValue() && EntityUtils.getPos(0, 0, 0, target).size() == 1) {
			BlockPos pos = EntityUtils.getEntityPosFloored(target);
			int placed = 0;
			boolean need = true;
			BlockPos medPos = null;

			for (int[] add : trapBlocks) {
				if (hole != null || inHole) continue;
				final BlockPos pos0 = pos.add(add[0], 0, add[2]);
				if (WorldUtils.getBlock(pos0) instanceof BlockEnderChest || WorldUtils.getBlock(pos0) instanceof BlockObsidian) {
					continue;
				}
				if (placed >= bps.getValue()) {
					continue;
				}
				if (pos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) > Math.pow(placeRange.getValue(), 2))
					continue;
				BlockUtils.placeBlock(pos0, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
				this.placed.add(pos0);
				placed++;
			}

			for (int[] add : trapBlocks) {
				final BlockPos pos0 = pos.add(add[0], add[1], add[2]);
				if (WorldUtils.getBlock(pos0) instanceof BlockEnderChest || WorldUtils.getBlock(pos0) instanceof BlockObsidian) {
					continue;
				}
				if (placed >= bps.getValue()) {
					continue;
				}
				if (pos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) > Math.pow(placeRange.getValue(), 2))
					continue;
				BlockUtils.placeBlock(pos0, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
				this.placed.add(pos0);
				placed++;
			}

			for (int[] add : trapBlocks) {
				final BlockPos pos0 = pos.add(add[0], add[1] + 1, add[2]);
				if (!WorldUtils.empty.contains(WorldUtils.getBlock(pos0))) {
					need = false;
				} else if (!EntityUtils.isIntercepted(pos0)) {
					if (medPos == null) {
						medPos = pos0;
					} else {
						if (pos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) < medPos.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ)) {
							medPos = pos0;
						}
					}
				}
			}

			if (placed >= bps.getValue()) return;

			if (need && medPos != null && WorldUtils.empty.contains(WorldUtils.getBlock(pos.add(0, 2, 0)))) {
				if (medPos.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
					BlockUtils.placeBlock(medPos, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
					this.placed.add(medPos);
					placed++;
				}
			}

			if (placed >= bps.getValue()) return;

			if (WorldUtils.empty.contains(WorldUtils.getBlock(pos.add(0, 2, 0)))) {
				if (pos.add(0, 2, 0).distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
					BlockUtils.placeBlock(pos.add(0, 2, 0), rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
					this.placed.add(pos.add(0, 2, 0));
					placed++;
				}
			}

			if (placed >= bps.getValue()) return;

			if (WorldUtils.empty.contains(WorldUtils.getBlock(pos.add(0, 3, 0))) && antiStep.getValue()) {
				if (pos.add(0, 3, 0).distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
					BlockUtils.placeBlock(pos.add(0, 3, 0), rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
					this.placed.add(pos.add(0, 3, 0));
				}
			}
		}

		if (EntityUtils.isTrapped(target, antiStep.getValue()) && toggle.getValue()) {
			toggle0("Trapped Target! Toggling!");
		}

		InventoryUtil.switchToSlot(old);
	}

	private void toggle0(String message) {
		MessageBus.sendClientMessage(message, true);
		this.setEnabled(false);
	}
}
