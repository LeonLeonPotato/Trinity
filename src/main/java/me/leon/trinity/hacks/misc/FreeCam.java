package me.leon.trinity.hacks.misc;

import com.mojang.authlib.GameProfile;
import me.leon.trinity.events.main.BlockPushEvent;
import me.leon.trinity.events.main.EventPacketSend;
import me.leon.trinity.events.main.EventSetOpaqueCube;
import me.leon.trinity.events.main.MoveEvent;
import me.leon.trinity.events.settings.EventBooleanToggle;
import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.hacks.render.FreeLook;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.entity.MotionUtils;
import me.leon.trinity.utils.math.MathUtils;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

/**
 * @author leon
 * <p>
 * pain
 * i feel pain
 */
public class FreeCam extends Module {

	public static final ModeSetting mode = new ModeSetting("Mode", "Camera", "Camera", "Normal");
	private static final SliderSetting speed = new SliderSetting("Speed", 0, 10, 20, false);

	@Override
	public String getHudInfo() {
		return mode.getValue();
	}

	@EventHandler
	private final Listener<EventPacketSend> packetListener = new Listener<>(event -> {
		if ((event.getPacket() instanceof CPacketPlayer
				|| event.getPacket() instanceof CPacketUseEntity
				|| event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock)
				&& mode.getValue().equalsIgnoreCase("Normal")) {
			event.cancel();
		}
	});
	@EventHandler
	private final Listener<BlockPushEvent> pushListener = new Listener<>(event -> {
		if (mode.getValue().equalsIgnoreCase("Normal")) {
			event.cancel();
		}
	});
	@EventHandler
	private final Listener<EventSetOpaqueCube> cubeListener = new Listener<>(event -> {
		event.cancel();
	});
	@EventHandler
	private final Listener<MoveEvent> moveEventListener = new Listener<>(event -> {
		if (mode.getValue().equalsIgnoreCase("Normal")) {

			MotionUtils.doStrafe((float) (speed.getValue() / 10f));

			event.y = (getY() * speed.getValue());
			mc.player.noClip = true;
			event.cancel();
		}
	});
	@EventHandler
	private final Listener<EventModeChange> toggleListener = new Listener<>(event -> {
		if (event.getSet() == mode) {
			this.toggle();
			this.toggle();
		}
	});
	private EntityPlayerCamera other;
	private final me.leon.trinity.utils.world.Timer timer = new me.leon.trinity.utils.world.Timer();

	public FreeCam() {
		super("FreeCam", "Out-of-body experience", Category.MISC);
	}

	@Override
	public void onUpdate() {
		if (nullCheck()) {
			this.setEnabled(false);
		}
	}

	@Override
	public void onDisable() {
		if (nullCheck()) return;
		if (other == null) return;
		mc.world.removeEntity(other);
		if (mode.getValue().equalsIgnoreCase("Normal")) {
			mc.player.setPositionAndRotation(other.posX, other.posY, other.posZ, other.rotationYaw, other.rotationPitch);
		}
		setRender(mc.player);
	}

	@SubscribeEvent
	public void onRender(TickEvent.RenderTickEvent event) {
		if (nullCheck()) {
			this.setEnabled(false);
			return;
		}
		if (!mode.getValue().equalsIgnoreCase("Camera")) return;

		if (mc.currentScreen != null) return;
		if (other == null) return;

		double cameraYaw = other.rotationYaw;
		double cameraPitch = other.rotationPitch;

		float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		float f1 = f * f * f * 8.0F;

		double dx = Mouse.getDX() * f1 * 0.15D;
		double dy = Mouse.getDY() * f1 * 0.15D;

		cameraYaw = cameraYaw + dx;
		cameraPitch = cameraPitch - dy;

		cameraPitch = MathHelper.clamp(cameraPitch, -90.0F, 90.0F);

		other.rotationPitch = (float) cameraPitch;
		other.rotationYaw = (float) cameraYaw;

		float moveForward = getMoveForward();
		float moveStrafe = getMoveStrafe();

		final Vec3d vec = MathUtils.interpolateStrafe(other.rotationYaw, timer.getDelta(), 1000, (float) (speed.getValue() * 5), moveForward, moveStrafe, (float) -getY()); // interpolate so faster pcs don't go faster
		timer.reset();

		double x = vec.x;
		double y = vec.y;
		double z = vec.z;

		other.noClip = true;
		other.move(MoverType.PLAYER, x, y, z);

		other.inventory.copyInventory(mc.player.inventory);
	}

	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		event.setCanceled(true);
	}

	@Override
	public void onEnable() {
		if (nullCheck()) return;
		ModuleManager.getMod(FreeLook.class).setEnabled(false);

		this.other = new EntityPlayerCamera(mc.world, mc.player.gameProfile);
		other.copyLocationAndAnglesFrom(mc.player);
		other.inventory.copyInventory(mc.player.inventory);
		other.noClip = true;
		other.inventory.copyInventory(mc.player.inventory);
		other.setLocationAndAngles(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
		other.setRotationYawHead(mc.player.rotationYaw);
		mc.world.addEntityToWorld(-1234, other);
		timer.reset();
		if (mode.getValue().equalsIgnoreCase("Camera"))
			setRender(other);
	}

	private float getMoveForward() {
		if (mc.gameSettings.keyBindForward.isKeyDown() && mc.gameSettings.keyBindBack.isKeyDown()) {
			return 0.0f;
		}
		if (mc.gameSettings.keyBindBack.isKeyDown()) {
			return -1.0f;
		}
		if (mc.gameSettings.keyBindForward.isKeyDown()) {
			return 1.0f;
		}
		return 0.0f;
	}

	private float getMoveStrafe() {
		if (mc.gameSettings.keyBindLeft.isKeyDown() && mc.gameSettings.keyBindRight.isKeyDown()) {
			return 0.0f;
		}
		if (mc.gameSettings.keyBindRight.isKeyDown()) {
			return -1.0f;
		}
		if (mc.gameSettings.keyBindLeft.isKeyDown()) {
			return 1.0f;
		}
		return 0.0f;
	}

	private double getY() {
		if (mc.gameSettings.keyBindSneak.isKeyDown() && mc.gameSettings.keyBindJump.isKeyDown()) {
			return 0.0f;
		}
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			return 1.0f;
		}
		if (mc.gameSettings.keyBindSneak.isKeyDown()) {
			return -1.0f;
		}
		return 0.0;
	}

	private void setRender(Entity entity) {
		mc.renderViewEntity = entity;
		if (ModuleManager.getMod(ClickGUI.class).isEnabled()) {
			ClickGUI.loadShader();
		}
	}

	public boolean check() {
		return this.isEnabled() && mode.getValue().equalsIgnoreCase("Camera") && !nullCheck();
	}

	private static class EntityPlayerCamera extends EntityOtherPlayerMP {
		public EntityPlayerCamera(World worldIn, GameProfile gameProfileIn) {
			super(worldIn, gameProfileIn);
		}

		@Override
		public boolean isInvisible() {
			return true;
		}

		@Override
		public boolean isInvisibleToPlayer(@NotNull EntityPlayer player) {
			if (mode.getValue().equalsIgnoreCase("Camera")) {
				return true;
			}
			if (mode.getValue().equalsIgnoreCase("Normal")) {
				return player != mc.player;
			}
			return false;
		}

		@Override
		public boolean isSpectator() {
			return false;
		}
	}
}
