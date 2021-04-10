package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.RotationManager;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.KeyBinding;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.sub.SubSlider;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.entity.InventoryUtil;
import me.leon.trinity.utils.world.Rotation.Rotation;
import me.leon.trinity.utils.world.Rotation.RotationPriority;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.world.gen.structure.template.BlockRotationProcessor;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.input.Keyboard;

public class AutoMend extends Module {
    public static Mode mode = new Mode("Mode", "Hold", "Hold", "Toggle");
    public static Boolean clientRotations = new Boolean("ClientRotations", false);
    public static Boolean predict = new Boolean("Predict", true);
    public static Boolean packet = new Boolean("Packet", true);
    public static SettingParent stopVals = new SettingParent("StopVals", true, false);
    public static KeyBinding macroBind = new KeyBinding("Hold Bind", Keyboard.KEY_O);
    public static SubSlider boots = new SubSlider("Boots", stopVals, 0, 90, 100, true);
    public static SubSlider leggings = new SubSlider("Leggings", stopVals, 0, 90, 100, true);
    public static SubSlider chestPlate = new SubSlider("Chestplate", stopVals, 0, 90, 100, true);
    public static SubSlider helmet = new SubSlider("Helmet", stopVals, 0, 90, 100, true);

    public AutoMend() {
        super("AutoMend", "Automatically mends armor", Category.COMBAT);
    }

    private int oldslot;
    private boolean started;

    @Override
    public void onEnable() {
        start();
    }

    @Override
    public void onDisable() {
        end();
    }

    @Override
    public void onUpdate() {
        if(nullCheck()) return;
        if(mode.getValue().equalsIgnoreCase("Hold")) {
            if (!Keyboard.isKeyDown(macroBind.Char)) {
                if(started) {
                    end();
                    this.started = false;
                }
                return;
            } else {
                if(!started) {
                    start();
                    this.started = true;
                }
            }
        }

        int xpSlot = InventoryUtil.find(Items.EXPERIENCE_BOTTLE);

        if(xpSlot == -1) {
            return;
        }

        if(clientRotations.getValue()) {
            mc.player.rotationPitch = 90;
        } else {
            RotationManager.rotationQueue.add(new Rotation(90, mc.player.rotationYaw, true, RotationPriority.Normal));
        }

        if(packet.getValue()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(xpSlot));
        } else {
            InventoryUtil.switchToSlot(xpSlot);
        }

        /** @author GameSense **/
        if(predict.getValue()) {
            int totalXp = mc.world.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityXPOrb)
                    .filter(entity -> entity.getDistanceSq(mc.player) <= 1)
                    .mapToInt(entity -> ((EntityXPOrb) entity).xpValue).sum();

            if ((totalXp * 2) < EntityUtils.getTotalArmor(mc.player)) {
                checkArmor();
                mend();
            }
        } else {
            checkArmor();
            mend();
        }
    }

    private void checkArmor() {
        for (ItemStack stack : mc.player.getArmorInventoryList()) {
            if (stack == null || stack.getItem() == Items.AIR)
                continue;

            if(stack.getItem() == Items.DIAMOND_BOOTS) {
                if(((float) (stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage()) * 100f >= boots.getValue()) {
                    final int foundSlot = findEmptySlot();
                    if(foundSlot != -1) {
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 8, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, foundSlot, 0, ClickType.PICKUP, mc.player);
                    }
                }
            }
            if(stack.getItem() == Items.DIAMOND_CHESTPLATE) {
                if(((float) (stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage()) * 100f >= chestPlate.getValue()) {
                    final int foundSlot = findEmptySlot();
                    if(foundSlot != -1) {
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, foundSlot, 0, ClickType.PICKUP, mc.player);
                    }
                }
            }
            if(stack.getItem() == Items.DIAMOND_LEGGINGS) {
                if(((float) (stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage()) * 100f >= leggings.getValue()) {
                    final int foundSlot = findEmptySlot();
                    if(foundSlot != -1) {
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 7, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, foundSlot, 0, ClickType.PICKUP, mc.player);
                    }
                }
            }
            if(stack.getItem() == Items.DIAMOND_HELMET) {
                if(((float) (stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage()) * 100f >= helmet.getValue()) {
                    final int foundSlot = findEmptySlot();
                    if(foundSlot != -1) {
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 5, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, foundSlot, 0, ClickType.PICKUP, mc.player);
                    }
                }
            }
        }
    }

    private void end() {
        AutoArmor.pause = false;
        AutoCrystal.pause = false;
        if(packet.getValue()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
        } else {
            InventoryUtil.switchToSlot(oldslot);
        }
    }

    private void start() {
        AutoArmor.pause = true;
        AutoArmor.checkCrafting = true;
        AutoCrystal.pause = true;
        this.oldslot = mc.player.inventory.currentItem;
    }

    private int findEmptySlot() {
        for(int a = 1; a < 5; a++) {
            final ItemStack stack = mc.player.inventoryContainer.getInventory().get(a);
            if(stack == ItemStack.EMPTY || stack == null || stack.getItem() == Items.AIR) {
                return a;
            }
        }
        for(int a = 9; a < 36; a++) {
            final ItemStack stack = mc.player.inventoryContainer.getInventory().get(a);
            if(stack == ItemStack.EMPTY || stack == null || stack.getItem() == Items.AIR) {
                return a;
            }
        }
        return -1;
    }

    private void mend() {
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
    }
}
