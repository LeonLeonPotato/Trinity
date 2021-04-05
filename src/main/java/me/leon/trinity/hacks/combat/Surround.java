package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.exploits.PacketFly;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.utils.entity.BlockUtils;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.entity.PlayerUtils;
import me.leon.trinity.utils.rendering.Tessellator;
import me.leon.trinity.utils.world.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static me.leon.trinity.utils.world.WorldUtils.empty;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Surround extends Module {
    public static Boolean rotate = new Boolean("Rotate", false);
    public static Boolean packet = new Boolean("Packet", true);
    public static Boolean shift = new Boolean("Shift Only", true);
    public static Mode center = new Mode("Center Mode", "Move", "Move", "Teleport", "None");
    public static Slider bps = new Slider("Blocks Per Tick", 1, 3, 8, true);

    public Surround() {
        super("Surround", "Surrounds your feet in obsidian", Category.COMBAT);
    }

    private BlockPos draw;

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(draw != null) {
            Tessellator.drawGradientAlphaCubeOutline(new AxisAlignedBB(draw), 3, new Color(255, 0, 255, 255));
        }
    }

    @Override
    public void onUpdate() {
        if(nullCheck()) return;
        if(shift.getValue() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
            this.draw = null;
            return;
        }
        if(ModuleManager.getMod(PacketFly.class).isEnabled()) {
            this.draw = null;
            return;
        }
        final BlockPos playerPos = PlayerUtils.getPlayerPosFloored();
        int placed = 0;
        final int oldslot = mc.player.inventory.currentItem;
        final ArrayList<BlockPos> blocks = EntityUtils.getPos(0, 0, 0, mc.player);

        if(blocks.size() == 2 && center.getValue().equalsIgnoreCase("None")) {
            BlockPos pos2 = blocks.get(1);
            BlockPos pos = blocks.get(0);

            InventoryUtil.switchToSlot(InventoryUtil.findHotbarBlock(BlockObsidian.class));

            final BlockPos[] offsets = {
                    pos.north(), pos.south(), pos.east(), pos.west()
            };
            final BlockPos[] offsets2 = {
                    pos2.north(), pos2.south(), pos2.east(), pos2.west()
            };

            if(empty.contains(WorldUtils.getBlock(pos2.down()))) {
                this.draw = pos2.down();
                BlockUtils.placeBlock(pos2.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                placed++;
            }

            if(placed > bps.getValue() - 1) return;

            if(empty.contains(WorldUtils.getBlock(pos.down()))) {
                this.draw = pos.down();
                BlockUtils.placeBlock(pos.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                placed++;
            }

            for(BlockPos off : offsets) {
                if(placed > bps.getValue() - 1) continue;
                Block block = WorldUtils.getBlock(off);
                Block block1 = WorldUtils.getBlock(off.down());
                if(empty.contains(block1) && !intersectsWithEntity(off.down())) {
                    this.draw = off.down();
                    BlockUtils.placeBlock(off.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    placed++;
                }
                if(placed > bps.getValue() - 1) continue;
                if(empty.contains(block) && !intersectsWithEntity(off)) {
                    this.draw = off;
                    BlockUtils.placeBlock(off, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    placed++;
                }
            }
            for(BlockPos off : offsets2) {
                if(placed > bps.getValue() - 1) continue;
                Block block = WorldUtils.getBlock(off);
                Block block1 = WorldUtils.getBlock(off.down());
                if(empty.contains(block1) && !intersectsWithEntity(off.down())) {
                    this.draw = off.down();
                    BlockUtils.placeBlock(off.down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    placed++;
                }
                if(placed > bps.getValue() - 1) continue;
                if(empty.contains(block) && !intersectsWithEntity(off)) {
                    this.draw = off;
                    BlockUtils.placeBlock(off, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                    placed++;
                }
            }
        } else {
            Vec3d Center = new Vec3d(PlayerUtils.getPlayerPosFloored().getX() + 0.5, PlayerUtils.getPlayerPosFloored().getY(), PlayerUtils.getPlayerPosFloored().getZ() + 0.5);

            if(center.getValue().equalsIgnoreCase("Move")) {
                double l_XDiff = Math.abs(Center.x - mc.player.posX);
                double l_ZDiff = Math.abs(Center.z - mc.player.posZ);

                if (l_XDiff <= 0.1 && l_ZDiff <= 0.1)
                {
                    Center = new Vec3d(0,0,0);
                }
                else
                {
                    double l_MotionX = Center.x - mc.player.posX;
                    double l_MotionZ = Center.z - mc.player.posZ;

                    mc.player.motionX = l_MotionX/2;
                    mc.player.motionZ = l_MotionZ/2;
                }
            } else
            if(center.getValue().equalsIgnoreCase("Teleport")) {
                mc.player.setPosition(Center.x, Center.y, Center.z);
                mc.player.connection.sendPacket(new CPacketPlayer.Position(Center.x, Center.y, Center.z, mc.player.onGround));
            }

            InventoryUtil.switchToSlot(InventoryUtil.findHotbarBlock(BlockObsidian.class));
            for(BlockPos pos : this.pos) {
                if(placed > bps.getValue() - 1) continue;
                if(empty.contains(WorldUtils.getBlock(playerPos.add(pos).down())) && !intersectsWithEntity(pos.down())){
                    this.draw = playerPos.add(pos).down();
                    placed++;
                    BlockUtils.placeBlock(playerPos.add(pos).down(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                }
                if(placed > bps.getValue() - 1) continue;
                if(empty.contains(WorldUtils.getBlock(playerPos.add(pos))) && !intersectsWithEntity(pos)) {
                    this.draw = playerPos.add(pos);
                    placed++;
                    BlockUtils.placeBlock(playerPos.add(pos), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), true);
                }
            }
        }
        if(placed == 0) {
            this.draw = null;
        }
        InventoryUtil.switchToSlot(oldslot);

    }

    private final BlockPos[] pos = {
            new BlockPos(0, 0, 1),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, -1),
            new BlockPos(-1, 0, 0)
    };

    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) continue;
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) return true;
        }
        return false;
    }
}
