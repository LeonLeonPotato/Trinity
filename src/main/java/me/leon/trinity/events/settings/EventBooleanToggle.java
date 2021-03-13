package me.leon.trinity.events.settings;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import me.leon.trinity.setting.Setting;

public class EventBooleanToggle extends TrinityEvent {
    private final Setting set;
    private boolean mode;

    public EventBooleanToggle(EventStage stage, Setting set, boolean mode) {
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

    public Setting getSet() {
        return set;
    }
}
