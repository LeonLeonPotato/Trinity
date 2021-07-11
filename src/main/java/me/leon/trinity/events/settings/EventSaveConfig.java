package me.leon.trinity.events.settings;

import me.leon.trinity.config.rewrite.PresetObj;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;

public class EventSaveConfig extends TrinityEvent {
	private final PresetObj obj;

	public EventSaveConfig(EventStage stage, PresetObj obj) {
		super(stage);
		this.obj = obj;
	}

	public PresetObj getObj() {
		return obj;
	}
}
