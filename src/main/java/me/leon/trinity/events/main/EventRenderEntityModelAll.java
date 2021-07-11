package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import net.minecraft.entity.Entity;

public class EventRenderEntityModelAll extends TrinityEvent {
	private final Entity entity;

	public EventRenderEntityModelAll(EventStage stage, Entity entity) {
		super(stage);
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}
}
