package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import net.minecraft.network.Packet;

public class EventPacketSend extends TrinityEvent {
	private final Packet<?> packet;

	public EventPacketSend(EventStage stage, Packet<?> packet) {
		super(stage);
		this.packet = packet;
	}

	public Packet<?> getPacket() {
		return packet;
	}
}
