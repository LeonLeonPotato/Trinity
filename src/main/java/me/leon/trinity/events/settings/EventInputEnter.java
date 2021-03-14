package me.leon.trinity.events.settings;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import me.leon.trinity.setting.settings.StringInput;

public class EventInputEnter extends TrinityEvent {
    private final StringInput input;

    public EventInputEnter(EventStage stage, StringInput input) {
        super(stage);
        this.input = input;
    }

    public StringInput getSetting() {
        return input;
    }
}
