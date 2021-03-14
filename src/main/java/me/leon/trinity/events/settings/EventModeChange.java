package me.leon.trinity.events.settings;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import me.leon.trinity.setting.Setting;

public class EventModeChange extends TrinityEvent {
    private final Setting set;

    public EventModeChange(EventStage stage, Setting set) {
        super(stage);
        this.set = set;
    }

    public Setting getSet() {
        return set;
    }
}
