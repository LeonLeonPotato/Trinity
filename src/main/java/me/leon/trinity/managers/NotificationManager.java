package me.leon.trinity.managers;

import me.leon.trinity.notification.Notification;
import me.leon.trinity.utils.world.Timer;

import java.util.concurrent.ConcurrentLinkedDeque;

public class NotificationManager {
    public static ConcurrentLinkedDeque<Notification> queue = new ConcurrentLinkedDeque<>();

    public NotificationManager() {

    }

    public static void add(Notification notification) {
        notification.timer = new Timer();
        notification.queuedTime = System.currentTimeMillis();
        queue.addFirst(notification);
    }
}
