package me.leon.trinity.managers;

import me.leon.trinity.events.main.EventPacketRecieve;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.math.MathUtils;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.server.SPacketTimeUpdate;

public class TickrateManager implements Listenable {
	public static float[] TPS = new float[20];
	public long prevTime;
	public int currentTick;
	@EventHandler
	private final Listener<EventPacketRecieve> onPacketReceive = new Listener<>(event -> {
		if (event.getPacket() instanceof SPacketTimeUpdate) {
			if (prevTime != -1) {
				TPS[currentTick % TPS.length] = (float) MathUtils.clamp(0.0d, 20.0d, (20.0f / ((float) (System.currentTimeMillis() - prevTime) / 1000.0f)));
				currentTick++;
			}

			prevTime = System.currentTimeMillis();
		}
	});

	public TickrateManager() {
		prevTime = -1;

		for (int i = 0, len = TPS.length; i < len; i++) {
			TPS[i] = 0.0f;
		}

		Trinity.dispatcher.subscribe(this);
	}

	public static float getTPS() {
		int tickCount = 0;
		float tickRate = 0.0f;

		for (float tick : TPS) {
			if (tick > 0.0f) {
				tickRate += tick;
				tickCount++;
			}
		}

		return (float) MathUtils.roundAvoid(MathUtils.clamp(0, 20, (tickRate / tickCount)), 2);
	}
}
