package me.leon.trinity.hacks.combat;

import me.leon.trinity.events.main.EventPacketRecieve;
import me.leon.trinity.events.main.EventPacketSend;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.sub.SubBoolean;
import me.leon.trinity.setting.settings.sub.SubMode;
import me.leon.trinity.setting.settings.sub.SubSlider;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.entity.PlayerUtils;
import me.leon.trinity.utils.rendering.Tessellator;
import me.leon.trinity.utils.world.Timer;
import me.leon.trinity.utils.world.WorldUtils;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AutoCrystal extends Module {
    // Settings
    public static SettingParent main = new SettingParent("Main", true, false);

    public static SettingParent Place = new SettingParent("Place", true, true);
    public static SubSlider placeDelay = new SubSlider("Place Delay", Place, 0, 5, 20, true);
    public static SubMode placeMode = new SubMode("Place Mode", Place, "Closest", "Closest", "Damage");
    public static SubBoolean rayTracePlace = new SubBoolean("RayTrace", Place, false);
    public static SubBoolean bounds = new SubBoolean("Bounds", Place, true);
    public static SubBoolean packetPlace = new SubBoolean("Packet Place", Place, true);
    public static SubMode switchPlace = new SubMode("Switch", Place, "None", "None", "Packet", "Normal");
    public static SubBoolean packetSwingPlace = new SubBoolean("Packet Swing", Place, false);
    public static SubMode swingArmPlace = new SubMode("Swing Arm", Place, "Mainhand", "Mainhand", "Offhand", "Both", "None");
    public static SubSlider placeRange = new SubSlider("Range", Place, 0.5, 5.2, 6, false);
    public static SubSlider placeRangeWalls = new SubSlider("Walls Range", Place, 0.5, 5.2, 6, false);
    public static SubSlider maxSelfDamagePlace = new SubSlider("Max Self Damage", Place, 0, 5, 20, false);
    public static SubSlider minTargetDamagePlace = new SubSlider("Min Target Damage", Place, 0, 5, 20, false);

    public static SettingParent Break = new SettingParent("Break", true, true);
    public static SubSlider breakDelay = new SubSlider("Break Delay", Break, 0, 5, 20, true);
    public static SubBoolean rayTraceBreak = new SubBoolean("RayTrace", Break, false);
    public static SubMode breakMode = new SubMode("Break Mode", Break, "Smart", "Smart", "All");
    public static SubBoolean packetBreak = new SubBoolean("Packet Break", Break, true);
    public static SubBoolean packetSwingBreak = new SubBoolean("Packet Swing", Break, false);
    public static SubMode swingArmBreak = new SubMode("Swing Arm", Break, "Mainhand", "Mainhand", "Offhand", "Both", "None");
    public static SubSlider breakRange = new SubSlider("Range", Break, 0.5, 5.2, 6, false);
    public static SubSlider breakRangeWalls = new SubSlider("Walls Range", Break, 0.5, 5.2, 6, false);
    public static SubSlider maxSelfDamageBreak = new SubSlider("Max Self Damage", Break, 0, 5, 20, false);
    public static SubSlider minTargetDamageBreak = new SubSlider("Min Target Damage", Break, 0, 5, 20, false);
    public static SubSlider breakAttempts = new SubSlider("Break Attempts", Break, 1, 5, 10, true);
    public static SubMode syncMode = new SubMode("Sync", Break, "Instant", "Sound", "Instant", "None");

    public static SettingParent logic = new SettingParent("Logic", true, false);
    public static SubMode logicMode = new SubMode("Logic Mode", logic, "BreakPlace", "PlaceBreak", "BreakPlace");
    public static SubMode version = new SubMode("Version", logic, "1.12.2", "1.13+", "1.12.2");

    public static SettingParent timing = new SettingParent("Timing", true, false);
    public static SubMode timingMode = new SubMode("Mode", timing, "Tick", "Thread", "Tick", "Sequential", "Fast");

    public static SettingParent targeting = new SettingParent("Targeting", true, false);
    public static SubMode targetingMode = new SubMode("Mode", targeting, "Closest", "Closest", "Lowest Health", "Highest Health");
    public static SubSlider targetRange = new SubSlider("Range", targeting, 0.5, 5, 10, false);
    public static SubBoolean players = new SubBoolean("Players", targeting, true);
    public static SubBoolean friends = new SubBoolean("Friends", targeting, true);
    public static SubBoolean neutral = new SubBoolean("Neutral", targeting, true);
    public static SubBoolean passive = new SubBoolean("Passive", targeting, true);
    public static SubBoolean hostile = new SubBoolean("Hostile", targeting, true);

    // Variables
    private static ArrayList<EntityEnderCrystal> placedCrystals;
    private static crystalPosition curPosPlace;
    private static EntityEnderCrystal curBreakCrystal;
    public static EntityLivingBase target;
    private static Timer breakTimer = new Timer();
    private static Timer placeTimer = new Timer();

    public AutoCrystal() {
        super("AutoCrystal", "Nagasaki", Category.COMBAT);
        placedCrystals = new ArrayList<>();
    }


    @SubscribeEvent
    public void onFast(TickEvent event) {
        try {
            if (nullCheck()) return;
            if (!timingMode.getValue().equalsIgnoreCase("Fast")) return;

            target = EntityUtils.getTarget(players.getValue(), neutral.getValue(), friends.getValue(), hostile.getValue(), passive.getValue(), targetRange.getValue(), EntityUtils.toMode(targetingMode.getValue()));
            autoCrystal();
        } catch (Exception e) {

        }
    }

    @Override
    public void onUpdate() {
        if(nullCheck()) return;
        if(!timingMode.getValue().equalsIgnoreCase("Tick") && !timingMode.getValue().equalsIgnoreCase("Sequential")) return;

        target = EntityUtils.getTarget(players.getValue(), neutral.getValue(), friends.getValue(), hostile.getValue(), passive.getValue(), targetRange.getValue(), EntityUtils.toMode(targetingMode.getValue()));
        autoCrystal();
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(curPosPlace == null) return;
        Tessellator.drawBBOutline(curPosPlace.base, 3, new Color(255, 255, 255, 255));
    }

    /**
     * the part that goes kaboom
     */
    private void autoCrystal() {
        switch (logicMode.getValue()) {
            case "BreakPlace": {
                mapPlacePosition();
                placeCrystal();
                breakCrystals();
                break;
            }
            case "PlaceBreak": {
                breakCrystals();
                mapPlacePosition();
                placeCrystal();
                break;
            }
        }
    }

    private void mapPlacePosition() {
        if(target == null)
        {
            clean();
            return;
        }

        if(!Place.getValue()) return;
        if(!placeTimer.hasPassed((int) (placeDelay.getValue() * 50))) {
            return;
        }
        placeTimer.reset();

        final boolean oneThirteen = version.getValue().equals("1.13+");
        List<crystalPosition> poss = WorldUtils.getSphere(PlayerUtils.getPlayerPosFloored(), (float) placeRange.getValue(), (int) placeRange.getValue(), false, true, 0).stream()
                .filter(pos -> canPlaceCrystal(pos, oneThirteen))
                .map(pos -> new crystalPosition(WorldUtils.calculateDamage(pos.x + 0.5, pos.y + 1, pos.z + 0.5, mc.player), WorldUtils.calculateDamage(pos.x + 0.5, pos.y + 1, pos.z + 0.5, target), pos))
                .collect(Collectors.toList());

        if(placeMode.getValue().equalsIgnoreCase("Closest")) {
            crystalPosition pos0 = null;
            for(crystalPosition pos : poss) {
                if(pos.self <= maxSelfDamagePlace.getValue()) {
                    if(pos.damage > minTargetDamagePlace.getValue()) {
                        if(pos0 == null) {
                            pos0 = pos;
                        }
                        if(pos.getCrystalVec().distanceTo(target.getPositionVector().add(0, target.height / 2, 0)) < pos0.getCrystalVec().distanceTo(target.getPositionVector().add(0, target.height / 2, 0))) {
                            pos0 = pos;
                        }
                    }
                }
            }
            curPosPlace = pos0;
        } else
        if(placeMode.getValue().equalsIgnoreCase("Damage")) {
            crystalPosition pos0 = null;
            for(crystalPosition pos : poss) {
                if(pos.self <= maxSelfDamagePlace.getValue()) {
                    if(pos.damage > minTargetDamagePlace.getValue()) {
                        if(pos0 == null) {
                            pos0 = pos;
                        }
                        if(pos0.damage < pos.damage) {
                            pos0 = pos;
                        }
                    }
                }
            }
            curPosPlace = pos0;
        }
    }

    private void breakCrystals() {
        if(!Break.getValue()) return;
        if(!breakTimer.hasPassed((int) (breakDelay.getValue() * 50))) {
            return;
        }
        breakTimer.reset();

        List<Entity> entities = mc.world.loadedEntityList.stream()
                .filter(this::isValidBreak)
                .collect(Collectors.toList());

        if(breakMode.getValue().equalsIgnoreCase("Smart")) {
            if(target == null)
            {
                clean();
                return;
            }
            for(Entity entity : entities) {
                if(WorldUtils.calculateDamage(entity.posX + 0.5, entity.posY + 1, entity.posZ, mc.player) <= maxSelfDamageBreak.getValue()) {
                    if(WorldUtils.calculateDamage(entity.posX + 0.5, entity.posY + 1, entity.posZ + 0.5, target) > minTargetDamageBreak.getValue()) {
                        curBreakCrystal = (EntityEnderCrystal) entity;
                        for(int a = 0; a < breakAttempts.getValue(); a++) {
                            if(packetBreak.getValue())
                                mc.player.connection.sendPacket(new CPacketUseEntity(curBreakCrystal));
                            else
                                mc.playerController.attackEntity(mc.player, curBreakCrystal);
                        }

                        if(syncMode.getValue().equalsIgnoreCase("Instant")) {
                            entity.setDead();
                        }

                        switch (swingArmBreak.getValue()) {
                            case "Mainhand": {
                                if(packetSwingBreak.getValue())
                                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                                else
                                    mc.player.swingArm(EnumHand.MAIN_HAND);
                                break;
                            }
                            case "Offhand": {
                                if(packetSwingBreak.getValue())
                                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                                else
                                    mc.player.swingArm(EnumHand.OFF_HAND);
                                break;
                            }
                            case "Both": {
                                if(packetSwingBreak.getValue()) {
                                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                                }
                                else {
                                    mc.player.swingArm(EnumHand.MAIN_HAND);
                                    mc.player.swingArm(EnumHand.OFF_HAND);
                                }
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        } else
        if(breakMode.getValue().equalsIgnoreCase("All")) {
            for(Entity entity : entities) {
                curBreakCrystal = (EntityEnderCrystal) entity;
                for(int a = 0; a < breakAttempts.getValue(); a++) {
                    if(packetBreak.getValue())
                        mc.player.connection.sendPacket(new CPacketUseEntity(curBreakCrystal));
                    else
                        mc.playerController.attackEntity(mc.player, curBreakCrystal);
                }

                switch (swingArmBreak.getValue()) {
                    case "Mainhand": {
                        if(packetSwingBreak.getValue())
                            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        else
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                        break;
                    }
                    case "Offhand": {
                        if(packetSwingBreak.getValue())
                            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                        else
                            mc.player.swingArm(EnumHand.OFF_HAND);
                        break;
                    }
                    case "Both": {
                        if(packetSwingBreak.getValue()) {
                            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        }
                        else {
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            mc.player.swingArm(EnumHand.OFF_HAND);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }
        }
    }

    private void placeCrystal() {
        if(target == null)
        {
            clean();
            return;
        }
        if(!Place.getValue()) return;

        if(curPosPlace == null) return;
        int current = mc.player.inventory.currentItem;

        if(!switchPlace.getValue().equalsIgnoreCase("None")) {
            if(!InventoryUtil.switchTo(Items.END_CRYSTAL)) return;
            InventoryUtil.switchTo(Items.END_CRYSTAL);
        } else if(switchPlace.getValue().equalsIgnoreCase("None")) {
            if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                return;
            }
        }

        place(curPosPlace.base, ((mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND), packetPlace.getValue());

        switch (swingArmPlace.getValue()) {
            case "Mainhand": {
                if(packetSwingPlace.getValue())
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                else
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                break;
            }
            case "Offhand": {
                if(packetSwingPlace.getValue())
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                else
                    mc.player.swingArm(EnumHand.OFF_HAND);
                break;
            }
            case "Both": {
                if(packetSwingPlace.getValue()) {
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                }
                else {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.swingArm(EnumHand.OFF_HAND);
                }
                break;
            }
            default: {
                break;
            }
        }

        if(switchPlace.getValue().equalsIgnoreCase("Packet")) {
            mc.player.inventory.currentItem = current;
            mc.playerController.updateController();
        }
    }

    @EventHandler
    private final Listener<EventPacketSend> sendListener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity) event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && ((CPacketUseEntity) event.getPacket()).getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
            if (syncMode.getValue().equalsIgnoreCase("Instant"))
                Objects.requireNonNull(((CPacketUseEntity) event.getPacket()).getEntityFromWorld(mc.world)).setDead();
        }
    });

    @EventHandler
    private final Listener<EventPacketRecieve> recieveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketSpawnObject && ((SPacketSpawnObject) event.getPacket()).getType() == 51 && timingMode.getValue().equalsIgnoreCase("Sequential") && Break.getValue()) {
            if (mc.player.getDistance(((SPacketSpawnObject) event.getPacket()).getX(), ((SPacketSpawnObject) event.getPacket()).getY(), ((SPacketSpawnObject) event.getPacket()).getZ()) > breakRange.getValue())
                return;

            attackCrystal(((SPacketSpawnObject) event.getPacket()).getEntityID());
        }
    });






    public static void attackCrystal(int entityId) {
        CPacketUseEntity sequentialCrystal = new CPacketUseEntity();
        sequentialCrystal.entityId = entityId;
        sequentialCrystal.action = CPacketUseEntity.Action.ATTACK;
        mc.player.connection.sendPacket(sequentialCrystal);
    }

    private void clean() {
        curPosPlace = null;
        curBreakCrystal = null;
        placedCrystals.clear();
        breakTimer.reset();
        placeTimer.reset();
    }

    private boolean isValidBreak(Entity entity) {
        if(!(entity instanceof EntityEnderCrystal)) return false;
        if(entity.isDead) return false;
        if(EntityUtils.getRange(entity) > breakRange.getValue()) return false;
        return true;
    }

    private boolean canPlaceCrystal(BlockPos blockPos, boolean oneThirteen) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        if(!oneThirteen) {
            if(mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                    || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN
                    && mc.world.getBlockState(boost).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR)
            {
                for(Entity en : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if(!(en instanceof EntityEnderCrystal)) {
                        return false;
                    }
                }
                for(Entity en : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                    if(!(en instanceof EntityEnderCrystal)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        else {
            if(mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                    || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
            {
                for(Entity en : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if(!(en instanceof EntityEnderCrystal)) {
                        return false;
                    }
                }
                for(Entity en : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                    if(!(en instanceof EntityEnderCrystal)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    private boolean place(BlockPos pos, EnumHand hand, boolean packet) {
        Block block = mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockObsidian) && !(block == Blocks.BEDROCK)) {
            return false;
        }

        EnumFacing side = getEnumFacing(bounds.getValue(), pos);

        float f = (float)(pos.x + 0.5);
        float f1 = (float)(pos.y + 0.5);
        float f2 = (float)(pos.z + 0.5);

        if(packet) {
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, side, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, side, new Vec3d(f, f1, f2), hand);
        }
        return true;
    }

    public static EnumFacing getEnumFacing(boolean rayTrace, BlockPos placePosition) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(placePosition.getX() + 0.5, placePosition.getY() - 0.5, placePosition.getZ() + 0.5));

        if (placePosition.getY() == 255)
            return EnumFacing.DOWN;

        if (rayTrace) {
            return (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        }

        return EnumFacing.UP;
    }

    private class crystalPosition {
        private float self;
        private float damage;
        private BlockPos base;

        private crystalPosition(float self, float target, BlockPos base) {
            this.self = self;
            this.damage = target;
            this.base = base;
        }

        public float getSelf() {
            return self;
        }

        public void setSelf(float self) {
            this.self = self;
        }

        public float getDamage() {
            return damage;
        }

        public void setDamage(float damage) {
            this.damage = damage;
        }

        private void setBase(BlockPos pos) {
            this.base = pos;
        }

        private BlockPos getBase() {
            return base;
        }

        private Vec3d getCrystalVec() {
            return new Vec3d(base.x + 0.5, base.y + 1, base.z + 0.5);
        }
    }
}
