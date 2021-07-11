package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import net.minecraft.util.math.BlockPos;

public class EventDestroyBlock extends TrinityEvent {
	private final BlockPos pos;

	public EventDestroyBlock(EventStage stage, BlockPos pos) {
		super(stage);
		this.pos = pos;
	}

	public BlockPos getPos() {
		return pos;
	}
}
