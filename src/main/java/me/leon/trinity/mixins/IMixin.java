package me.leon.trinity.mixins;

import net.minecraft.client.Minecraft;

public interface IMixin {
    Minecraft mc = Minecraft.getMinecraft();
    boolean nullCheck = (mc.player == null || mc.world == null);
    boolean pCheck = (mc.player == null);
    boolean wCheck = (mc.world == null);
}
