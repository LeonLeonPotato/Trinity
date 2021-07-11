package me.leon.trinity.notification;

import me.leon.trinity.hud.components.NotificationComponent;
import me.leon.trinity.utils.world.Timer;
import org.lwjgl.Sys;

public class Notification {
    private String name, description;
    private NotifType type;
    private int mode = 0;

    public Timer timer;
    public long queuedTime;


    public Notification() {
    }

    public Notification(String name, String description, NotifType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NotifType getType() {
        return type;
    }

    public void setType(NotifType type) {
        this.type = type;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getDeathTime() {
        if(mode != 2) return 0;
        return (int) (System.currentTimeMillis() - (queuedTime + NotificationComponent.time.getValue() + NotificationComponent.fadeInTime.getValue()));
    }

    public int getFadeTime() {
        if(mode != 0) return 0;
        return (int) (System.currentTimeMillis() - (queuedTime + NotificationComponent.fadeInTime.getValue()));
    }

    public boolean isOverTime() {
        return timer.hasPassed((int) NotificationComponent.fadeInTime.getValue());
    }

    public boolean isOverTime1() {
        return timer.hasPassed((int) (NotificationComponent.fadeInTime.getValue() + NotificationComponent.time.getValue()));
    }

    public boolean isOverTime2() {
        return timer.hasPassed((int) (NotificationComponent.dieTime.getValue() + NotificationComponent.time.getValue() + NotificationComponent.fadeInTime.getValue()));
    }
}
