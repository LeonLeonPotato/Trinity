package me.leon.trinity.utils.world;

public enum Priority {
	Highest(200),
	High(100),
	Normal(0),
	Low(-100),
	Lowest(-200);

	int priority;

	Priority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}
}
