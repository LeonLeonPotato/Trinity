package me.leon.trinity.utils;

import net.minecraft.client.Minecraft;

/**
 * used for utils, i don't wanna declare mc every time ok?????
 */
public interface Util {
    Minecraft mc = Minecraft.getMinecraft();
    boolean nullCheck = (mc.player == null || mc.world == null);
}
