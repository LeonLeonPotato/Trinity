package me.leon.trinity.managers;

import me.leon.trinity.events.main.EventPacketSend;
import me.leon.trinity.events.main.SpoofEvent;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.world.Rotation.Rotation;
import me.leon.trinity.utils.world.Priority;
import me.leon.trinity.utils.world.location.Location;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;

public class SpoofingManager implements Listenable {
	public static LinkedBlockingQueue<Rotation> rotationQueue = new LinkedBlockingQueue<>();
	public static Rotation serverRotation = null;
	public static Rotation currentRotation = null;

	public static LinkedBlockingQueue<Location> locationQueue = new LinkedBlockingQueue<>();
	public static Location serverLocation = null;
	public static Location currentLocation = null;

	public static boolean cancel = false;

	public SpoofingManager() {
		MinecraftForge.EVENT_BUS.register(this);
		Trinity.dispatcher.subscribe(this);
	}

	@EventHandler
	private final Listener<SpoofEvent> rotationEventListener = new Listener<>(event -> {
		if(cancel) return;

		try {
			if (currentRotation != null && currentRotation.packet) {
				event.cancel();
				event.yaw = (currentRotation.yaw);
				event.pitch = (currentRotation.pitch);
				event.hasRotation = true;
			}
			if (currentLocation != null) {
				event.cancel();
				event.posX = currentLocation.getPosX();
				event.posY = currentLocation.getPosY();
				event.posZ = currentLocation.getPosZ();
				event.onGround = currentLocation.isOnGround();
				event.hasLocation = true;
			}
		} catch (Exception ignored) {

		}
	});

	@EventHandler
	private final Listener<EventPacketSend> onPacketSend = new Listener<>(event -> {
		if(cancel) return;

		if (currentRotation != null && !rotationQueue.isEmpty() && event.getPacket() instanceof CPacketPlayer) {
			if (((CPacketPlayer) event.getPacket()).rotating)
				serverRotation = new Rotation(((CPacketPlayer) event.getPacket()).yaw, ((CPacketPlayer) event.getPacket()).pitch, true, Priority.Lowest);
		}
		if (currentLocation != null && !locationQueue.isEmpty() && event.getPacket() instanceof CPacketPlayer) {
			if (((CPacketPlayer) event.getPacket()).moving)
				serverLocation = new Location(((CPacketPlayer) event.getPacket()).x, ((CPacketPlayer) event.getPacket()).y, ((CPacketPlayer) event.getPacket()).z, ((CPacketPlayer) event.getPacket()).onGround, Priority.Lowest);
		}
	});

	@SubscribeEvent
	public void onUpdate(TickEvent.ClientTickEvent event) {
		if(cancel) return;

		if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().world == null) return;

		rotationQueue.stream().sorted(Comparator.comparing(rotation -> rotation.priority.getPriority()));
		locationQueue.stream().sorted(Comparator.comparing(rotation -> rotation.getPriority().getPriority()));

		if (currentRotation != null) {
			currentRotation.cancel();
			currentRotation = null;
		}

		if (!rotationQueue.isEmpty()) {
			currentRotation = rotationQueue.poll();
			currentRotation.updateRotations();
		}

		if(currentLocation != null) {
			currentLocation = null;
		}

		if (!locationQueue.isEmpty()) {
			currentLocation = locationQueue.poll();
		}
	}
}
