package me.leon.trinity.hacks.misc;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

public class ChatSuffix extends Module {
    public ChatSuffix(){
        super("Suffix","Adds the client name to the end of your messages", Category.MISC);
    }

    public void onEnable(){
    }

    @SubscribeEvent
    public void onChat(final ClientChatEvent event){
        Trinity.dispatcher.post(event);
        if(isEnabled()) {
            for (final String s : Arrays.asList("/", ".", "-", ",", ":", ";", "'", "+", "\\", "@"))
            {
                if (event.getMessage().startsWith(s)) return;
            }
            event.setMessage(event.getMessage() + " " + "\u23D0" + toUnicode(" " + Trinity.CLIENTNAME));
        }
    }

    public String toUnicode(String s) {
        return s.toLowerCase()
                .replace("a", "\u1d00")
                .replace("b", "\u0299")
                .replace("c", "\u1d04")
                .replace("d", "\u1d05")
                .replace("e", "\u1d07")
                .replace("f", "\ua730")
                .replace("g", "\u0262")
                .replace("h", "\u029c")
                .replace("i", "\u026a")
                .replace("j", "\u1d0a")
                .replace("k", "\u1d0b")
                .replace("l", "\u029f")
                .replace("m", "\u1d0d")
                .replace("n", "\u0274")
                .replace("o", "\u1d0f")
                .replace("p", "\u1d18")
                .replace("q", "\u01eb")
                .replace("r", "\u0280")
                .replace("s", "\ua731")
                .replace("t", "\u1d1b")
                .replace("u", "\u1d1c")
                .replace("v", "\u1d20")
                .replace("w", "\u1d21")
                .replace("x", "\u02e3")
                .replace("y", "\u028f")
                .replace("z", "\u1d22");
    }
}
