package me.leon.trinity.hacks.render;

import com.mojang.authlib.GameProfile;
import me.leon.trinity.events.main.EventSetOpaqueCube;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class FreeLook extends Module {

	public static SliderSetting range = new SliderSetting("Range", 0, 5, 7, false);
	private static float cameraYaw;
	private static float cameraPitch;
	private static EntityPlayerCamera camera;
	private int flag = -1;
	@EventHandler
	private final Listener<EventSetOpaqueCube> cubeListener = new Listener<>(event -> {
		event.cancel();
	});

	public FreeLook() {
		super("FreeLook", "LUNAR MODE", Category.RENDER);
	}

	private void updateCamera() {
		if (nullCheck() || camera == null) {
			return;
		}
		if (mc.inGameHasFocus) {
			float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f1 = f * f * f * 8.0F;

			double dx = Mouse.getDX() * f1 * 0.15D;
			double dy = Mouse.getDY() * f1 * 0.15D;

			cameraYaw += dx;
			cameraPitch += dy * -1;

			cameraPitch = MathHelper.clamp(cameraPitch, -90.0F, 90.0F);
			cameraYaw = MathHelper.clamp(cameraYaw, (cameraYaw + -100.0F), (cameraYaw + 100.0F));

			camera.rotationPitch = cameraPitch;
			camera.rotationYaw = cameraYaw;
		}
	}

	private void updateCamera2() {
		double x = (mc.player.posX + (range.getValue() * Math.cos(Math.toRadians(cameraPitch))));
		double z = (mc.player.posY + (range.getValue() * Math.sin(Math.toRadians(cameraPitch))));
		double dist = Math.abs(mc.player.posX - x);

		double x1 = (mc.player.posX + (dist * Math.cos(Math.toRadians(cameraYaw - 90))));
		double z1 = (mc.player.posZ + (dist * Math.sin(Math.toRadians(cameraYaw - 90))));

		setPosition(camera, x1, z, z1);

		camera.inventory.copyInventory(mc.player.inventory);
	}

	@SubscribeEvent
	public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
		this.setEnabled(false);
	}

	@SubscribeEvent
	public void onRender(TickEvent.RenderTickEvent event) {
		if (nullCheck() || camera == null) {
			return;
		}
		updateCamera();
		updateCamera2();
	}

	@SubscribeEvent
	public void onRender(RenderHandEvent event) {
		event.setCanceled(true);
	}

	@Override
	public void onEnable() {
		if (nullCheck()) return;
		cameraPitch = mc.player.rotationPitch;
		cameraYaw = mc.player.rotationYaw;
		camera = new EntityPlayerCamera(mc.player.gameProfile);
		if(mc.gameSettings.thirdPersonView != 0) flag = mc.gameSettings.thirdPersonView;
		EntityPlayer player = mc.player;

		if (player != null) {
			camera.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
			camera.setRotationYawHead(player.rotationYaw);
		}
		mc.world.addEntityToWorld(-9283, camera);
		mc.renderViewEntity = camera;
	}

	@Override
	public void onUpdate() {
		if (nullCheck()) {
			this.setEnabled(false);
		}
	}

	@Override
	public void onDisable() {
		if (nullCheck() || camera == null) {
			return;
		}
		mc.renderViewEntity = mc.player;
		mc.world.removeEntity(camera);
		if(flag != -1) {
			mc.gameSettings.thirdPersonView = flag;
			flag = -1;
		}
	}

	private void setPosition(Entity en, double x, double y, double z) {
		setPosition(en, x, y, z, en.rotationYaw, en.rotationPitch);
	}

	private void setPosition(Entity en, double x, double y, double z, float yaw, float cameraPitch) {
		en.prevPosX = en.posX = x;
		en.prevPosY = en.posY = y;
		en.prevPosZ = en.posZ = z;
		en.rotationYaw = yaw;
		en.rotationPitch = cameraPitch;
	}

	private static class EntityPlayerCamera extends EntityOtherPlayerMP {
		public EntityPlayerCamera(GameProfile gameProfileIn) {
			super(mc.world, gameProfileIn);
		}

		@Override
		public boolean isInvisible() {
			return true;
		}

		@Override
		public boolean isInvisibleToPlayer(EntityPlayer player) {
			return true;
		}

		@Override
		public boolean isSpectator() {
			return false;
		}
	}
}