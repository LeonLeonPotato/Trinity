package me.leon.trinity.utils.misc;

import me.leon.trinity.hacks.client.Font;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.Util;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.InputStream;

public class FontUtil implements Util {
    private static CFontRenderer fontRenderer = null;
    private static final java.awt.Font comfortaa = getFont("comfortaa.ttf", 1);
    private static final java.awt.Font ubuntu = getFont("Ubuntu.ttf", 1);

    public static void load() { }

    public static float drawString(String text, float x, float y, Color color) {
        return drawString(text, x, y, color, Font.shadow.getValue());
    }

    public static float drawString(String text, float x, float y, int color) {
        return drawString(text, x, y, color, Font.shadow.getValue());
    }

    public static float drawString(String text, float x, float y, Color color, boolean shadow) {
        final CFontRenderer renderer = getFontRenderer();
        if(renderer == null) {
            if(shadow)
                return mc.fontRenderer.drawStringWithShadow(text, (int) x, (int) y, color.getRGB());
            else
                return mc.fontRenderer.drawString(text, (int) x, (int) y, color.getRGB());
        } else {
            //y -= 1;
            if(shadow)
                return getFontRenderer().drawStringWithShadow(text, x, y, color);
            else
                return getFontRenderer().drawString(text, x, y, color);
        }
    }

    public static float drawString(String text, float x, float y, int color, boolean shadow) {
        final CFontRenderer renderer = getFontRenderer();
        if(renderer == null) {
            if(shadow)
                return mc.fontRenderer.drawStringWithShadow(text, (int) x, (int) y, color);
            else
                return mc.fontRenderer.drawString(text, (int) x, (int) y, color);
        } else {
            if(shadow)
                return getFontRenderer().drawStringWithShadow(text, x, y, new Color(color));
            else
                return getFontRenderer().drawString(text, x, y, new Color(color));
        }
    }

    public static float getStringWidth(String string) {
        final CFontRenderer renderer = getFontRenderer();
        if(renderer != null) {
            return renderer.getStringWidth(string);
        } else {
            return mc.fontRenderer.getStringWidth(string);
        }
    }

    public static int getFontHeight() {
        final CFontRenderer renderer = getFontRenderer();
        if(renderer != null) {
            return renderer.getHeight();
        } else {
            return mc.fontRenderer.FONT_HEIGHT;
        }
    }

    private static CFontRenderer getFontRenderer() {
        if(Font.enabled()) {
            if(fontRenderer == null || !Font.families.getValue().equals(fontRenderer.fontName) || getStyle(Font.style.getValue()) != fontRenderer.font.getStyle()) {
                CFontRenderer renderer = fontRenderer = new CFontRenderer(getFont(), true, true);
                return renderer;
            } else if(Font.families.getValue().equals(fontRenderer.fontName)) {
                return fontRenderer;
            }
        }
        return null;
    }

    private static java.awt.Font getFont(String fontName, float size) {
        try {
            InputStream inputStream = FontUtil.class.getResourceAsStream("/assets/trinity/fonts/" + fontName);
            java.awt.Font awtClientFont = java.awt.Font.createFont(0, inputStream);
            awtClientFont = awtClientFont.deriveFont(getStyle(Font.style.getValue()), size);
            inputStream.close();

            return awtClientFont;
        } catch (Exception e) {
            e.printStackTrace();
            return new java.awt.Font("default", java.awt.Font.PLAIN, (int) size);
        }
    }

    private static java.awt.Font getFont() {
        switch (Font.families.getValue()) {
            case "Comfortaa": {
                return comfortaa.deriveFont(getStyle(Font.style.getValue()), 18);
            }
            case "Ubuntu": {
                return ubuntu.deriveFont(getStyle(Font.style.getValue()), 18);
            }
            default: {
                return new java.awt.Font(Font.families.getValue(), getStyle(Font.style.getValue()), 18);
            }
        }
    }

    private static int getStyle(String name) {
        switch (name) {
            case "Bold": {
                return 1;
            }
            case "Italic": {
                return 2;
            }
            case "Bold-Italic": {
                return 3;
            }
            default: {
                return 0;
            }
        }
    }
}
