package me.leon.trinity.utils.entity;

import me.leon.trinity.utils.Util;
import net.minecraft.item.Item;

public class InventoryUtil implements Util {
    public static int findFirst(Class<? extends Item> clazz) {
        int b = -1;
        for(int a = 0; a < 9; a++) {
            if(mc.player.inventory.getStackInSlot(a).getItem().getClass().equals(clazz)) {
                b = a;
                break;
            }
        }
        return b;
    }
    public static int find(Class<? extends Item> clazz) {
        int b = -1;
        for(int a = 0; a < 9; a++) {
            if(mc.player.inventory.getStackInSlot(a).getItem().getClass().equals(clazz)) {
                b = a;
            }
        }
        return b;
    }

    public static int findFirst(Item item) {
        int b = -1;
        for (int a = 0; a < 9; a++) {
            if (mc.player.inventory.getStackInSlot(a).getItem() == item) {
                b = a;
                break;
            }
        }
        return b;
    }

    public static int find(Item item) {
        int b = -1;
        for (int a = 0; a < 9; a++) {
            if (mc.player.inventory.getStackInSlot(a).getItem() == item) {
                b = a;
            }
        }
        return b;
    }
}
