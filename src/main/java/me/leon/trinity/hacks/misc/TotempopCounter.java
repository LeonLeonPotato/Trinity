package me.leon.trinity.hacks.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.EventPacketRecieve;
import me.leon.trinity.events.main.EventTotemPop;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.combat.AutoCrystal;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.NotificationManager;
import me.leon.trinity.managers.TotempopManager;
import me.leon.trinity.notification.NotifType;
import me.leon.trinity.notification.Notification;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.utils.misc.MessageBus;
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
import java.util.List;
import java.util.Set;

public class TotempopCounter extends Module {
    public static final ModeSetting notif = new ModeSetting("Notification", "Notif", "Notif", "Chat", "Both");
    public static final ModeSetting mode = new ModeSetting("Mode", "Notify", "Notify", "Pops");
    public static final BooleanSetting self = new BooleanSetting("Self", false);
    public static final BooleanSetting death = new BooleanSetting("Death", true);
    public static final BooleanSetting caTarget = new BooleanSetting("Only CA Target", false);

    public TotempopCounter() {
        super("TotemPopCounter", "Counts totem pops", Category.MISC);
    }

    @EventHandler
    private final Listener<EventTotemPop> packetRecieveListener = new Listener<>(event -> {
        if(event.getEntity() == mc.player && !self.getValue()) return;
        if(event.getEntity() != AutoCrystal.target && caTarget.getValue()) return;

        final String message;
        if(mode.getValue().equals("Notify")) {
            message = ChatFormatting.DARK_RED + event.getEntity().getName() + ChatFormatting.RESET + " Popped a totem";
        } else {
            message = ChatFormatting.DARK_RED + event.getEntity().getName() + ChatFormatting.RESET + " Popped " + ChatFormatting.LIGHT_PURPLE + event.getTimes() + ChatFormatting.RESET
                    + (TotempopManager.getPops(event.getEntity()) == 1 ? " totem" : " totems");
        }

        switch (notif.getValue()) {
            case "Notif": {
                NotificationManager.add(new Notification("Totem Pop", message, NotifType.INFO));
                break;
            }
            case "Chat": {
                MessageBus.sendClientMessage(message, true);
                break;
            }
            case "Both": {
                MessageBus.sendClientMessage(message, true);
                NotificationManager.add(new Notification("Totem Pop", message, NotifType.INFO));
                break;
            }
        }
    });

    @Override
    public void onUpdate() {
        if(nullCheck()) return;

        for(Entity e : mc.world.loadedEntityList) {
            if(e instanceof EntityLivingBase) {
                if(e == mc.player && !self.getValue()) continue;
                if(e != AutoCrystal.target && caTarget.getValue()) continue;

                final EntityLivingBase living = (EntityLivingBase) e;
                if(TotempopManager.totemMap.containsKey(e) && living.getHealth() <= 0) {
                    if(death.getValue()) {
                        final String message = ChatFormatting.DARK_RED + living.getName() + ChatFormatting.RESET + " Died after " + ChatFormatting.LIGHT_PURPLE + TotempopManager.getPops(living) + ChatFormatting.RESET
                                + (TotempopManager.getPops(living) == 1 ? " totem" : " totems");
                        switch (notif.getValue()) {
                            case "Notif": {
                                NotificationManager.add(new Notification("Death", message, NotifType.INFO));
                                break;
                            }
                            case "Chat": {
                                MessageBus.sendClientMessage(message, true);
                                break;
                            }
                            case "Both": {
                                MessageBus.sendClientMessage(message, true);
                                NotificationManager.add(new Notification("Death", message, NotifType.INFO));
                                break;
                            }
                        }
                    }
                    TotempopManager.totemMap.remove(living);
                }
            }
        }
    }
}
