package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;

public class SpoofEvent extends TrinityEvent {
	public float yaw, pitch;
	public double posX, posY, posZ;
	public boolean hasLocation, hasRotation, onGround;

	public SpoofEvent(EventStage stage) {
		super(stage);
	}
}
