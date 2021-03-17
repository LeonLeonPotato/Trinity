package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.managers.RotationManager;
import me.leon.trinity.managers.TickrateManager;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.sub.SubBoolean;
import me.leon.trinity.setting.settings.sub.SubMode;
import me.leon.trinity.setting.settings.sub.SubSlider;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.misc.MessageBus;
import me.leon.trinity.utils.world.RaytraceUtils;
import me.leon.trinity.utils.world.Rotation.Rotation;
import me.leon.trinity.utils.world.Rotation.RotationUtils;
import me.leon.trinity.utils.world.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class KillAura extends Module {
    public static SettingParent main = new SettingParent("Main", true, false);
    public static SubBoolean chat = new SubBoolean("Chat", main, false);
    public static SubBoolean packet = new SubBoolean("Packet", main, true);
    public static SubBoolean onlyHolding = new SubBoolean("Only Holding", main, true);
    public static SubMode Switch = new SubMode("Switch", main, "None", "None", "Normal");

    public static SettingParent ranges = new SettingParent("Ranges", true, false);
    public static SubMode rayTraceMode = new SubMode("RayTrace Mode", ranges, "Leon", "Leon", "Simple", "Offset-Simple");
    public static SubSlider off = new SubSlider("Offset", ranges, 0, 1, 3, false);
    public static SubSlider wallsRange = new SubSlider("Wall Range", ranges, 0, 3.5, 5, false);
    public static SubSlider normalRange = new SubSlider("Normal Range", ranges, 1, 5, 6, false);

    public static SettingParent rotate = new SettingParent("Rotate", true, true);
    public static SubMode rotation = new SubMode("Mode", rotate, "Packet", "Packet", "Client", "Random");
    public static SubSlider offset = new SubSlider("Offset", rotate, 0, 1, 2, false);

    public static SettingParent delays = new SettingParent("Delays", true, false);
    public static SubMode delayMode = new SubMode("Mode", delays, "Ready", "Custom", "Ready");
    public static SubSlider delay = new SubSlider("Delay", delays, 10, 700, 2000, false);
    public static SubBoolean sync = new SubBoolean("Sync", delays, false);

    public static SettingParent experimental = new SettingParent("Experimental", true, false);
    public static SubBoolean armorMelt = new SubBoolean("Armor Melt", experimental, true);

    public static SettingParent targeting = new SettingParent("Targeting", true, false);
    public static SubMode targetingMode = new SubMode("Mode", targeting, "Closest", "Closest", "Lowest Health", "Highest Health");
    public static SubSlider targetRange = new SubSlider("Range", targeting, 0.5, 5, 10, false);
    public static SubBoolean players = new SubBoolean("Players", targeting, true);
    public static SubBoolean friends = new SubBoolean("Friends", targeting, true);
    public static SubBoolean passive = new SubBoolean("Passive", targeting, true);
    public static SubBoolean hostile = new SubBoolean("Hostile", targeting, true);

    public static EntityLivingBase target = null;
    private MODES mode;
    private Timer timer;

    public KillAura() {
        super("KillAura", "Attacks Entities nearby", Category.COMBAT);
        this.timer = new Timer();
    }

    @Override
    public void onEnable() {
        clean();
    }

    @Override
    public void onUpdate() {
        if(mc.player == null || mc.world == null) return;
        EntityLivingBase target1 = target;
        target = EntityUtils.getTarget(players.getValue(), friends.getValue(), hostile.getValue(), passive.getValue(), targetRange.getValue(), EntityUtils.toMode(targetingMode.getValue()));
        if(target == null) {
            clean();
            return;
        }
        if(target1 != target && chat.getValue()) {
            MessageBus.sendClientMessage("Found new target: " + target.getName(), true);
        }
        killAura();
    }

    private void killAura() {
        if(EntityUtils.getRange(target) > normalRange.getValue()) return;
        this.mode = MODES.INSIGHT;

        //Trinity.LOGGER.info("a");
        Vec3d vec = null;
        if(rayTraceMode.getValue().equalsIgnoreCase("Leon")) {
            vec = RaytraceUtils.rayTraceLeon(target);
            if(vec == null) {
                if(EntityUtils.getRange(target) > wallsRange.getValue()) {
                    clean();
                    return;
                } else {
                    this.mode = MODES.WALL;
                    vec = target.getPositionVector().add(0, target.height / 2, 0);
                }
            }
        } else
        if(rayTraceMode.getValue().equalsIgnoreCase("Simple")) {
            vec = target.getPositionVector().add(0, target.height / 2, 0);
            if(!RaytraceUtils.rayTraceSimple(target)) {
                if(EntityUtils.getRange(target) > wallsRange.getValue()) {
                    clean();
                    return;
                } else this.mode = MODES.WALL;
            }
        } else
        if(rayTraceMode.getValue().equalsIgnoreCase("Offset-Simple")) {
            vec = target.getPositionVector().add(0, target.height / 2, 0);
            if(!RaytraceUtils.rayTraceSimple(target, off.getValue())) {
                if(EntityUtils.getRange(target) > wallsRange.getValue()) {
                    clean();
                    return;
                } else this.mode = MODES.WALL;
            }
        }

        //Trinity.LOGGER.info("b");
        int slot = InventoryUtil.find(Items.DIAMOND_SWORD);
        int slot0 = InventoryUtil.find(Items.DIAMOND_AXE);
        if(Switch.getValue().equalsIgnoreCase("Normal")) {
            if(slot != -1) {
                mc.player.inventory.currentItem = slot;
            } else if(slot0 != -1) {
                mc.player.inventory.currentItem = slot0;
            } else {
                clean();
                return;
            }
            mc.playerController.updateController();
        } else {
            if(slot != mc.player.inventory.currentItem && slot0 != mc.player.inventory.currentItem && onlyHolding.getValue()) {
                clean();
                return;
            }
        }
        //Trinity.LOGGER.info("c");

        if(rotate.getValue()) {
            if(rotation.getValue().equalsIgnoreCase("Random")) {
                RotationUtils.rotateRandom(true);
            } else {
                RotationUtils.rotateTowards(vec, rotation.getValue().equalsIgnoreCase("Packet"));
            }
        }
        //Trinity.LOGGER.info("d");

        if(delayMode.getValue().equalsIgnoreCase("Ready")) {
            if(armorMelt.getValue())
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);

            attackEntity(target, true, sync.getValue(), packet.getValue());

            if(armorMelt.getValue()) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                attackEntity(target, false, sync.getValue(), packet.getValue());
            }
        } else if(delayMode.getValue().equalsIgnoreCase("Custom")) {
            if(timer.hasPassed((int) delay.getValue())) {
                this.timer.reset();

                attackEntity(target, false, sync.getValue(), packet.getValue());
            }
        }
        //Trinity.LOGGER.info("e");
    }

    private void attackEntity(Entity entity, boolean cooldown, boolean sync, boolean packet) {
        if (!cooldown || (mc.player.getCooledAttackStrength(sync ? (20 - TickrateManager.getTPS()) : 0) >= 1)) {
            if (packet)
                mc.player.connection.sendPacket(new CPacketUseEntity(entity));
            else
                mc.playerController.attackEntity(mc.player, entity);

            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.resetCooldown();
        }
    }

    private void clean() {
        this.timer.reset();
        this.mode = MODES.INSIGHT;
    }

    private enum MODES {
        WALL, INSIGHT
    }
}
