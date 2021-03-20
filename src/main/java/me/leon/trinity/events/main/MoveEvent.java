package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import net.minecraft.entity.MoverType;

public class MoveEvent extends TrinityEvent {
    public double x, y, z;
    public MoverType type;

    public MoveEvent(MoverType type, double x, double y, double z, EventStage stage) {
        super(stage);
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
