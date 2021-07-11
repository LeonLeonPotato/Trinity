package me.leon.trinity.hacks.combat;

import me.leon.trinity.events.main.EventPacketRecieve;
import me.leon.trinity.events.main.EventPacketSend;
import me.leon.trinity.events.main.EventSpawnObject;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.combat.autocrystal.Breaker;
import me.leon.trinity.hacks.combat.autocrystal.Crystal;
import me.leon.trinity.hacks.combat.autocrystal.CrystalPosition;
import me.leon.trinity.hacks.combat.autocrystal.Placer;
import me.leon.trinity.setting.rewrite.*;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.rendering.Tessellator;
import me.leon.trinity.utils.world.*;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;
import java.util.HashSet;

public class AutoCrystal extends Module {

	public AutoCrystal() {
		super("AutoCrystal", "Nagasaki", Category.COMBAT);
		placedCrystals = new HashSet<>();
		targeter = new Targeting();
	}

	public static final Timer breakTimer = new Timer();
	public static final Timer placeTimer = new Timer();
	public static BooleanSetting main = new BooleanSetting("Main", true, false);
	public static SliderSetting iterations = new SliderSetting("Iterations", main, 1, 1, 3, true);
	public static ModeSetting noSuicide = new ModeSetting("NoSuicide", main, "Both", "Place", "Destroy", "None", "Both");
	public static SliderSetting pauseHealth = new SliderSetting("Pause Health", main, 0, 6, 20, true);

	public static BooleanSetting Place = new BooleanSetting("Place", true, true);
	public static ModeSetting placeMode = new ModeSetting("Place Mode", Place, "Closest", "Closest", "Damage");
	public static ModeSetting heuristic = new ModeSetting("Preference", Place, "Target", "Target", "Self", "Subtract", "Atomic");
	public static SliderSetting placeDelay = new SliderSetting("Place Delay", Place, 0, 2, 20, true);
	public static SliderSetting placeRange = new SliderSetting("Range", Place, 0.5, 5.0, 6, false);
	public static SliderSetting placeRangeWalls = new SliderSetting("Walls Range", Place, 0, 3.5, 6, false);
	public static SliderSetting minTargetDamagePlace = new SliderSetting("Min Target Damage", Place, 0, 6, 20, false);
	public static SliderSetting maxSelfDamagePlace = new SliderSetting("Max Self Damage", Place, 0, 8, 20, false);
	public static ModeSetting rayTracePlaceMode = new ModeSetting("RayTrace Mode", Place, "Simple", "Leon", "Simple", "Offset");
	public static SliderSetting offsetPlace = new SliderSetting("Offset", Place, 0, 1, 3, false);
	public static BooleanSetting packetPlace = new BooleanSetting("Packet Place", Place, true);
	public static ModeSetting swingArmPlace = new ModeSetting("Swing Arm", Place, "Offhand", "Mainhand", "Offhand", "Both", "None");
	public static BooleanSetting packetSwingPlace = new BooleanSetting("Packet Swing", Place, false);
	public static ModeSetting switchPlace = new ModeSetting("Switch", Place, "None", "None", "Packet", "Normal");
	public static BooleanSetting multiPlace = new BooleanSetting("Multiplace", Place, false);
	public static BooleanSetting bounds = new BooleanSetting("Bounds", Place, true);
	public static BooleanSetting extraCalc = new BooleanSetting("Extra Calc", Place, true);
	public static BooleanSetting predict = new BooleanSetting("Predict", Place, true);
	public static SliderSetting predictTicks = new SliderSetting("Predict Ticks", Place, 0, 3, 10, true);
	public static BooleanSetting selfPredict = new BooleanSetting("Self Predict", Place, true);
	public static SliderSetting selfPredictTicks = new SliderSetting("Self Ticks", Place, 0, 3, 10, true);
	public static BooleanSetting limit = new BooleanSetting("Limit", Place, false);
	public static BooleanSetting doubleCheck = new BooleanSetting("DoubleCheck", Place, true);
	public static BooleanSetting antiTotem = new BooleanSetting("Anti-Totem", Place, true);
	public static SliderSetting antiTotemTime = new SliderSetting("TotemTime", Place, 0, 3, 10, true);

