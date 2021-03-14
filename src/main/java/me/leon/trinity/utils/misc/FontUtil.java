package me.leon.trinity.utils.misc;

import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.world.Timer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.InputStream;

public class FontUtil implements Util {

    public static FontRender verdana = null;
    public static FontRender lato = null;
    public static FontRender ubuntu = null;
    public static FontRender comfortaa = null;
    public static FontRender comicsans = null;

    public static Timer cursorTimer = new Timer();
    public static boolean blink = false;

    public void load() {
        try {
            lato = new FontRender(FontUtil.getFont("Lato.ttf", (float) me.leon.trinity.hacks.client.Font.scale.getValue()));
            comfortaa = new FontRender(FontUtil.getFont("comfortaa.ttf", (float) me.leon.trinity.hacks.client.Font.scale.getValue()));
            comicsans = new FontRender(FontUtil.getFont("comic-sans.ttf", (float) me.leon.trinity.hacks.client.Font.scale.getValue()));
            verdana = new FontRender(FontUtil.getFont("Verdana.ttf", (float) me.leon.trinity.hacks.client.Font.scale.getValue()));
            ubuntu = new FontRender(FontUtil.getFont("Ubuntu.ttf", (float) me.leon.trinity.hacks.client.Font.scale.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Font getFont(String fontName, float size) {
        try {
            InputStream inputStream = FontUtil.class.getResourceAsStream("/trinity/resources/fonts/" + fontName);
            Font awtClientFont = Font.createFont(0, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();

            return awtClientFont;
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("default", Font.PLAIN, (int) size);
        }
    }

    public FontRender getCustomFont() {
        switch (me.leon.trinity.hacks.client.Font.families.getValue()) {
            case "Lato":
                return lato;
            case "Ubuntu":
                return ubuntu;
            case "Verdana":
                return verdana;
            case "Comfortaa":
                return comfortaa;
            case "Comic-Sans":
                return comicsans;
        }

        return lato;
    }

    public static void drawString(String text, float x, float y, int color) {
        GL11.glPushMatrix();
        if (Trinity.moduleManager.getMod("Font").isEnabled()) {
            y += 1;
            if (me.leon.trinity.hacks.client.Font.shadow.getValue())
                Trinity.fontManager.getCustomFont().drawStringWithShadow(text, x, y, color);
            else
                Trinity.fontManager.getCustomFont().drawString(text, (int) x, (int) y, color);
        }

        else {
            if (me.leon.trinity.hacks.client.Font.shadow.getValue())
                mc.fontRenderer.drawStringWithShadow(text, x, y, color);
            else
                mc.fontRenderer.drawString(text, (int) x, (int) y, color);
        }
        GL11.glPopMatrix();
    }

    public static int getString(String text, float x, float y, int color) {
        if (Trinity.moduleManager.getMod("Font").isEnabled()) {
            if (me.leon.trinity.hacks.client.Font.shadow.getValue())
                return Trinity.fontManager.getCustomFont().drawStringWithShadow(text, x, y, color);
            else
                return Trinity.fontManager.getCustomFont().drawString(text, (int) x, (int) y, color);
        }

        else {
            if (me.leon.trinity.hacks.client.Font.shadow.getValue())
                return mc.fontRenderer.drawStringWithShadow(text, x, y, color);
            else
                return mc.fontRenderer.drawString(text, (int) x, (int) y, color);
        }
    }

    public static float getStringWidth(String text) {
        if (Trinity.moduleManager.getMod("Font").isEnabled())
            return Trinity.fontManager.getCustomFont().getStringWidth(text);
        else
            return mc.fontRenderer.getStringWidth(text) + 4;
    }

    public static float getFontHeight() {
        if (Trinity.moduleManager.getMod("Font").isEnabled())
            return Trinity.fontManager.getCustomFont().FONT_HEIGHT;
        else
            return mc.fontRenderer.FONT_HEIGHT;
    }
}
