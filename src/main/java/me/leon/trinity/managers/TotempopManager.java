package me.leon.trinity.managers;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.EventPacketRecieve;
import me.leon.trinity.events.main.EventTotemPop;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.Util;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class TotempopManager implements Util, Listenable {
    public static ConcurrentHashMap<EntityLivingBase, Integer> totemMap;

    public TotempopManager() {
        totemMap = new ConcurrentHashMap<>();
        Trinity.dispatcher.subscribe(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static void update() {
        for(EntityLivingBase entity : totemMap.keySet()) {
            if(!mc.world.loadedEntityList.contains(entity)) {
                totemMap.remove(entity);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null || mc.world == null) {
            totemMap.clear();
            return;
        }
        update();
    }

    @EventHandler
    private final Listener<EventPacketRecieve> packetRecieveListener = new Listener<>(event -> {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            EntityLivingBase entity = (EntityLivingBase) packet.getEntity(mc.world);

            if (packet.getOpCode() == 35) {
                if(totemMap.containsKey(entity)) {
                    int times = totemMap.get(entity) + 1;
                    Trinity.dispatcher.post(new EventTotemPop(EventStage.PRE, entity, times));
                    totemMap.remove(entity);
                    totemMap.put(entity, times);
                } else {
                    Trinity.dispatcher.post(new EventTotemPop(EventStage.PRE, entity, 1));
                    totemMap.put(entity, 1);
                }
            }
        }
    });

    public static int getPops(EntityLivingBase entity) {
        //update();
        if(totemMap.containsKey(entity)) {
            return totemMap.get(entity);
        }
        return 0;
    }

    public static int getPops(String name) {
        //update();
        boolean flag = false;
        EntityLivingBase e = null;
        for(Entity entity : mc.world.loadedEntityList) {
            if(entity instanceof EntityLivingBase) {
                if(entity.getName().equals(name)) {
                    flag = true;
                    e = (EntityLivingBase) entity;
                    break;
                }
            }
        }
        if(flag) {
            if(totemMap.containsKey(e)) {
                return totemMap.get(e);
            }
        }
        return 0;
    }
}