	public static BooleanSetting Break = new BooleanSetting("Break", true, true);
	public static ModeSetting breakMode = new ModeSetting("Break Mode", Break, "Smart", "Smart", "All", "Only Own");
	public static SliderSetting breakDelay = new SliderSetting("Break Delay", Break, 0, 4, 20, true);
	public static SliderSetting breakRange = new SliderSetting("Range", Break, 0.5, 5.0, 6, false);
	public static SliderSetting breakRangeWalls = new SliderSetting("Walls Range", Break, 0, 3.0, 6, false);
	public static SliderSetting minTargetDamageBreak = new SliderSetting("Min Target Damage", Break, 0, 6, 20, false);
	public static SliderSetting maxSelfDamageBreak = new SliderSetting("Max Self Damage", Break, 0, 8, 20, false);
	public static BooleanSetting packetBreak = new BooleanSetting("Packet Break", Break, true);
	public static ModeSetting rayTraceBreakMode = new ModeSetting("RayTrace Mode", Break, "Simple", "Leon", "Simple", "Offset");
	public static ModeSetting swingArmBreak = new ModeSetting("Swing Arm", Break, "Mainhand", "Mainhand", "Offhand", "Both", "None");
	public static BooleanSetting packetSwingBreak = new BooleanSetting("Packet Swing", Break, false);
	public static SliderSetting offsetBreak = new SliderSetting("Offset", Break, 0, 1, 3, false);
	public static SliderSetting breakAttempts = new SliderSetting("Break Attempts", Break, 1, 3, 10, true);
	public static ModeSetting syncMode = new ModeSetting("Sync", Break, "None", "Sound", "Instant", "None");

	public static BooleanSetting rotate = new BooleanSetting("Rotate", true, true);
	public static BooleanSetting placeRotate = new BooleanSetting("Place", rotate, false);
	public static BooleanSetting breakRotate = new BooleanSetting("Break", rotate, false);
	public static BooleanSetting clientRotate = new BooleanSetting("Client", rotate, false);

	public static BooleanSetting logic = new BooleanSetting("Logic", true, false);
	public static ModeSetting logicMode = new ModeSetting("Logic Mode", logic, "BreakPlace", "PlaceBreak", "BreakPlace");
	public static ModeSetting version = new ModeSetting("Version", logic, "1.12.2", "1.13+", "1.12.2");

	public static BooleanSetting timing = new BooleanSetting("Timing", true, false);
	public static ModeSetting timingMode = new ModeSetting("Mode", timing, "Tick", "Tick", "Fast", "Sequential");

	public static BooleanSetting facePlace = new BooleanSetting("FacePlace", true, true);
	public static KeybindSetting forceBind = new KeybindSetting("Force Faceplace", facePlace, Keyboard.KEY_O);
	public static BooleanSetting armorBreaker = new BooleanSetting("Armor Breaker", facePlace, true);
	public static SliderSetting armorBreakerScale = new SliderSetting("Armor Scale", facePlace, 0, 30, 100, true);
	public static SliderSetting facePlaceMinHealth = new SliderSetting("Min Health", facePlace, 0, 8, 36, true);
	public static SliderSetting facePlaceMinDamage = new SliderSetting("Min Damage", facePlace, 0, 2.4, 8, false);

	public static BooleanSetting rendering = new BooleanSetting("Rendering", true, true);
	public static ModeSetting renderMode = new ModeSetting("RenderMode", rendering, "Claw", "Claw", "Outline", "Fill", "Both", "Slab");
	public static SliderSetting renderWidth = new SliderSetting("Width", rendering, 0.1, 1.5, 3, false);
	public static SliderSetting renderHeight = new SliderSetting("Height", rendering, -1, 0.3, 1, false);
	public static ColorSetting outlineColor = new ColorSetting("OutLine Color", rendering, 0, 255, 255, 255, false);
	public static ColorSetting fillColor = new ColorSetting("Fill Color", rendering, 0, 255, 255, 255, false);
	public static BooleanSetting renderDamage = new BooleanSetting("Render Damage", rendering, true);
	public static ColorSetting renderDamageColor = new ColorSetting("Render Color", rendering, 255, 255, 255, 255, false);

	public static BooleanSetting multithreading = new BooleanSetting("Multithreading", true, false);
	public static BooleanSetting threadedPlace = new BooleanSetting("Place", multithreading, true);
	public static BooleanSetting threadedBreak = new BooleanSetting("Break", multithreading, true);
	public static BooleanSetting threadedTargeting = new BooleanSetting("Targeting", multithreading, true);

	public static BooleanSetting targeting = new BooleanSetting("Targeting", true, false);
	public static ModeSetting targetingMode = new ModeSetting("Mode", targeting, "Closest", "Closest", "Lowest Health", "Highest Health");
	public static SliderSetting targetRange = new SliderSetting("Range", targeting, 0.5, 5, 15, false);
	public static BooleanSetting players = new BooleanSetting("Players", targeting, true);
	public static BooleanSetting friends = new BooleanSetting("Friends", targeting, true);
	public static BooleanSetting neutral = new BooleanSetting("Neutral", targeting, true);
	public static BooleanSetting passive = new BooleanSetting("Passive", targeting, true);
	public static BooleanSetting hostile = new BooleanSetting("Hostile", targeting, true);

