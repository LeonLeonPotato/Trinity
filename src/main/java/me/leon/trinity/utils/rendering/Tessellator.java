package me.leon.trinity.utils.rendering;

import me.leon.trinity.utils.Util;
import net.minecraft.client.renderer.BufferBuilder;

public class Tessellator implements Util {
    private static net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
    private static BufferBuilder builder = tessellator.getBuffer();
}
