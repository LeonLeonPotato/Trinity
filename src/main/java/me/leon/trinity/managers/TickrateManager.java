package me.leon.trinity.managers;

import io.netty.util.internal.MathUtil;
import me.leon.trinity.events.main.EventPacketRecieve;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.math.MathUtils;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TickrateManager implements Listenable {
    public long prevTime;
    public static float[] TPS = new float[20];
    public int currentTick;

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

        return (float) MathUtils.roundAvoid(MathUtils.clamp((tickRate / tickCount), 0.0f, 20.0f), 2);
    }

    @EventHandler
    private final Listener<EventPacketRecieve> onPacketReceive = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            if (prevTime != -1) {
                TPS[currentTick % TPS.length] = MathHelper.clamp((20.0f / ((float) (System.currentTimeMillis() - prevTime) / 1000.0f)), 0.0f, 20.0f);
                currentTick++;
            }

            prevTime = System.currentTimeMillis();
        }
    });
}
