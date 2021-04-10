package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.utils.entity.BlockUtils;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.misc.MessageBus;
import me.leon.trinity.utils.world.HoleUtils;
import me.leon.trinity.utils.world.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class HoleFill extends Module {
    public static Boolean doubleHoles = new Boolean("DoubleHoles", false);
    public static Slider bps = new Slider("Blocks Per Tick", 1, 3, 8, true);
    public static Slider range = new Slider("Range", 1, 3, 8, true);
    public static Boolean toggle = new Boolean("Toggle", false);
    public static Boolean packet = new Boolean("Packet", false);
    public static Boolean antiGlitch = new Boolean("AntiGlitch", false);
    public static Boolean rotate = new Boolean("Rotate", false);

    public HoleFill() {
        super("HoleFill", "Fills holes near you", Category.COMBAT);
    }

    public void onEnable() {
        if(nullCheck()) return;
    }

    public void onUpdate() {
        if(nullCheck()) return;
        int oldslot = mc.player.inventory.currentItem;
        matCheck();
        int placed = 0;
        final ArrayList<HoleUtils.Hole> holeList = HoleUtils.holes((float) range.getValue(), -1);
        for(final HoleUtils.Hole hole : holeList) {
            if(placed >= bps.getValue()) continue;

            if(hole instanceof HoleUtils.SingleHole && !EntityUtils.isIntercepted(((HoleUtils.SingleHole) hole).pos) && WorldUtils.empty.contains(WorldUtils.getBlock(((HoleUtils.SingleHole) hole).pos))) {
                BlockUtils.placeBlock(((HoleUtils.SingleHole) hole).pos, rotate.getValue(), false, true, packet.getValue(), true, antiGlitch.getValue());
                placed++;
            }
            if(placed >= bps.getValue()) continue;

            if(hole instanceof HoleUtils.DoubleHole && doubleHoles.getValue()) {
                if(placed >= bps.getValue()) continue;

                final HoleUtils.DoubleHole doubleH = ((HoleUtils.DoubleHole) hole);
                if(getDist(doubleH.pos) && !EntityUtils.isIntercepted(doubleH.pos) && WorldUtils.empty.contains(WorldUtils.getBlock(doubleH.pos))) {
                    BlockUtils.placeBlock(doubleH.pos, rotate.getValue(), false, true, packet.getValue(), true, antiGlitch.getValue());
                    placed++;
                }
                if(placed >= bps.getValue()) continue;

                if(getDist(doubleH.pos1) && !EntityUtils.isIntercepted(doubleH.pos1) && WorldUtils.empty.contains(WorldUtils.getBlock(doubleH.pos1))) {
                    BlockUtils.placeBlock(doubleH.pos1, rotate.getValue(), false, true, packet.getValue(), true, antiGlitch.getValue());
                    placed++;
                }
            }
        }

        InventoryUtil.switchToSlot(oldslot);

        if(placed == 0 && holeList.isEmpty()) {
            if(toggle.getValue()) {
                toggle0("Finished Holefilling, toggling!");

            }
        }
    }

    private boolean getDist(BlockPos pos) {
        return pos.add(0.5, 0.5, 0.5).distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) < Math.pow(range.getValue(), 2);
    }

    private void matCheck() {
        if(!InventoryUtil.switchTo(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
            if(!InventoryUtil.switchTo(Item.getItemFromBlock(Blocks.ENDER_CHEST))) {
                toggle0("Cannot find materials!");
                this.setEnabled(false);
            }
        }
    }

    private void toggle0(String message) {
        MessageBus.sendClientMessage(message, true);
        this.setEnabled(false);
    }
}
