package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.utils.entity.BlockUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.entity.PlayerUtils;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.io.BufferedReader;

public class InstantBurrow extends Module {

    public Boolean rotate = new Boolean("Rotate", true);
    public Boolean packet = new Boolean("Packet", true);
    public Slider offset = new Slider("Offset", -20, 10, 20, false);
    private BlockPos originalPos;

    public InstantBurrow() {
        super("InstantBurrow", "Instantly Burrow / glitch yourself into a block to avoid being crystalled", Category.COMBAT);
    }


    public void onEnable() {
        if (nullCheck())
            return;
        this.originalPos = PlayerUtils.getPlayerPosFloored();

        if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock().equals(Blocks.OBSIDIAN) || intersectsWithEntity(this.originalPos)) {
            this.setEnabled(false);
            return;
        }
    }

    public void onUpdate() {
        if (InventoryUtil.findHotbarBlock(BlockObsidian.class) == -1) {
            this.setEnabled(false);
            return;
        }

        int oldSlot = mc.player.inventory.currentItem;
        InventoryUtil.switchToSlot(InventoryUtil.findHotbarBlock(BlockObsidian.class));

        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));

        BlockUtils.placeBlock(originalPos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false);
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset.getValue(), mc.player.posZ, false));
        InventoryUtil.switchToSlot(oldSlot);
        this.setEnabled(false);
    }

    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : mc.world.loadedEntityList) {
            if (entity.equals(mc.player)) continue;
            if (entity instanceof EntityItem) continue;
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) return true;
        }
        return false;
    }
}
