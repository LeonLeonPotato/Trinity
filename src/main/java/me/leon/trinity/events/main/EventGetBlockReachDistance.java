package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;

public class EventGetBlockReachDistance extends TrinityEvent {
	public double reach;

	public EventGetBlockReachDistance(EventStage stage, double reach) {
		super(stage);
		this.reach = reach;
	}
}
