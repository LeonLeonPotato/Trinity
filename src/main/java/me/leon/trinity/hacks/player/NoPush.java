package me.leon.trinity.hacks.player;

import me.leon.trinity.events.main.BlockPushEvent;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraftforge.common.MinecraftForge;

public class NoPush extends Module {
    public NoPush(){
        super("NoPush","cancels any block push", Category.PLAYER);
    }

    public void onEnable(){
        MinecraftForge.EVENT_BUS.register(this);
        Trinity.dispatcher.subscribe(this);
    }

    @EventHandler
    public Listener<BlockPushEvent> onBurrowPush = new Listener<>(event -> {
        event.cancel();
    });
}
