package me.leon.trinity.hacks.player;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Boolean;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

public class FastUse extends Module {
    public static final Boolean exp = new Boolean("EXP", true);
    public static final Boolean obi = new Boolean("Obsidian", true);
    public static final Boolean echests = new Boolean("Ender Chests", true);
    public static final Boolean crystals = new Boolean("Crystals", true);
    public static final Boolean everything = new Boolean("Everything", true);

    public FastUse() {
        super("FastUse", "Use items faster", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if(nullCheck()) return;

        if(everything.getValue()) {
            mc.rightClickDelayTimer = 0;
        }
        
        if(exp.getValue()) {
            if(mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle || mc.player.getHeldItemOffhand().getItem() instanceof ItemExpBottle) {
                mc.rightClickDelayTimer = 0;
            }
        }

        if(obi.getValue()) {
            if((Block.getBlockFromItem(mc.player.getHeldItemMainhand().getItem()) == Blocks.OBSIDIAN || Block.getBlockFromItem(mc.player.getHeldItemOffhand().getItem()) == Blocks.OBSIDIAN)) {
                mc.rightClickDelayTimer = 0;
            }
        }

        if(crystals.getValue()) {
            if((mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)) {
                RayTraceResult r = mc.objectMouseOver;

                if(mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    if (r.typeOfHit == RayTraceResult.Type.BLOCK && !mc.player.isHandActive()) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(r.getBlockPos(), r.sideHit, (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND), (float) r.hitVec.x, (float) r.hitVec.y, (float) r.hitVec.z));
                    }
                }
            }
        }

        if(echests.getValue()) {
            if((Block.getBlockFromItem(mc.player.getHeldItemMainhand().getItem()) == Blocks.ENDER_CHEST || Block.getBlockFromItem(mc.player.getHeldItemOffhand().getItem()) == Blocks.ENDER_CHEST)) {
                mc.rightClickDelayTimer = 0;
            }
        }
    }
}
