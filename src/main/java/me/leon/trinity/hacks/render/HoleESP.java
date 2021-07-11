package me.leon.trinity.hacks.render;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.entity.PlayerUtils;
import me.leon.trinity.utils.rendering.Tessellator;
import me.leon.trinity.utils.world.HoleUtils;
import me.leon.trinity.utils.world.WorldUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class HoleESP extends Module {
	public static ModeSetting mode = new ModeSetting("Mode", "Glow", "Fill", "Glow", "None");
	public static ModeSetting outline = new ModeSetting("Outline", "Claw", "Outline", "Claw", "None");

	public static BooleanSetting own = new BooleanSetting("Own", true, true);
	public static ModeSetting ownMode = new ModeSetting("Mode", own, "Glow", "Fill", "Glow", "None");
	public static ModeSetting ownOutline = new ModeSetting("Outline", own, "Outline", "Outline", "Claw", "None");
	public static SliderSetting ownCheckHeight = new SliderSetting("Check Height", own, 0, 1.5, 5, false);
	public static SliderSetting ownHeight = new SliderSetting("Height", own, 0, 1.5, 5, false);
	public static SliderSetting ownClawHeight = new SliderSetting("ClawHeight", own, 0, 0.3, 1, false);
	public static SliderSetting ownWidth = new SliderSetting("Width", own, 0.1, 2, 5, false);
	public static ColorSetting ownBedrockColor = new ColorSetting("Bedrock Color", own, 0, 255, 0, 150, false);
	public static ColorSetting ownObsidianColor = new ColorSetting("Obsidian Color", own, 0, 255, 0, 150, false);

	public static SliderSetting range = new SliderSetting("Range", 0, 8, 12, false);
	public static SliderSetting checkHeight = new SliderSetting("CheckHeight", 0, 1.5, 5, false);
	public static SliderSetting height = new SliderSetting("Height", 0, 1.5, 5, false);
	public static SliderSetting clawHeight = new SliderSetting("ClawHeight", 0, 0.3, 1, false);
	public static SliderSetting width = new SliderSetting("Width", 0.1, 2, 5, false);
	public static BooleanSetting doubleHoles = new BooleanSetting("Doubles", true);
	public static ColorSetting bedrockColor = new ColorSetting("Bedrock Color", 0, 255, 255, 150, false);
	public static ColorSetting obsidianColor = new ColorSetting("Obsidian Color", 255, 255, 0, 150, false);

	public HoleESP() {
		super("HoleESP", "Highlights holes", Category.RENDER);
	}

	@SubscribeEvent
	public void onWorldRender(RenderWorldLastEvent event) {
		List<BlockPos> poss = WorldUtils.getSphere(PlayerUtils.getPlayerPosFloored(), (float) range.getValue(), (int) range.getValue(), false, true, 0);

		List<HoleUtils.Hole> cachedHoles = new ArrayList<>();

		for (BlockPos pos : poss) {
			HoleUtils.Hole hole = HoleUtils.getHole(pos, (int) checkHeight.getValue());
			if (hole != null) {
				if (hole instanceof HoleUtils.DoubleHole) {
					if (!doubleHoles.getValue()) {
						continue;
					}
					boolean a = false;
					for (HoleUtils.Hole hole1 : cachedHoles) {
						if (hole1 instanceof HoleUtils.DoubleHole) {
							if (((HoleUtils.DoubleHole) hole1).contains(((HoleUtils.DoubleHole) hole))) {
								a = true;
								break;
							}
						}
					}
					if (!a) {
						cachedHoles.add(hole);
					}
				} else {
					cachedHoles.add(hole);
				}
			}
		}

		for (HoleUtils.Hole hole : cachedHoles) {
			if (hole instanceof HoleUtils.DoubleHole) {
				HoleUtils.DoubleHole pos = (HoleUtils.DoubleHole) hole;
				if (mc.player.getDistanceSqToCenter(pos.pos) > Math.pow(range.getValue(), 2) || mc.player.getDistanceSqToCenter(pos.pos1) > Math.pow(range.getValue(), 2)) {
					continue;
				}

				drawHoleESP(hole);
			}
			if (hole instanceof HoleUtils.SingleHole) {
				HoleUtils.SingleHole pos = (HoleUtils.SingleHole) hole;
				if (mc.player.getDistanceSqToCenter(pos.pos) > Math.pow(range.getValue(), 2)) {
					continue;
				}

				drawHoleESP(hole);
			}
		}
		drawOwnHoleESP();
	}

	private void drawHoleESP(HoleUtils.Hole hole) {
		final HoleUtils.Hole hole0 = HoleUtils.getHole(PlayerUtils.getPlayerPosFloored(), (int) checkHeight.getValue());
		if (own.getValue()) {
			if (hole0 != null) {
				if (hole0 instanceof HoleUtils.DoubleHole && hole instanceof HoleUtils.DoubleHole) {
					HoleUtils.DoubleHole pos = (HoleUtils.DoubleHole) hole0;
					HoleUtils.DoubleHole pos0 = (HoleUtils.DoubleHole) hole;
					if (pos.contains(pos0)) {
						return;
					}
				}
				if (hole0 instanceof HoleUtils.SingleHole && hole instanceof HoleUtils.SingleHole) {
					HoleUtils.SingleHole pos = (HoleUtils.SingleHole) hole0;
					HoleUtils.SingleHole pos0 = (HoleUtils.SingleHole) hole;
					if (pos.pos.equals(pos0.pos)) {
						return;
					}
				}
			}
		}

		AxisAlignedBB bb;

		if (hole instanceof HoleUtils.DoubleHole) {
			HoleUtils.DoubleHole pos = (HoleUtils.DoubleHole) hole;
			bb = new AxisAlignedBB(pos.pos);
			bb = bb.expand(pos.dir.getXOffset(), pos.dir.getYOffset(), pos.dir.getZOffset());
		} else {
			HoleUtils.SingleHole pos = (HoleUtils.SingleHole) hole;
			bb = new AxisAlignedBB(pos.pos);
		}

		switch (mode.getValue()) {
			case "Glow": {
				Tessellator.drawBBSlab(bb.offset(0, -1, 0), (float) height.getValue(), getColor(hole, false));
				break;
			}
			case "Fill": {
				Tessellator.drawBBFill(bb, getColor(hole, false));
				break;
			}
		}

		switch (outline.getValue()) {
			case "Claw": {
				Tessellator.drawBBClaw(bb.contract(0, 1, 0), (float) width.getValue(), (float) clawHeight.getValue(), getColor(hole, false));
				break;
			}
			case "Outline": {
				Tessellator.drawBBOutline(bb.contract(0, 1, 0), (float) width.getValue(), getColor(hole, false));
				break;
			}
		}
	}

	private void drawOwnHoleESP() {
		if (!own.getValue()) return;

		final HoleUtils.Hole hole = HoleUtils.getHole(PlayerUtils.getPlayerPosFloored(), (int) ownCheckHeight.getValue());

		if (hole != null) {
			AxisAlignedBB bb;

			if (hole instanceof HoleUtils.DoubleHole) {
				if (!doubleHoles.getValue()) return;
				HoleUtils.DoubleHole pos = (HoleUtils.DoubleHole) hole;
				bb = new AxisAlignedBB(pos.pos);
				bb = bb.expand(pos.dir.getXOffset(), pos.dir.getYOffset(), pos.dir.getZOffset());
			} else {
				HoleUtils.SingleHole pos = (HoleUtils.SingleHole) hole;
				bb = new AxisAlignedBB(pos.pos);
			}

			switch (ownMode.getValue()) {
				case "Glow": {
					Tessellator.drawBBSlab(bb.offset(0, -1, 0), (float) ownHeight.getValue(), getColor(hole, true));
					break;
				}
				case "Fill": {
					Tessellator.drawBBFill(bb, getColor(hole, true));
					break;
				}
			}

			switch (ownOutline.getValue()) {
				case "Claw": {
					Tessellator.drawBBClaw(bb.contract(0, 1, 0), (float) ownWidth.getValue(), (float) ownClawHeight.getValue(), getColor(hole, true));
					break;
				}
				case "Outline": {
					Tessellator.drawBBOutline(bb.contract(0, 1, 0), (float) ownWidth.getValue(), getColor(hole, true));
					break;
				}
			}
		}
	}

	private java.awt.Color getColor(HoleUtils.Hole hole, boolean own) {
		if (own)
			return hole.mat == HoleUtils.material.BEDROCK ? ownBedrockColor.getValue() : ownObsidianColor.getValue();
		return hole.mat == HoleUtils.material.BEDROCK ? bedrockColor.getValue() : obsidianColor.getValue();
	}
}
