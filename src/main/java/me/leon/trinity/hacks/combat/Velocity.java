package me.leon.trinity.hacks.combat;


import me.leon.trinity.events.main.EventPacketRecieve;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.common.MinecraftForge;

public class Velocity extends Module {

    public Velocity(){
        super("Velocity","Cancels knockback from all entities", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        Trinity.dispatcher.subscribe(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        Trinity.dispatcher.unsubscribe(this);
        MinecraftForge.EVENT_BUS.unregister(this);
    }


    @EventHandler
    private final Listener<EventPacketRecieve> receiveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketEntityVelocity){
            if (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {
                event.cancel();
            }
        }
        if (event.getPacket() instanceof SPacketExplosion){
            event.cancel();
        }
    });
}
