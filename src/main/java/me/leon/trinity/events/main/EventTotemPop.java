package me.leon.trinity.events.main;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import me.leon.trinity.managers.NotificationManager;
import me.leon.trinity.notification.NotifType;
import me.leon.trinity.notification.Notification;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.EntityLivingBase;

public class EventTotemPop extends TrinityEvent {
    private final EntityLivingBase entity;
    private final int times;

    public EventTotemPop(EventStage stage, EntityLivingBase entity, int times) {
        super(stage);
        this.entity = entity;
        this.times = times;
    }

    @EventHandler
    private final Listener<EventTotemPop> packetRecieveListener = new Listener<>(event -> {
        NotificationManager.add(new Notification("Totem Pop", event.entity.getName() + " Popped a totem", NotifType.INFO));
    });

    public EntityLivingBase getEntity() {
        return entity;
    }

    public int getTimes() {
        return times;
    }
}
