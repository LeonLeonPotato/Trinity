package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EventDamageBlock extends TrinityEvent {
	private final BlockPos pos;
	private final EnumFacing direction;

	public EventDamageBlock(EventStage stage, BlockPos pos, EnumFacing dir) {
		super(stage);
		this.pos = pos;
		this.direction = dir;
	}

	public BlockPos getPos() {
		return pos;
	}

	public EnumFacing getDirection() {
		return direction;
	}
}
