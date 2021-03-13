package me.leon.trinity.events.events;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;

public class RotationEvent extends TrinityEvent {
    public float yaw, pitch;

    public RotationEvent(EventStage stage, float yaw, float pitch) {
        super(stage);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public RotationEvent(EventStage stage) {
        super(stage);
    }
}
