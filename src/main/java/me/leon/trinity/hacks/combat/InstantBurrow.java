package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.utils.entity.BlockUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class InstantBurrow extends Module {

    public Boolean rotate = new Boolean("Rotate", true);
    //public Mode mode = new Mode("Mode","Rubberband","Rubberband","Teleport","Clip");
    //public Boolean instant = new Boolean("Insta",true);
    //public Boolean centerPlayer = new Boolean("Center", false);
    //public Boolean silent = new Boolean("Silent", false);
    //public Boolean onGround = new Boolean("On Ground", false);
    public Slider offset = new Slider("Offset", -20.0f, 7.0f, 20.0F, false);
    public Boolean insta = new Boolean("Insta",false);

    private BlockPos originalPos;
    private int oldSlot = -1;

    public InstantBurrow() {
        super("InstantBurrow", "Instantly Burrow / glitch yourself into a block to avoid being crystalled", Category.COMBAT);
    }


    public void onEnable() {
        if (nullCheck())
            return;
        originalPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        int slot;
        // Change to obsidian slot
        slot = InventoryUtil.findObsidianSlot(false, true);
        if (slot != -1 && mc.player.inventory.currentItem != slot) {
            mc.player.inventory.currentItem = slot;

        }

        if(insta.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));
        }else {
            mc.player.jump();
        }
    }

    public void onUpdate() {

            // Fake jump // doesnt work wtf - momin5
            //mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
            //mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
            //mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
            //mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));
            //mc.player.jump();

            // Place block
            BlockUtils.placeBlockScaffold(originalPos);

            // Rubberband
            mc.player.motionY = 1.0D;
            //mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 7, mc.player.posZ, false));

            // AutoDisable
            toggle();

        }

    }
