package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;

public class EventStopHandActive extends TrinityEvent {
	public EventStopHandActive(EventStage stage) {
		super(stage);
	}
}
