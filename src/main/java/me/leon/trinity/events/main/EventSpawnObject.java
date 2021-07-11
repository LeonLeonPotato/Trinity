package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import net.minecraft.network.play.server.SPacketSpawnObject;

public class EventSpawnObject extends TrinityEvent {
    private final SPacketSpawnObject packet;

    public EventSpawnObject(EventStage stage, final SPacketSpawnObject packet) {
        super(stage);
        this.packet = packet;
    }

    public SPacketSpawnObject getPacket() {
        return packet;
    }
}
