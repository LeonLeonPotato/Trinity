package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;

public class BlockPushEvent extends TrinityEvent {

    public double var1;
    public double var2;
    public double var3;

    public BlockPushEvent(double var1, double var2, double var3,EventStage stage) {
        super(stage);
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
    }
}
