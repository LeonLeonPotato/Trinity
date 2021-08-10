package me.leon.trinity.events.settings;

import me.leon.trinity.config.rewrite.Preset;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;

public class EventLoadPreset extends TrinityEvent {
	private final Preset obj;

	public EventLoadPreset(EventStage stage, Preset obj) {
		super(stage);
		this.obj = obj;
	}

	public Preset getObj() {
		return obj;
	}
}