	public static boolean pause = false;
	public static EntityLivingBase target = null;
	public static HashSet<CrystalPosition> placedCrystals;
	public static CrystalPosition curPosPlace = null;
	public static Crystal curBreakCrystal = null;
	
	public static Placer placer = new Placer();
	public static Breaker breaker = new Breaker();
	public static Targeting targeter;

	@Override
	public void onUpdate() {
		if (nullCheck()) return;
		if (timingMode.getValue().equalsIgnoreCase("Fast")) return;
		if (pause) return;

		if(threadedTargeting.getValue()) {
			new Targeting().start();
		} else targeter.run();

		autoCrystal();
	}

	@SubscribeEvent
	public void onFastTick(TickEvent event) {
		try {
			if (nullCheck()) return;
			if (!timingMode.getValue().equalsIgnoreCase("Fast")) return;
			if (pause) return;

			if(threadedTargeting.getValue()) {
				Targeting t = new Targeting();
				t.start();
			} else targeter.run();

			autoCrystal();

		} catch (Exception ignored) { }
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (curPosPlace == null) return;
		if (!rendering.getValue()) return;
		final AxisAlignedBB pos = new AxisAlignedBB(curPosPlace.getBase());
		switch (renderMode.getValue()) {
			case "Both": {
				Tessellator.drawBBOutline(pos, (float) renderWidth.getValue(), outlineColor.getValue());
				Tessellator.drawBBFill(pos, fillColor.getValue());
				break;
			}
			case "Outline": {
				Tessellator.drawBBOutline(pos, (float) renderWidth.getValue(), outlineColor.getValue());
				break;
			}
			case "Fill": {
				Tessellator.drawBBFill(pos, fillColor.getValue());
				break;
			}
			case "Claw": {
				Tessellator.drawBBClaw(pos, (float) renderWidth.getValue(), (float) renderHeight.getValue(), outlineColor.getValue());
				break;
			}
			case "Slab": {
				Tessellator.drawBBSlab(pos, (float) renderHeight.getValue(), outlineColor.getValue());
				break;
			}
		}
		if (renderDamage.getValue()) {
			DecimalFormat format = new DecimalFormat("##.#");
			String formatted = format.format(curPosPlace.getTargetDamage());
			Tessellator.drawTextFromBlock(curPosPlace.getBase(), formatted, renderDamageColor.getValue().getRGB(), 1.0f);
		}
	}

	@Override
	public String getHudInfo() {
		return target == null ? null : target.getName();
	}

	private void autoCrystal() {
		for (int a = 0; a < iterations.getValue(); a++) {
			placedCrystals.removeIf(id -> WorldUtils.getRange(new Vec3d(id.getBase().x + 0.5, id.getBase().y + 0.5, id.getBase().z + 0.5)) > 10);

			if(!timingMode.getValue().equalsIgnoreCase("Sequential")) {
				switch (logicMode.getValue()) {
					case "PlaceBreak": {
						placer.placeCrystal();
						breaker.breakCrystals();
						break;
					}
					case "BreakPlace": {
						breaker.breakCrystals();
						placer.placeCrystal();
						break;
					}
				}
			} else {
				placer.placeCrystal();
			}
		}
	}

	@EventHandler
	private final Listener<EventPacketSend> onPacketSend = new Listener<>(event -> {
		final Packet<?> rawPacket = event.getPacket();
		if (rawPacket instanceof CPacketUseEntity) {
			breaker.instaSync((CPacketUseEntity) rawPacket);
		}
	});

	@EventHandler
	private final Listener<EventPacketRecieve> onPacketReceive = new Listener<>(event -> {
		final Packet<?> rawPacket = event.getPacket();
		if(rawPacket instanceof SPacketSoundEffect) {
			breaker.soundSync((SPacketSoundEffect) event.getPacket());
		}
	});

	// after object spawn... this gives me way more options as the crystal is added to the entity list
	@EventHandler
	private final Listener<EventSpawnObject> onSpawnObject = new Listener<>(event -> {
		breaker.sequential(event.getPacket());
	});

	private static class Targeting extends Thread {
		@Override
		public void run() {
			AutoCrystal.target = EntityUtils.getTarget(players.getValue(), neutral.getValue(), friends.getValue(), hostile.getValue(), passive.getValue(), targetRange.getValue(), EntityUtils.toMode(targetingMode.getValue()));
		}
	}
}
