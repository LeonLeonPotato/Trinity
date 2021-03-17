package me.leon.trinity.events;

import me.zero.alpine.fork.event.type.ICancellable;

public class TrinityEvent implements ICancellable {
    private final EventStage stage;
    private boolean canceled;

    public TrinityEvent(EventStage stage) {
        this.stage = stage;
    }

    public EventStage getStage() {
        return stage;
    }

    @Override
    public void cancel() {
        this.canceled = true;
    }

    public void resume() {
        this.canceled = false;
    }

    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
