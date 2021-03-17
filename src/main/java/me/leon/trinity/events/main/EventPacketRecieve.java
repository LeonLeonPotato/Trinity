package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import net.minecraft.network.Packet;

public class EventPacketRecieve extends TrinityEvent {
    private final Packet<?> packet;

    public EventPacketRecieve(EventStage stage, Packet<?> packet) {
        super(stage);
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
