package me.leon.trinity.managers;

import me.leon.trinity.events.main.EventPacketSend;
import me.leon.trinity.events.main.RotationEvent;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.world.Rotation.Rotation;
import me.leon.trinity.utils.world.Rotation.RotationPriority;
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

public class RotationManager implements Listenable {
    public RotationManager() {
        MinecraftForge.EVENT_BUS.register(this);
        Trinity.dispatcher.subscribe(this);
    }

    public static LinkedBlockingQueue<Rotation> rotationQueue = new LinkedBlockingQueue<>();
    public static Rotation serverRotation = null;
    public static Rotation currentRotation = null;

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if(Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().world == null) return;

        rotationQueue.stream().sorted(Comparator.comparing(rotation -> rotation.priority.getPriority()));

        if (currentRotation != null) {
            currentRotation.cancel();
            currentRotation = null;
        }

        if (!rotationQueue.isEmpty()) {
            currentRotation = rotationQueue.poll();
            currentRotation.updateRotations();
        }
    }

    @EventHandler
    private final Listener<RotationEvent> rotationEventListener = new Listener<>(event ->{
        try {
            if (currentRotation != null && currentRotation.packet) {
                event.cancel();
                event.yaw = (currentRotation.yaw);
                event.pitch = (currentRotation.pitch);
            }
        } catch (Exception ignored) {

        }
    });

    @EventHandler
    private final Listener<EventPacketSend> onPacketSend = new Listener<>(event -> {
        if (currentRotation != null && !rotationQueue.isEmpty() && event.getPacket() instanceof CPacketPlayer) {
            if (((CPacketPlayer) event.getPacket()).rotating)
                serverRotation = new Rotation(((CPacketPlayer) event.getPacket()).yaw, ((CPacketPlayer) event.getPacket()).pitch, true, RotationPriority.Lowest);
        }
    });
}
