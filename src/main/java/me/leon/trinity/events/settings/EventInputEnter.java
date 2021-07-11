package me.leon.trinity.events.settings;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import me.leon.trinity.setting.rewrite.Setting;

public class EventInputEnter extends TrinityEvent {
	private final Setting input;

	public EventInputEnter(EventStage stage, Setting input) {
		super(stage);
		this.input = input;
	}

	public Setting getSetting() {
		return input;
	}
}
