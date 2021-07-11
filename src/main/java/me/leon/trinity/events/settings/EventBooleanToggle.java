package me.leon.trinity.events.settings;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import me.leon.trinity.setting.rewrite.BooleanSetting;

public class EventBooleanToggle extends TrinityEvent {
	private final BooleanSetting set;
	private boolean mode;

	public EventBooleanToggle(EventStage stage, BooleanSetting set, boolean mode) {
		super(stage);
		this.set = set;
		this.mode = mode;
	}

	public boolean isMode() {
		return mode;
	}

	public void setMode(boolean mode) {
		this.mode = mode;
	}

	public BooleanSetting getSet() {
		return set;
	}
}
