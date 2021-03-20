package me.leon.trinity.utils.entity;

import me.leon.trinity.utils.Util;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;

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

    public static boolean switchTo(Item item) {
        int a = find(item);
        if(a == -1) return false;
        mc.player.inventory.currentItem = a;
        mc.playerController.updateController();
        return true;
    }
}
