package me.leon.trinity.utils.rendering;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.leon.trinity.hacks.client.Font;
import me.leon.trinity.main.Trinity;

/**
 * Coloring because minecraft's ChatFormatting doesn't work ):
 */
public class Coloring {
    private static final String RESET = "§r";
    private static final String WHITE = "§f";
    private static final String GREY = "§7";
    private static final String DARKGREY = "§8";
    private static final String BLACK = "§0";
    private static final String DARKBLUE = "§1";
    private static final String DARKGREEN = "§2";
    private static final String DARKAQUA = "§3";
    private static final String DARKRED = "§4";
    private static final String DARKPURPLE = "§5";
    private static final String GOLD = "§6";
    private static final String BLUE = "§9";
    private static final String GREEN = "§a";
    private static final String AQUA = "§b";
    private static final String RED = "§c";
    private static final String LIGHTPURPLE = "§d";
    private static final String YELLOW = "§e";

    public static Object getRESET() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? RESET : ChatFormatting.RESET;
    }

    public static Object getWHITE() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? WHITE : ChatFormatting.WHITE;
    }

    public static Object getGREY() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? GREY : ChatFormatting.GRAY;
    }

    public static Object getDARKGREY() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? DARKGREY : ChatFormatting.DARK_GRAY;
    }

    public static Object getBLACK() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? BLACK : ChatFormatting.BLACK;
    }

    public static Object getDARKBLUE() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? DARKBLUE : ChatFormatting.DARK_BLUE;
    }

    public static Object getDARKGREEN() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? DARKGREEN : ChatFormatting.DARK_GREEN;
    }

    public static Object getDARKAQUA() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? DARKAQUA : ChatFormatting.DARK_AQUA;
    }

    public static Object getDARKRED() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? DARKRED : ChatFormatting.DARK_RED;
    }

    public static Object getDARKPURPLE() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? DARKPURPLE : ChatFormatting.DARK_PURPLE;
    }

    public static Object getGOLD() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? GOLD : ChatFormatting.GOLD;
    }

    public static Object getBLUE() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? BLUE : ChatFormatting.BLUE;
    }

    public static Object getGREEN() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? GREEN : ChatFormatting.GREEN;
    }

    public static Object getAQUA() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? AQUA : ChatFormatting.AQUA;
    }

    public static Object getRED() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? RED : ChatFormatting.RED;
    }

    public static Object getLIGHTPURPLE() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? LIGHTPURPLE : ChatFormatting.LIGHT_PURPLE;
    }

    public static Object getYELLOW() {
        return Trinity.moduleManager.getMod(Font.class).isEnabled() ? YELLOW : ChatFormatting.YELLOW;
    }
}
