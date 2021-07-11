package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;

public class LocationSpoofEvent extends TrinityEvent {
    public final SpoofEvent location;

    public LocationSpoofEvent(EventStage stage, SpoofEvent location) {
        super(stage);
        this.location = location;
    }
}
