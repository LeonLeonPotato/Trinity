package me.leon.trinity.hacks.player;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.EventClickBlock;
import me.leon.trinity.events.main.EventDamageBlock;
import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.entity.BlockUtils;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.rendering.Tessellator;
import me.leon.trinity.utils.world.RaytraceUtils;
import me.leon.trinity.utils.world.WorldUtils;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DecimalFormat;

public class SpeedMine extends Module {
	public static ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Normal", "Instant");
	public static ModeSetting haste = new ModeSetting("Haste", "None", "None", "I", "II", "Max");
	public static SliderSetting startPercent = new SliderSetting("Start Percent", 0, 0, 100, true);
	public static SliderSetting endPercent = new SliderSetting("End Percent", 0, 70, 100, true);
	public static SliderSetting range = new SliderSetting("Range", 0, 10, 20, true);

	public static BooleanSetting render = new BooleanSetting("Render", true, true);
	public static ModeSetting renderMode = new ModeSetting("Mode", render, "Both", "Both", "Outline", "Claw", "Fill");
	public static SliderSetting width = new SliderSetting("Width", render, 0, 0.5, 5, false);
	public static SliderSetting length = new SliderSetting("Length", render, 0, 0.2, 1, false);
	public static ColorSetting mainColor = new ColorSetting("Main", render, 119, 0, 255, 150, false);
	public static ColorSetting secondaryColor = new ColorSetting("Secondary", render, 119, 0, 255, 255, false);
	public static BooleanSetting renderDamage = new BooleanSetting("Damage", render, true);

	private boolean started = false;
	private BlockPos curPos = null;

	@Override
	public String getHudInfo() {
		return mode.getValue();
	}

	@EventHandler
	private final Listener<EventModeChange> modeChangeListener = new Listener<>(event -> {
		if (event.getStage() != EventStage.POST) return;
		if (event.getSet() != haste) return;
		if (haste.getValue().equalsIgnoreCase("None")) {
			removeEffect();
		} else {
			addEffect();
		}
	});

	@Override
	public void onUpdate() {
		if(curPos != null && mc.player.getDistanceSq(curPos) > range.getValue() * range.getValue()) {
			curPos = null;
		}
	}

	@EventHandler
	private final Listener<EventClickBlock> destroyBlockListener = new Listener<>(event -> started = false);

	@EventHandler
	private final Listener<EventDamageBlock> damageBlockListener = new Listener<>(event -> {
		if (BlockUtils.getBlockResistance(event.getPos()) == BlockUtils.BlockResistance.Unbreakable || BlockUtils.getBlockResistance(event.getPos()) == BlockUtils.BlockResistance.Blank)
			return;

		switch (mode.getValue()) {
			case "Normal": {
				if (!started) {
					mc.playerController.curBlockDamageMP = (float) (startPercent.getValue() / 100f);
					started = true;
				}

				if (mc.playerController.curBlockDamageMP >= (endPercent.getValue() / 100f)) {
					mc.playerController.curBlockDamageMP = 1f;
				}
				curPos = event.getPos();
				break;
			}

			case "Packet": {
				mc.player.swingArm(EnumHand.MAIN_HAND);
				mc.player.connection.sendPacket(new CPacketPlayerDigging(
						CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getDirection()));
				mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
						event.getPos(), event.getDirection()));
				event.cancel();
				if (curPos == null) {
					curPos = event.getPos();
				}
				break;
			}

			case "Instant": {
				mc.player.swingArm(EnumHand.MAIN_HAND);
				mc.player.connection.sendPacket(new CPacketPlayerDigging(
						CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getDirection()));
				mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
						event.getPos(), event.getDirection()));
				mc.playerController.onPlayerDestroyBlock(event.getPos());
				mc.world.setBlockToAir(event.getPos());
				curPos = event.getPos();
				break;
			}
		}
	});

	public SpeedMine() {
		super("SpeedMine", "Mine Blocks Faster", Category.PLAYER);
	}

	@Override
	public void onEnable() {
		addEffect();
	}

	@Override
	public void onDisable() {
		removeEffect();
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (curPos == null) return;
		if (!render.getValue()) return;

		if (BlockUtils.getBlockResistance(curPos) == BlockUtils.BlockResistance.Blank
				|| (mode.getValue().equalsIgnoreCase("Normal") && !mc.playerController.isHittingBlock)) {
			curPos = null;
			return;
		}

		AxisAlignedBB pos = new AxisAlignedBB(curPos);
		if (renderMode.getValue().equalsIgnoreCase("Outline")) {
			Tessellator.drawBBOutline(pos, (float) width.getValue(), mainColor.getValue());
		}
		if (renderMode.getValue().equalsIgnoreCase("Fill")) {
			Tessellator.drawBBFill(pos, mainColor.getValue());
		}
		if (renderMode.getValue().equalsIgnoreCase("Claw")) {
			Tessellator.drawBBClaw(pos, (float) width.getValue(), (float) length.getValue(), mainColor.getValue());
		}
		if (renderMode.getValue().equalsIgnoreCase("Slab")) {
			Tessellator.drawBBSlab(pos, (float) length.getValue(), mainColor.getValue());
		}
		if (renderMode.getValue().equalsIgnoreCase("Both")) {
			Tessellator.drawBBFill(pos, mainColor.getValue());
			Tessellator.drawBBOutline(pos, (float) width.getValue(), secondaryColor.getValue());
		}
		if (renderDamage.getValue()) {
			final DecimalFormat format = new DecimalFormat("##");
			final String string = format.format(mc.playerController.curBlockDamageMP * 100f);
			Tessellator.drawTextFromBlockWithBackground(curPos, string, mainColor.getValue().getRGB(), 1, secondaryColor.getValue(), true, (float) width.getValue());
		}
	}

	private void removeEffect() {
		if (mc.player.isPotionActive(MobEffects.HASTE)) {
			mc.player.removeActivePotionEffect(MobEffects.HASTE);
		}
	}

	private void addEffect() {
		if (haste.getValue().equalsIgnoreCase("I")) {
			mc.player.addPotionEffect(new PotionEffect(new PotionEffect(MobEffects.HASTE, 10000, 0, false, false)));
		}
		if (haste.getValue().equalsIgnoreCase("II")) {
			mc.player.addPotionEffect(new PotionEffect(new PotionEffect(MobEffects.HASTE, 10000, 1, false, false)));
		}
		if (haste.getValue().equalsIgnoreCase("Max")) {
			mc.player.addPotionEffect(new PotionEffect(new PotionEffect(MobEffects.HASTE, 10000, 100, false, false)));
		}
	}
}
