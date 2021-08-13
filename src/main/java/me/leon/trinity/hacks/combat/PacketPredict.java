package me.leon.trinity.hacks.combat;

import me.leon.trinity.events.main.EventPacketSend;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.world.WorldUtils;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;

public class PacketPredict extends Module {
    public PacketPredict() {
        super("PacketPredict", "Predict crystal entity ID", Category.COMBAT);
    }

    @EventHandler
    private final Listener<EventPacketSend> sendListener = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
            if(WorldUtils.getBlock(packet.position) == Blocks.OBSIDIAN) {
                if(mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                    int id = 0;
                    for(Entity e : mc.world.loadedEntityList) {
                        if(e.entityId > id) {
                            id = e.entityId;
                        }
                    }
                    attackByID(id+1);
                    attackByID(id+2);
                    attackByID(id+3);
                    attackByID(id+4);
                    attackByID(id+5);
                    Trinity.LOGGER.info(id);
                }
            }
        }
    });

    private void attackByID(int entityId) {
        CPacketUseEntity sequentialCrystal = new CPacketUseEntity();
        sequentialCrystal.entityId = entityId;
        sequentialCrystal.action = CPacketUseEntity.Action.ATTACK;
        mc.player.connection.sendPacket(sequentialCrystal);
    }
}
