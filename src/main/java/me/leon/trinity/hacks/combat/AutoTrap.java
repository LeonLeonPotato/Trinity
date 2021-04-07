package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.setting.settings.sub.SubBoolean;
import me.leon.trinity.setting.settings.sub.SubMode;
import me.leon.trinity.setting.settings.sub.SubSlider;
import me.leon.trinity.utils.entity.BlockUtils;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.misc.MessageBus;
import me.leon.trinity.utils.world.HoleUtils;
import me.leon.trinity.utils.world.WorldUtils;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class AutoTrap extends Module {
    public static Boolean       antiStep = new Boolean("AntiStep", true);
    public static Slider        bps = new Slider("Blocks per Tick", 1, 4, 8, true);
    public static Slider        placeRange = new Slider("PlaceRage", 0, 5.5, 7, false);
    public static Boolean       antiGlitch = new Boolean("NoGlitch", true);
    public static Boolean       packet = new Boolean("Packet", false);
    public static Boolean       rotate = new Boolean("Rotate", false);
    public static Boolean       strict = new Boolean("Strict", false);
    public static Boolean       onlyInHole = new Boolean("OnlyInHole", true);
    public static Boolean       toggle = new Boolean("Toggle", false);

    public static SettingParent targeting = new SettingParent("Targeting", true, false);
    public static SubMode       targetingMode = new SubMode("Mode", targeting, "Closest", "Closest", "Lowest Health", "Highest Health");
    public static SubSlider     targetRange = new SubSlider("Range", targeting, 0.5, 5, 12, false);
    public static SubBoolean    players = new SubBoolean("Players", targeting, true);
    public static SubBoolean    friends = new SubBoolean("Friends", targeting, true);
    public static SubBoolean    neutral = new SubBoolean("Neutral", targeting, true);
    public static SubBoolean    passive = new SubBoolean("Passive", targeting, true);
    public static SubBoolean    hostile = new SubBoolean("Hostile", targeting, true);

    public AutoTrap() {
        super("AutoTrap", "Automatically traps your target", Category.COMBAT);
    }

    public static EntityLivingBase target;

    @Override
    public void onUpdate() {
        if(nullCheck()) return;
        target = EntityUtils.getTarget(players.getValue(), neutral.getValue(), friends.getValue(), hostile.getValue(), passive.getValue(), targetRange.getValue(), EntityUtils.toMode(targetingMode.getValue()));

        if(target == null) {
            if(toggle.getValue()) toggle0("Target not found!");
            return;
        }
        autoTrap();
    }

    private void autoTrap() {
        final HoleUtils.Hole hole = HoleUtils.getHole(EntityUtils.getEntityPosFloored(target), -1);
        final boolean inHole = EntityUtils.isInHole(target);

        if(!inHole && onlyInHole.getValue() && hole == null) {
            if(toggle.getValue()) toggle0("Target not in Hole!");
            return;
        }

        int old = mc.player.inventory.currentItem;

        if(!InventoryUtil.switchTo(Item.getItemFromBlock(Blocks.OBSIDIAN))) {
            if(!InventoryUtil.switchTo(Item.getItemFromBlock(Blocks.ENDER_CHEST))) {
                toggle0("Cannot find any blocks!");
                return;
            }
        }

        if(hole instanceof HoleUtils.DoubleHole || !onlyInHole.getValue() && EntityUtils.getPos(0, 0, 0, target).size() == 2) {
            BlockPos pos = null;
            BlockPos pos1 = null;
            if(!inHole) {
                final ArrayList<BlockPos> targetPoses = EntityUtils.getPos(0, 0, 0, target);
                pos = targetPoses.get(0);
                pos1 = targetPoses.get(1);
            } else if(hole instanceof HoleUtils.DoubleHole) {
                pos = ((HoleUtils.DoubleHole) hole).pos1;
                pos1 = ((HoleUtils.DoubleHole) hole).pos;
            }

            if(pos == null) return;

            int placed = 0;
            BlockPos medPos = null;
            boolean need = true;

            ArrayList<BlockPos> possible = new ArrayList<>();

            for(int[] add : trapBlocks) {
                final BlockPos pos0 = pos.add(add[0], add[1], add[2]);
                final BlockPos pos3 = pos.add(add[0], add[1] + 1, add[2]);
                if(!pos3.equals(pos1.add(0, 2, 0))) {
                    possible.add(pos3);
                }
                if(pos0.equals(pos1.add(0, 1, 0))) {
                    continue;
                }
                if(WorldUtils.getBlock(pos0) instanceof BlockEnderChest || WorldUtils.getBlock(pos0) instanceof BlockObsidian) {
                    continue;
                }
                if(placed >= bps.getValue()) {
                    continue;
                }
                if(pos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) > Math.pow(placeRange.getValue(), 2)) continue;
                BlockUtils.placeBlock(pos0, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
                placed++;
            }

            if(placed >= bps.getValue()) return;

            for(int[] add : trapBlocks) {
                final BlockPos pos0 = pos1.add(add[0], add[1], add[2]);
                final BlockPos pos3 = pos1.add(add[0], add[1] + 1, add[2]);
                if(!pos3.equals(pos.add(0, 2, 0))) {
                    possible.add(pos3);
                }
                if(pos0.equals(pos.add(0, 1, 0))) {
                    continue;
                }
                if(WorldUtils.getBlock(pos0) instanceof BlockEnderChest || WorldUtils.getBlock(pos0) instanceof BlockObsidian) {
                    continue;
                }
                if(placed >= bps.getValue()) {
                    continue;
                }
                if(pos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) > Math.pow(placeRange.getValue(), 2)) continue;
                BlockUtils.placeBlock(pos0, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
                placed++;
            }

            if(placed >= bps.getValue()) return;

            for(BlockPos medPos0 : possible) {
                if(!WorldUtils.empty.contains(WorldUtils.getBlock(medPos0))) {
                    need = false;
                } else if(!EntityUtils.isIntercepted(medPos0)){
                    if(medPos == null) {
                        medPos = medPos0;
                    } else {
                        if(medPos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) < medPos.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ)) {
                            medPos = medPos0;
                        }
                    }
                }
            }

            final BlockPos pos3 = pos.add(0, 2, 0);
            final BlockPos pos4 = pos1.add(0, 2, 0);

            if(need && medPos != null && WorldUtils.empty.contains(WorldUtils.getBlock(pos3)) && WorldUtils.empty.contains(WorldUtils.getBlock(pos4))) {
                if(medPos.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
                    BlockUtils.placeBlock(medPos, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
                    placed++;
                }
            }

            if(placed >= bps.getValue()) return;

            if(!(WorldUtils.getBlock(pos3) instanceof BlockEnderChest) && !(WorldUtils.getBlock(pos3) instanceof BlockObsidian)) {
                if(pos3.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
                    BlockUtils.placeBlock(pos3, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
                    placed++;
                }
            }

            if(placed >= bps.getValue()) return;

            if(!(WorldUtils.getBlock(pos4) instanceof BlockEnderChest) && !(WorldUtils.getBlock(pos4) instanceof BlockObsidian)) {
                if(pos4.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
                    BlockUtils.placeBlock(pos4, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
                    placed++;
                }
            }

            if(!antiStep.getValue()) return;
            if(placed >= bps.getValue()) return;

            if(!(WorldUtils.getBlock(pos3.add(0, 1, 0)) instanceof BlockEnderChest) && !(WorldUtils.getBlock(pos3.add(0, 1, 0)) instanceof BlockObsidian)) {
                if(pos3.add(0, 1, 0).distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
                    BlockUtils.placeBlock(pos3.add(0, 1, 0), rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
                    placed++;
                }
            }

            if(placed >= bps.getValue()) return;

            if(!(WorldUtils.getBlock(pos4.add(0, 1, 0)) instanceof BlockEnderChest) && !(WorldUtils.getBlock(pos4.add(0, 1, 1)) instanceof BlockObsidian)) {
                if(pos4.add(0, 1, 0).distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2))
                    BlockUtils.placeBlock(pos4.add(0, 1, 0), rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
            }

        } else if(hole instanceof HoleUtils.SingleHole || !onlyInHole.getValue() && EntityUtils.getPos(0, 0, 0, target).size() == 1) {
            BlockPos pos = EntityUtils.getEntityPosFloored(target);
            int placed = 0;
            boolean need = true;
            BlockPos medPos = null;

            for(int[] add : trapBlocks) {
                if(hole != null || inHole) continue;
                final BlockPos pos0 = pos.add(add[0], 0, add[2]);
                if(WorldUtils.getBlock(pos0) instanceof BlockEnderChest || WorldUtils.getBlock(pos0) instanceof BlockObsidian) {
                    continue;
                }
                if(placed >= bps.getValue()) {
                    continue;
                }
                if(pos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) > Math.pow(placeRange.getValue(), 2)) continue;
                BlockUtils.placeBlock(pos0, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
                placed++;
            }

            for(int[] add : trapBlocks) {
                final BlockPos pos0 = pos.add(add[0], add[1], add[2]);
                if(WorldUtils.getBlock(pos0) instanceof BlockEnderChest || WorldUtils.getBlock(pos0) instanceof BlockObsidian) {
                    continue;
                }
                if(placed >= bps.getValue()) {
                    continue;
                }
                if(pos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) > Math.pow(placeRange.getValue(), 2)) continue;
                BlockUtils.placeBlock(pos0, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
                placed++;
            }

            for(int[] add : trapBlocks) {
                final BlockPos pos0 = pos.add(add[0], add[1] + 1, add[2]);
                if(!WorldUtils.empty.contains(WorldUtils.getBlock(pos0))) {
                    need = false;
                } else if(!EntityUtils.isIntercepted(pos0)){
                    if(medPos == null) {
                        medPos = pos0;
                    } else {
                        if(pos0.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) < medPos.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ)) {
                            medPos = pos0;
                        }
                    }
                }
            }

            if(placed >= bps.getValue()) return;

            if(need && medPos != null && WorldUtils.empty.contains(WorldUtils.getBlock(pos.add(0, 2, 0)))) {
                if(medPos.distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
                    BlockUtils.placeBlock(medPos, rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
                    placed++;
                }
            }

            if(placed >= bps.getValue()) return;

            if(WorldUtils.empty.contains(WorldUtils.getBlock(pos.add(0, 2, 0)))) {
                if(pos.add(0, 2, 0).distanceSq(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ) <= Math.pow(placeRange.getValue(), 2)) {
                    BlockUtils.placeBlock(pos.add(0, 2, 0), rotate.getValue(), strict.getValue(), true, packet.getValue(), true, antiGlitch.getValue());
                }
            }
        }

        InventoryUtil.switchToSlot(old);
    }

    private void toggle0(String message) {
        MessageBus.sendClientMessage(message, true);
        this.setEnabled(false);
    }

    private final int[][] trapBlocks = {
            {0, 1, -1},
            {-1, 1, 0},
            {0, 1, 1},
            {1, 1, 0},
    };
}
