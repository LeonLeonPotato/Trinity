package me.leon.trinity.hacks.combat.autocrystal;

import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.world.RaytraceUtils;
import me.leon.trinity.utils.world.WorldUtils;
import me.zero.alpine.fork.listener.Listenable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.leon.trinity.hacks.combat.AutoCrystal.*;
import static me.leon.trinity.hacks.combat.autocrystal.Utility.*;

public class Breaker implements Util, Listenable {
    private List<Crystal> filtered;
    private Mapper mapper;

    public Breaker() {
        mapper = new Mapper(this);
        filtered = new ArrayList<>();
    }

    public void breakCrystals() {
        if(nullCheck()) return;
        if(!Break.getValue()) return;
        if(!breakTimer.hasPassAndReset((int) breakDelay.getValue() * 50)) return;
        if(getSelfHealth() <= pauseHealth.getValue()) return;

        curBreakCrystal = null;

        if(threadedBreak.getValue()) {
            new Mapper(this).start();
        } else mapper.run();

        for (Crystal crystal : filtered) {
            switch (breakMode.getValue()) {
                case "Only Own":
                case "Smart": {
                    if (target == null) {
                        reset();
                        return;
                    }

                    if(breakMode.getValue().equalsIgnoreCase("Only Own")) {
                        boolean found = false;
                        for(CrystalPosition pos : placedCrystals) {
                            if(pos.getBase().equals(crystal.getBase())) {
                                found = true;
                                break;
                            }
                        }
                        if(!found) continue;
                    }

                    if (filterDamage(crystal)) {
                        curBreakCrystal = crystal;
                        for (int a = 0; a < breakAttempts.getValue(); a++) {
                            attackCrystal(curBreakCrystal);
                        }

                        if (syncMode.getValue().equalsIgnoreCase("Instant")) {
                            curBreakCrystal.getCrystal().setDead();
                        }
                        break;
                    }
                }
                case "All": {
                    curBreakCrystal = crystal;
                    for (int a = 0; a < breakAttempts.getValue(); a++) {
                        attackCrystal(curBreakCrystal);
                    }
                    break;
                }
            }
        }

        if(curBreakCrystal != null) swingHand(true);
    }

    private boolean filterReachable(EntityEnderCrystal crystal) {
        switch (rayTraceBreakMode.getValue()) {
            case "Leon": {
                final Vec3d vec = RaytraceUtils.rayTraceLeon(crystal);
                final boolean walls = (vec == null);
                if(walls)
                {
                    if(WorldUtils.isWithin(breakRangeWalls.getValue(), eyePos(), crystal.getPositionVector()))
                        return true;
                } else
                {
                    if(WorldUtils.isWithin(breakRange.getValue(), eyePos(), vec))
                        return true;
                }
                break;
            }
            case "Offset":
            case "Simple": {
                final boolean walls = RaytraceUtils.rayTraceSimple(crystal.getPositionVector(), rayTraceBreakMode.getValue().equalsIgnoreCase("Offset") ? offsetBreak.getValue() : 0.0D);
                if(walls) {
                    if(WorldUtils.isWithin(breakRangeWalls.getValue(), eyePos(), crystal.getPositionVector()))
                        return true;
                } else {
                    if(WorldUtils.isWithin(breakRange.getValue(), eyePos(), crystal.getPositionVector()))
                        return true;
                }
                break;
            }
        }
        return false;
    }

    private boolean filterDamage(Crystal crystal) {
        if(crystal.getTargetDamage() >= minTargetDamageBreak.getValue()) {
            return crystal.getSelfDamage() < maxSelfDamageBreak.getValue();
        }
        return false;
    }

    public void sequential(SPacketSpawnObject packet) {
        if (mc.world == null) return;
        if (pause) return;
        if (packet.getType() == 51 && timingMode.getValue().equalsIgnoreCase("Sequential") && Break.getValue()) {
            breakCrystals();
        }
    }

    public void soundSync(SPacketSoundEffect packet) {
        if (mc.world == null) return;
        if (pause) return;
        if (syncMode.getValue().equalsIgnoreCase("Sound")) {
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity e : mc.world.loadedEntityList) {
                    if (e instanceof EntityEnderCrystal) {
                        if (e.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) <= 36.0f) {
                            e.setDead();
                        }
                    }
                }
            }
        }
    }

    public void instaSync(CPacketUseEntity packet) {
        if(syncMode.getValue().equalsIgnoreCase("Instant") && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
            packet.getEntityFromWorld(mc.world).setDead();
            mc.world.removeEntityFromWorld(packet.entityId);
        }
    }

    private void attackCrystal(Crystal crystal) {
        rotate(true, crystal.getCrystal().getPositionVector());
        if(packetBreak.getValue()) {
            attackByID(crystal.getCrystal().getEntityId());
        } else {
            mc.playerController.attackEntity(mc.player, crystal.getCrystal());
        }
    }

    private void attackByID(int entityId) {
        CPacketUseEntity sequentialCrystal = new CPacketUseEntity();
        sequentialCrystal.entityId = entityId;
        sequentialCrystal.action = CPacketUseEntity.Action.ATTACK;
        mc.player.connection.sendPacket(sequentialCrystal);
    }

    private static class Mapper extends Thread {
        private final Breaker breaker;

        public Mapper(Breaker breaker) {
            this.breaker = breaker;
        }

        @Override
        public void run() {
            breaker.filtered = mc.world.loadedEntityList.stream()
                    .filter(e -> e instanceof EntityEnderCrystal)
                    .map(e -> (EntityEnderCrystal) e)
                    .filter(breaker::filterReachable)
                    .map(Crystal::new)
                    .collect(Collectors.toList());
        }
    }
}