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
import me.leon.trinity.utils.world.RaytraceUtils;
import me.leon.trinity.utils.world.Timer;
import me.leon.trinity.utils.world.WorldUtils;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class AutoCrystal extends Module {
    // Settings
    public static SettingParent main = new SettingParent("Main", true, false);
    public static SubSlider iterations = new SubSlider("Iterations", main, 1, 1, 3, true);
    public static SubMode nosuicide = new SubMode("NoSuicide", main, "Both", "Place", "Destroy", "None", "Both");
    public static SubSlider minHealth = new SubSlider("Pause Health", main, 0, 6, 20, true);

    public static SettingParent Place = new SettingParent("Place", true, true);
    public static SubMode       placeMode = new SubMode("Place Mode", Place, "Closest", "Closest", "Damage");
    public static SubSlider     placeDelay = new SubSlider("Place Delay", Place, 0, 2, 20, true);
    public static SubSlider     placeRange = new SubSlider("Range", Place, 0.5, 5.0, 6, false);
    public static SubSlider     placeRangeWalls = new SubSlider("Walls Range", Place, 0, 3.5, 6, false);
    public static SubSlider     minTargetDamagePlace = new SubSlider("Min Target Damage", Place, 0, 6, 20, false);
    public static SubSlider     maxSelfDamagePlace = new SubSlider("Max Self Damage", Place, 0, 8, 20, false);
    public static SubMode       rayTracePlaceMode = new SubMode("RayTrace Mode", Place, "Simple", "Leon", "Simple", "Simple-Offset");
    public static SubBoolean    packetPlace = new SubBoolean("Packet Place", Place, true);
    public static SubMode       swingArmPlace = new SubMode("Swing Arm", Place, "Offhand", "Mainhand", "Offhand", "Both", "None");
    public static SubBoolean    packetSwingPlace = new SubBoolean("Packet Swing", Place, false);
    public static SubSlider     offsetPlace = new SubSlider("Offset", Place, 0, 1, 3, false);
    public static SubMode       switchPlace = new SubMode("Switch", Place, "None", "None", "Packet", "Normal");
    public static SubBoolean    multiPlace = new SubBoolean("Multiplace", Place, false);
    public static SubBoolean    bounds = new SubBoolean("Bounds", Place, true);

    public static SettingParent Break = new SettingParent("Break", true, true);
    public static SubMode       breakMode = new SubMode("Break Mode", Break, "Smart", "Smart", "All", "Only Own");
    public static SubSlider     breakDelay = new SubSlider("Break Delay", Break, 0, 4, 20, true);
    public static SubSlider     breakRange = new SubSlider("Range", Break, 0.5, 5.0, 6, false);
    public static SubSlider     breakRangeWalls = new SubSlider("Walls Range", Break, 0, 3.0, 6, false);
    public static SubSlider     minTargetDamageBreak = new SubSlider("Min Target Damage", Break, 0, 6, 20, false);
    public static SubSlider     maxSelfDamageBreak = new SubSlider("Max Self Damage", Break, 0, 8, 20, false);
    public static SubMode       rayTraceBreakMode = new SubMode("RayTrace Mode", Break, "Simple", "Leon", "Simple", "Simple-Offset");
    public static SubBoolean    packetBreak = new SubBoolean("Packet Break", Break, true);
    public static SubMode       swingArmBreak = new SubMode("Swing Arm", Break, "Mainhand", "Mainhand", "Offhand", "Both", "None");
    public static SubBoolean    packetSwingBreak = new SubBoolean("Packet Swing", Break, false);
    public static SubSlider     offsetBreak = new SubSlider("Offset", Break, 0, 1, 3, false);
    public static SubSlider     breakAttempts = new SubSlider("Break Attempts", Break, 1, 3, 10, true);
    public static SubMode       syncMode = new SubMode("Sync", Break, "None", "Sound", "Instant", "None");

    public static SettingParent logic = new SettingParent("Logic", true, false);
    public static SubMode logicMode = new SubMode("Logic Mode", logic, "BreakPlace", "PlaceBreak", "BreakPlace");
    public static SubMode version = new SubMode("Version", logic, "1.12.2", "1.13+", "1.12.2");

    public static SettingParent timing = new SettingParent("Timing", true, false);
    public static SubMode timingMode = new SubMode("Mode", timing, "Tick", "Tick", "Fast");
    public static SubBoolean sequential = new SubBoolean("Sequential", timing, true);
    public static SubBoolean nowait = new SubBoolean("GodMode", timing, false);

    public static SettingParent targeting = new SettingParent("Targeting", true, false);
    public static SubMode targetingMode = new SubMode("Mode", targeting, "Closest", "Closest", "Lowest Health", "Highest Health");
    public static SubSlider targetRange = new SubSlider("Range", targeting, 0.5, 5, 10, false);
    public static SubBoolean players = new SubBoolean("Players", targeting, true);
    public static SubBoolean friends = new SubBoolean("Friends", targeting, true);
    public static SubBoolean neutral = new SubBoolean("Neutral", targeting, true);
    public static SubBoolean passive = new SubBoolean("Passive", targeting, true);
    public static SubBoolean hostile = new SubBoolean("Hostile", targeting, true);

    // Variables
    private static CopyOnWriteArrayList<Integer> placedCrystals;
    private static crystalPosition curPosPlace;
    private static EntityEnderCrystal curBreakCrystal;
    public static EntityLivingBase target;
    private static int highestID = -10000000;
    private static final Timer breakTimer = new Timer();
    private static final Timer placeTimer = new Timer();

    public AutoCrystal() {
        super("AutoCrystal", "Nagasaki", Category.COMBAT);
        placedCrystals = new CopyOnWriteArrayList<>();
    }

    @SubscribeEvent
    public void onFast(TickEvent event) {
        try {
            if (nullCheck()) return;
            if (!timingMode.getValue().equalsIgnoreCase("Fast")) return;

            target = EntityUtils.getTarget(players.getValue(), neutral.getValue(), friends.getValue(), hostile.getValue(), passive.getValue(), targetRange.getValue(), EntityUtils.toMode(targetingMode.getValue()));
            autoCrystal();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onUpdate() {
        if(nullCheck()) return;
        if(!timingMode.getValue().equalsIgnoreCase("Tick")) return;

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
        for(int a = 0; a < iterations.getValue(); a++)
        {
            for(int id : placedCrystals) {
                if(placedCrystals.contains(id) && (mc.world.getEntityByID(id) == null || !(mc.world.getEntityByID(id) instanceof EntityEnderCrystal))) {
                    placedCrystals.remove((Object) id);
                } else
                if(EntityUtils.getRange(Objects.requireNonNull(mc.world.getEntityByID(id))) > 10) {
                    placedCrystals.remove((Object) id);
                }
            }

            updateEntityID();

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
    }

    private void mapPlacePosition() {
        if(target == null)
        {
            clean();
            return;
        }

        if(!Place.getValue()) return;

        final boolean oneThirteen = version.getValue().equals("1.13+");
        List<crystalPosition> poss = WorldUtils.getSphere(PlayerUtils.getPlayerPosFloored(), (float) placeRange.getValue(), (int) placeRange.getValue(), false, true, 0).stream()
                .filter(pos -> crystalCheckPlace(pos, oneThirteen))
                .map(pos -> new crystalPosition(WorldUtils.calculateDamage(pos.x + 0.5, pos.y + 1, pos.z + 0.5, mc.player), WorldUtils.calculateDamage(pos.x + 0.5, pos.y + 1, pos.z + 0.5, target), pos))
                .collect(Collectors.toList());

        if(placeMode.getValue().equalsIgnoreCase("Closest")) {
            crystalPosition pos0 = null;
            for(crystalPosition pos : poss) {
                if(pos.self <= maxSelfDamagePlace.getValue()) {
                    if(pos.damage > minTargetDamagePlace.getValue()) {
                        if(nosuicide.getValue().equalsIgnoreCase("Both") || nosuicide.getValue().equalsIgnoreCase("Place")) {
                            if(pos.self >= mc.player.getHealth() + 1) continue;
                        }

                        if(pos0 == null) {
                            pos0 = new crystalPosition(0, 0, BlockPos.ORIGIN);
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
                        if(nosuicide.getValue().equalsIgnoreCase("Both") || nosuicide.getValue().equalsIgnoreCase("Place")) {
                            if(pos.self >= mc.player.getHealth() + 1) continue;
                        }

                        if(pos0 == null) {
                            pos0 = new crystalPosition(0, 0, BlockPos.ORIGIN);
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

        if(mc.player.getHealth() <= minHealth.getValue()) return;

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
        } else
        if(breakMode.getValue().equalsIgnoreCase("Only Own")) {
            if(target == null)
            {
                clean();
                return;
            }
            for(Entity entity : entities) {
                if(WorldUtils.calculateDamage(entity.posX + 0.5, entity.posY + 1, entity.posZ, mc.player) <= maxSelfDamageBreak.getValue()) {
                    if(WorldUtils.calculateDamage(entity.posX + 0.5, entity.posY + 1, entity.posZ + 0.5, target) > minTargetDamageBreak.getValue()) {
                        if(!placedCrystals.contains(entity.entityId)) {
                            continue;
                        }

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

                        placedCrystals.remove((Object) entity.entityId);

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
        }
    }

    private void placeCrystal() {
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
        if(mc.player.getHealth() <= minHealth.getValue()) return;

        if(curPosPlace == null || curPosPlace.base == BlockPos.ORIGIN) return;
        int current = mc.player.inventory.currentItem;

        if(!switchPlace.getValue().equalsIgnoreCase("None")) {
            if(!InventoryUtil.switchTo(Items.END_CRYSTAL)) return;
            InventoryUtil.switchTo(Items.END_CRYSTAL);
        } else if(switchPlace.getValue().equalsIgnoreCase("None")) {
            if(mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                return;
            }
            mc.playerController.updateController();
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
    private final Listener<EventPacketSend> sendListener0 = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && target != null && curPosPlace != null && ((CPacketPlayerTryUseItemOnBlock) event.getPacket()).position == curPosPlace.base) {
            if(nowait.getValue()) {
                updateEntityID();
                for (int i = 0; i <= iterations.getValue(); ++i) {
                    attackCrystal(highestID + 1);
                }
            }
        }
    });

    @EventHandler
    private final Listener<EventPacketRecieve> recieveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketSpawnObject && ((SPacketSpawnObject) event.getPacket()).getType() == 51 && sequential.getValue() && Break.getValue()) {
            if (mc.player.getDistance(((SPacketSpawnObject) event.getPacket()).getX(), ((SPacketSpawnObject) event.getPacket()).getY(), ((SPacketSpawnObject) event.getPacket()).getZ()) > breakRange.getValue())
                return;

            attackCrystal(((SPacketSpawnObject) event.getPacket()).getEntityID());
        }
    });

    @EventHandler
    private final Listener<EventPacketRecieve> packetReceiveListener = new Listener<>(event -> {
        if(syncMode.getValue().equalsIgnoreCase("Sound")) {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
                if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity e : Minecraft.getMinecraft().world.loadedEntityList) {
                        if (e instanceof EntityEnderCrystal) {
                            if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                                e.setDead();
                            }
                        }
                    }
                }
            }
        }
    });


    @EventHandler
    private final Listener<EventPacketRecieve> recieveListener0 = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            if(mc.world.getEntityByID(((SPacketSpawnObject) event.getPacket()).getEntityID()) instanceof EntityEnderCrystal && curPosPlace != null && target != null) placedCrystals.add(((SPacketSpawnObject) event.getPacket()).getEntityID());
            this.checkID(((SPacketSpawnObject)event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnExperienceOrb) {
            this.checkID(((SPacketSpawnExperienceOrb)event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnPlayer) {
            this.checkID(((SPacketSpawnPlayer)event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnGlobalEntity) {
            this.checkID(((SPacketSpawnGlobalEntity)event.getPacket()).getEntityId());
        } else if (event.getPacket() instanceof SPacketSpawnPainting) {
            this.checkID(((SPacketSpawnPainting)event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnMob) {
            this.checkID(((SPacketSpawnMob)event.getPacket()).getEntityID());
        }
    });








    private void attackCrystal(int entityId) {
        CPacketUseEntity sequentialCrystal = new CPacketUseEntity();
        sequentialCrystal.entityId = entityId;
        sequentialCrystal.action = CPacketUseEntity.Action.ATTACK;
        mc.player.connection.sendPacket(sequentialCrystal);
    }

    private void checkID(int id) {
        if (id > highestID) {
            highestID = id;
        }
    }

    public void updateEntityID() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity.getEntityId() <= highestID) continue;
            highestID = entity.getEntityId();
        }
    }

    private void clean() {
        highestID = -10000000;
        curPosPlace = null;
        curBreakCrystal = null;
        placedCrystals.clear();
        breakTimer.reset();
        placeTimer.reset();
    }

    private boolean isValidBreak(Entity entity) {
        if(nosuicide.getValue().equalsIgnoreCase("Both") || nosuicide.getValue().equalsIgnoreCase("Destroy")) {
            if(mc.player.getHealth() <= WorldUtils.calculateDamage(entity.posX, entity.posY, entity.posZ, mc.player) + 1) return false;
        }
        if(!(entity instanceof EntityEnderCrystal)) return false;
        if(entity.isDead) return false;
        if(EntityUtils.getRange(entity) > breakRange.getValue()) return false;
        if(rayTraceBreakMode.getValue().equalsIgnoreCase("Leon")) {
            Vec3d vec = RaytraceUtils.rayTraceLeon(entity);
            if(vec == null) {
                return EntityUtils.getRange(entity) < breakRangeWalls.getValue();
            }
        } else
        if(rayTracePlaceMode.getValue().equalsIgnoreCase("Simple")) {
            if(!RaytraceUtils.rayTraceSimple(entity)) {
                return EntityUtils.getRange(entity) < breakRangeWalls.getValue();
            }
        } else
        if(rayTracePlaceMode.getValue().equalsIgnoreCase("Simple-Offset")) {
            if(!RaytraceUtils.rayTraceSimple(entity, offsetBreak.getValue())) {
                return EntityUtils.getRange(entity) < breakRangeWalls.getValue();
            }
        }
        return true;
    }

    private boolean canPlaceCrystal(BlockPos blockPos, boolean oneThirteen) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        if(!oneThirteen) {
            if((mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                    || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                    && mc.world.getBlockState(boost).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR)
            {
                for(Entity en : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if(multiPlace.getValue()) {
                        if(!en.isDead) {
                            return false;
                        }
                    } else {
                        if(!en.isDead && !(en instanceof EntityEnderCrystal)) {
                            return false;
                        }
                    }
                }
                for(Entity en : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                    if(multiPlace.getValue()) {
                        if(!en.isDead) {
                            return false;
                        }
                    } else {
                        if(!en.isDead && !(en instanceof EntityEnderCrystal)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        else {
            if(mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                    || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
            {
                for(Entity en : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if(multiPlace.getValue()) {
                        if(!en.isDead) {
                            return false;
                        }
                    } else {
                        if(!en.isDead && !(en instanceof EntityEnderCrystal)) {
                            return false;
                        }
                    }
                }
                for(Entity en : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                    if(multiPlace.getValue()) {
                        if(!en.isDead) {
                            return false;
                        }
                    } else {
                        if(!en.isDead && !(en instanceof EntityEnderCrystal)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
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

    private boolean crystalCheckPlace(BlockPos pos, boolean oneThirteen) {
        if(!canPlaceCrystal(pos, oneThirteen)) return false;
        if(rayTracePlaceMode.getValue().equalsIgnoreCase("Leon")) {
            Vec3d vec = RaytraceUtils.rayTraceLeon(pos);
            if(vec == null) {
                return (WorldUtils.getRange(new Vec3d(pos.x + 0.5, pos.y + 1, pos.z + 0.5)) < placeRangeWalls.getValue());
            }
        } else
        if(rayTracePlaceMode.getValue().equalsIgnoreCase("Simple")) {
            if(!RaytraceUtils.rayTraceSimple(pos)) {
                return (WorldUtils.getRange(new Vec3d(pos.x + 0.5, pos.y + 1, pos.z + 0.5)) < placeRangeWalls.getValue());
            }
        } else
        if(rayTracePlaceMode.getValue().equalsIgnoreCase("Simple-Offset")) {
            if(!RaytraceUtils.rayTraceSimple(target, offsetPlace.getValue())) {
                return (WorldUtils.getRange(new Vec3d(pos.x + 0.5, pos.y + offsetPlace.getValue(), pos.z + 0.5)) < placeRangeWalls.getValue());
            }
        }
        return true;
    }

    public static boolean isRunning() {
        return target != null;
    }

    private static class crystalPosition {
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
