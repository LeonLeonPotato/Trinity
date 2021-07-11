package me.leon.trinity.hacks.combat;

import me.leon.trinity.events.main.BlockPushEvent;
import me.leon.trinity.events.main.LocationSpoofEvent;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.player.NoPush;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.managers.SpoofingManager;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.entity.BlockUtils;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.world.Priority;
import me.leon.trinity.utils.world.WorldUtils;
import me.leon.trinity.utils.world.location.Location;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

/**
 * @author momin5
 *
 * fixed by Leon - May 16, 20201
 */
public class Burrow extends Module {
	/*
	ok im gonna comment this thingy lol - momin5
	 */
	private final ModeSetting mode = new ModeSetting("Mode", "Packet", "Normal", "Packet", "Double");
	private final ModeSetting blocksToUse = new ModeSetting("Blocks", "Obsidian", "Obsidian", "EChest", "Skull");
	private final ModeSetting backupBlock = new ModeSetting("Backup", "EChest", "Obsidian", "EChest", "Skull");
	private final BooleanSetting rotate = (new BooleanSetting("Rotate", true));
	private final BooleanSetting preventBlockPush = new BooleanSetting("NoBlockPush", true);
	private final BooleanSetting sneak = new BooleanSetting("Sneak", false);
	private final BooleanSetting packetPlace = new BooleanSetting("PacketPlace", false);
	private final SliderSetting fakeClipHeight = new SliderSetting("Clip", -5, 2, 5, false);
	public static BooleanSetting smart = new BooleanSetting("Smart", true, true);
	public static SliderSetting distance = new SliderSetting("Distance", smart, 0, 1, 3, false);
	public static BooleanSetting predict = new BooleanSetting("Predict", smart, true);
	public static SliderSetting ticks = new SliderSetting("Ticks", smart, 1, 3, 5, true);
	private int oldSelection = -1;
	private BlockPos playerPos;
	private boolean burrowing;

	public Burrow() {
		super("InstantBurrow", "Instantly Burrow / glitch yourself into a block to avoid being crystalled", Category.COMBAT);
	}

	// gets the current block
	private Block getCurrBlock() {
		Block index = null;
		switch (blocksToUse.getValue()) {
			case "Obsidian":
				index = Blocks.OBSIDIAN;
				break;
			case "EChest":
				index = Blocks.ENDER_CHEST;
				break;
			case "Skull":
				index = Blocks.SKULL;
				break;
		}
		return index;
	}

	//if the current bock doesnt exist it will choose this one
	private Block getBackBlock() {
		Block index = null;
		switch (backupBlock.getValue()) {
			case "Obsidian":
				index = Blocks.OBSIDIAN;
				break;
			case "EChest":
				index = Blocks.ENDER_CHEST;
				break;
			case "Skull":
				index = Blocks.SKULL;
				break;
		}
		return index;
	}

	//switch to the block, if you make a incorrect selection with Packethelditemexchange it kicks you from the server lol
	private void switchToBlock() {
		oldSelection = mc.player.inventory.currentItem;

		int newSelection = InventoryUtil.getBlockInHotbar(getCurrBlock());
		if (newSelection == -1) newSelection = InventoryUtil.getBlockInHotbar(getBackBlock());
		if (newSelection != -1) {
			if(!packetPlace.getValue())
				mc.player.connection.sendPacket(new CPacketHeldItemChange(newSelection));
			else
				InventoryUtil.switchToSlot(newSelection);
		}
		else this.setEnabled(false);
	}

	@Override
	public void onEnable() {
		if (nullCheck())
			return;

		// gets the player
		playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
		if(!WorldUtils.empty.contains(WorldUtils.getBlock(playerPos.up(2))) && !smart.getValue()) {
			this.toggleWithMessage("There is a block above your head!");
			return;
		}

		if(check()) return;

		// jump
		jump();

		if (mode.getValue().equalsIgnoreCase("Packet")) {
			burrow();
		}
	}

	@Override
	public void onDisable() {
		if(oldSelection != -1) {
			if(!packetPlace.getValue())
				mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSelection));
			else
				InventoryUtil.switchToSlot(oldSelection);
		}
		oldSelection = -1;
		burrowing = false;
	}

	// No Block Push
	@EventHandler
	public Listener<BlockPushEvent> onBurrowPush = new Listener<>(event -> {
		if (preventBlockPush.getValue())
			event.cancel();
	});

	@Override
	public void onUpdate() {
		if (nullCheck())
			return;

		if (mode.getValue().equalsIgnoreCase("Double") || mode.getValue().equalsIgnoreCase("Normal")) {
			if (mc.player.posY >= playerPos.getY() + 1.12) {
				burrow();
			}
		}
	}

	private void burrow() {
		//place block where the player was before jumping
		switchToBlock();

		BlockUtils.placeBlock(playerPos, EnumHand.MAIN_HAND, rotate.getValue(), packetPlace.getValue(), sneak.getValue());

		//tries to produce a rubberband
		rubberBand();

		//disable module
		setEnabled(false);
	}

	public void fakeJump() {
		mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.40, mc.player.posZ, true));
		mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.75, mc.player.posZ, true));
		mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.01, mc.player.posZ, true));
		mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.15, mc.player.posZ, true));
	}

	private boolean check() {
		if (mc.world.getBlockState(playerPos).getBlock() == getCurrBlock() || mc.world.getBlockState(playerPos).getBlock() == getBackBlock()) {
			setEnabled(false);
			return true;
		}

		if (InventoryUtil.amountBlockInHotbar(getCurrBlock()) <= 0 && InventoryUtil.amountBlockInHotbar(getBackBlock()) <= 0) {
			setEnabled(false);
			return true;
		}
		return false;
	}

	private void jump() {
		if(mode.getValue().equalsIgnoreCase("Normal") || mode.getValue().equalsIgnoreCase("Double"))
			mc.player.jump();
		else if(mode.getValue().equalsIgnoreCase("Packet"))
			fakeJump();
	}

	private void rubberBand() {
		if(mode.getValue().equalsIgnoreCase("Packet") || mode.getValue().equalsIgnoreCase("Normal"))
			mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + fakeClipHeight.getValue(), mc.player.posZ, false));
		else if(mode.getValue().equalsIgnoreCase("Double"))
			mc.player.jump();
	}
}
