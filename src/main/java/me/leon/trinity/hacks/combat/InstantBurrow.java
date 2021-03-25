package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.utils.entity.BlockUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
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
    //public Mode mode = new Mode("Mode","Rubberband","Rubberband","Teleport","Clip");
    //public Boolean instant = new Boolean("Insta",true);
    //public Boolean centerPlayer = new Boolean("Center", false);
    //public Boolean silent = new Boolean("Silent", false);
    //public Boolean onGround = new Boolean("On Ground", false);
    public Slider offset = new Slider("Offset", -20.0f, 7.0f, 20.0F, false);
    public Boolean insta = new Boolean("Insta",false);
    public Boolean switchback = new Boolean("SwitchBack", true);

    private BlockPos originalPos;

    public InstantBurrow() {
        super("InstantBurrow", "Instantly Burrow / glitch yourself into a block to avoid being crystalled", Category.COMBAT);
    }


    public void onEnable() {
        if (nullCheck())
            return;
        originalPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock().equals(Blocks.OBSIDIAN) ||
                intersectsWithEntity(this.originalPos))
        {
            toggle();
            return;
        }
    }

    public void onUpdate() {
        if (InventoryUtil.findHotbarBlock(BlockObsidian.class) == -1) {
            toggle();
            return;
        }
        int slot = mc.player.inventory.currentItem;
        InventoryUtil.switchToSlot(InventoryUtil.findHotbarBlock(BlockObsidian.class));

        if(insta.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));
        } else {
            mc.player.setPosition(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ);
            mc.player.setPosition(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ);
            mc.player.setPosition(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ);
            mc.player.setPosition(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ);
        }

        BlockUtils.placeBlock(originalPos, EnumHand.MAIN_HAND, rotate.getValue(), true, false);
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset.getValue(), mc.player.posZ, false));
        InventoryUtil.switchToSlot(slot);
        toggle();
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
