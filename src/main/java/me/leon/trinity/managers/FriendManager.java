package me.leon.trinity.managers;

import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FriendManager {
    private ConcurrentHashMap<String, UUID> friends;

    public FriendManager() {
        this.friends = new ConcurrentHashMap<>();
    }

    public boolean isFriend(EntityPlayer player) {
        if(friends.get(player.getName()) != null) {
            UUID uuid = friends.get(player.getName());
            return player.getPersistentID() == uuid;
        }
        return false;
    }

    public void add(String name, UUID uuid) {
        this.friends.put(name, uuid);
    }

    public void remove(String name, UUID uuid) {
        this.friends.remove(name, uuid);
    }
}
