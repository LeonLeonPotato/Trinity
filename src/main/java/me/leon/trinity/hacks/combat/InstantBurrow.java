package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.player.NoPush;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.utils.entity.BlockUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.entity.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.io.BufferedReader;

public class InstantBurrow extends Module {
/*
ok im gonna comment this thingy lol - momin5
 */
    private Mode rubberband = new Mode("RubbMode", "Packet","Packet","Jump");
    private Mode blocksToUse = new Mode("Blocks","Obsidian","Obsidian","EChest");
    private Mode backupBlock = new Mode("Backup", "EChest","Obsidian","EChest");
    private Boolean rotate = (new Boolean("Rotate", true));
    private Boolean preventBlockPush = new Boolean("NoBlockPush", true);
    private Boolean fakeJump = new Boolean("Insta", true);
    private Slider fakeClipHeight = new Slider("Clip",12,-60,60,false);
    private BlockPos playerPos;
    float oldYaw;
    float oldPitch;

    int oldSelection = -1;

    public InstantBurrow() {
        super("InstantBurrow", "Instantly Burrow / glitch yourself into a block to avoid being crystalled", Category.COMBAT);
    }

    // gets the current block
    private Block getCurrBlock(){
        Block index = null;
        if (blocksToUse.getValue().equals("Obsidian")) {index = Blocks.OBSIDIAN;}
        else if (blocksToUse.getValue().equals("EChest")) {index = Blocks.ENDER_CHEST;}
        return index;
    }

    //if the current bock doesnt exist it will choose this one
    private Block getBackBlock(){
        Block index = null;
        if (backupBlock.getValue().equals("Obsidian")) {index = Blocks.OBSIDIAN;}
        else if (backupBlock.getValue().equals("EChest")) {index = Blocks.ENDER_CHEST;}
        return index;
    }

    //switch to the block, if you make a incorrect selection with Packethelditemexchange it kicks you from the server lol
    private void switchToBlock() {
        oldSelection = mc.player.inventory.currentItem;

        int newSelection = InventoryUtil.getBlockInHotbar(getCurrBlock());
        if (newSelection == -1) newSelection = InventoryUtil.getBlockInHotbar(getBackBlock());
        if (newSelection != -1) mc.player.connection.sendPacket(new CPacketHeldItemChange(newSelection));
        else toggle();
    }

    public void onEnable() {
        if (nullCheck())
            return;

        // gets the player
        playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        // puts the no block push class on
        if(preventBlockPush.getValue())
            ModuleManager.getMod(NoPush.class).setEnabled(true);

        // sees if the blovks exitst
        if (mc.world.getBlockState(playerPos).getBlock() == getCurrBlock() || mc.world.getBlockState(playerPos).getBlock() == getBackBlock()) {
            setEnabled(false);
            return;
        }

        // if you have 0 or less than zero then it disables, i will have to had a new mehtod in InventoryUtil so i can get the amount of blocks
        if (InventoryUtil.amountBlockInHotbar(getCurrBlock()) <= 0 && InventoryUtil.amountBlockInHotbar(getBackBlock()) <= 0) {
            setEnabled(false);
            return;
        }

        // if no insta mode then jump normally
        if(!fakeJump.getValue()){
            mc.player.jump();
        }

    }

    public void onUpdate() {
        if(nullCheck())
            return;

        oldYaw = mc.player.rotationYaw;
        oldPitch = mc.player.rotationPitch;

        switchToBlock();

        if (fakeJump.getValue()) {
            fakeJump();
            burrow();
        }

        if (!fakeJump.getValue()) {
            if (mc.player.posY >= playerPos.getY() + 1.12) {
                burrow();
            }
        }
    }

    private void burrow(){
        //place block where the player was before jumping
        BlockUtils.placeBlock(playerPos, EnumHand.MAIN_HAND,rotate.getValue(),true,false);

        mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSelection));

        //tries to produce a rubberband
        if (rubberband.getValue().equals("Packet") || fakeJump.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + fakeClipHeight.getValue(), mc.player.posZ, false));
        } else mc.player.jump();

        //disable module
        setEnabled(false);
    }

    public void fakeJump() {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.40, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.75, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.01, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.15, mc.player.posZ, true));
    }

}
