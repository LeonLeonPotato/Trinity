package me.leon.trinity.utils.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.leon.trinity.utils.Util;
import net.minecraft.util.text.TextComponentString;

public class MessageBus implements Util {
    public static void sendClientMessage(String message, boolean prefix) {
        mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(prefix ? (ChatFormatting.DARK_PURPLE + "[TRINITY] " + ChatFormatting.RESET + message) : message));
    }
}
