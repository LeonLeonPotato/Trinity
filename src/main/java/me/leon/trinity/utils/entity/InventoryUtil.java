package me.leon.trinity.utils.entity;

import me.leon.trinity.utils.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import java.util.List;

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

    public static int getBlockInHotbar(Block block) {
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().equals(block))
                return i;
        }

        return -1;
    }



    public static void switchToSlot(Block block) {
        if (getBlockInHotbar(block) != -1 && mc.player.inventory.currentItem != getBlockInHotbar(block)){
            mc.player.inventory.currentItem = getBlockInHotbar(block);
            mc.playerController.updateController();
        }

    }

    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) {
                continue;
            }

            if (clazz.isInstance(stack.getItem())) {
                return i;
            }

            if (stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (clazz.isInstance(block)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static void switchToSlot(int slot) {
        if (slot != -1 && mc.player.inventory.currentItem != slot) {
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }

    }

    public static int amountInHotbar(Item item) {
        int quantity = 0;

        for(int i = 44; i > 35; i--) {
            ItemStack stackInSlot = mc.player.inventoryContainer.getSlot(i).getStack();
            if(stackInSlot.getItem() == item) quantity += stackInSlot.getCount();
        }
        if(mc.player.getHeldItemOffhand().getItem() == item) quantity += mc.player.getHeldItemOffhand().getCount();

        return quantity;
    }

    public static int amountBlockInHotbar(Block block) {return amountInHotbar(new ItemStack(block).getItem());}

}
