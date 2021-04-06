package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.setting.settings.sub.SubMode;
import me.leon.trinity.setting.settings.sub.SubSlider;
import me.leon.trinity.utils.world.WorldUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemSword;

public class Offhand extends Module {
    public static Mode offhand = new Mode("Mode", "None", "Totem", "Crystal", "Gapple", "None");
    public static Slider switchHealth = new Slider("Switch", 0, 16, 36, true);
    public static SettingParent calcCrystalDamage = new SettingParent("Calc", true, true);
    public static SubSlider calculateDist = new SubSlider("Distance", calcCrystalDamage, 1, 8, 16, false);
    public static SubMode calcMode = new SubMode("CalcMode", calcCrystalDamage, "PlayerHealth", "PlayerHealth", "SwitchHealth");
    public static Boolean forceGapple = new Boolean("Force Gapple", true);
    public static Boolean forceStr = new Boolean("Force Str", false);
    public static Boolean onlySword = new Boolean("onlySword", false);
    public static Boolean reverse = new Boolean("Reverse", false);
    public static Boolean autoTotem = new Boolean("AutoTotem", true);

    public Offhand() {
        super("Offhand", "Puts things in your offhand / mainhand", Category.COMBAT);
    }

    private static int lastSlot = -1;
    private static int endSlot = -1;
    private static int gapSlot = -1;
    private static int totSlot = -1;
    private static int strSlot = -1;

    @Override
    public void onUpdate() {
        if(pCheck()) return;
        for(int a = 9; a < 36; a++) {
            final Item item = mc.player.inventoryContainer.getInventory().get(a).getItem();
            if(item == Items.END_CRYSTAL) {
                endSlot = a;
            }
            if(item == Items.POTIONITEM) {
                if(mc.player.inventoryContainer.getInventory().get(a).stackTagCompound.toString().split(":")[2].contains("strength")) {
                    strSlot = a;
                }
            }
            if(item == Items.TOTEM_OF_UNDYING) {
                totSlot = a;
            }
            if(item == Items.GOLDEN_APPLE) {
                gapSlot = a;
            }
        }
        for(int a = 36; a < 45; a++) {
            final Item item = mc.player.inventoryContainer.getInventory().get(a).getItem();
            if(endSlot == -1 && item == Items.END_CRYSTAL) {
                endSlot = a;
            }
            if(strSlot == -1 && item == Items.POTIONITEM) {
                if(mc.player.inventoryContainer.getInventory().get(a).stackTagCompound.toString().split(":")[2].contains("strength")) {
                    strSlot = a;
                }
            }
            if(totSlot == -1 && item == Items.TOTEM_OF_UNDYING) {
                totSlot = a;
            }
            if(gapSlot == -1 && item == Items.GOLDEN_APPLE) {
                gapSlot = a;
            }
        }

        if(mc.currentScreen instanceof GuiInventory) {
            clean();
            return;
        }
        if((calc() || getHealth() <= switchHealth.getValue()) && autoTotem.getValue())  {
            switchTo(Modes.TOTEM);
        } else {
            if (!forceGapple.getValue() && !forceStr.getValue()) {
                switchTo(toMode(offhand.getValue()));
            } else {
                if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    if (onlySword.getValue() && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) {
                        clean();
                        return;
                    }
                    if (forceStr.getValue() && forceStr.getValue() && !mc.player.isPotionActive(MobEffects.STRENGTH)) {
                        switchTo(Modes.POT);
                    } else if (forceGapple.getValue()) {
                        switchTo(Modes.GAP);
                    } else {
                        switchTo(toMode(offhand.getValue()));
                    }
                } else {
                    switchTo(toMode(offhand.getValue()));
                }
            }
        }
        clean();
    }

    private void clean() {
        totSlot = -1;
        gapSlot = -1;
        strSlot = -1;
        endSlot = -1;
    }

    private boolean calc() {
        if(!calcCrystalDamage.getValue()) return false;
        for(final Entity entity : mc.world.loadedEntityList) {
            if(entity.getDistanceSq(mc.player) <= Math.pow(calculateDist.getValue(), 2) && entity instanceof EntityEnderCrystal) {
                if(getHealth() - WorldUtils.calculateDamage(entity.posX, entity.posY, entity.posZ, mc.player) <= (calcMode.getValue().equalsIgnoreCase("SwitchHealth") ? switchHealth.getValue() : 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    private float getHealth() {
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    private void switchToSlot(int from) {
        if(from == -1) return;
        lastSlot = from;
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, reverse.getValue() ? mc.player.inventory.currentItem + 36 : 45, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }

    private void switchToSlot(int from, boolean main) {
        if(from == -1) return;
        lastSlot = from;
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, main ? mc.player.inventory.currentItem + 36 : 45, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }

    private void switchTo(Modes mode) {
        if(mode == Modes.CRYSTAL) {
            if(mc.player.inventoryContainer.getInventory().get(45).getItem() != Items.END_CRYSTAL) {
                switchToSlot(endSlot);
            }
        }
        if(mode == Modes.GAP) {
            if(mc.player.inventoryContainer.getInventory().get(45).getItem() != Items.GOLDEN_APPLE) {
                switchToSlot(gapSlot);
            }
        }
        if(mode == Modes.POT) {
            if(mc.player.inventoryContainer.getInventory().get(45).getItem() != Items.POTIONITEM) {
                switchToSlot(strSlot);
            }
        }
        if(mode == Modes.TOTEM) {
            if(mc.player.inventoryContainer.getInventory().get(45).getItem() != Items.TOTEM_OF_UNDYING) {
                switchToSlot(totSlot);
            }
        }
    }

    private void switchToMain(Modes mode) {
        if(mode == Modes.CRYSTAL) {
            if(mc.player.inventoryContainer.getInventory().get(45).getItem() != Items.END_CRYSTAL) {
                switchToSlot(endSlot, true);
            }
        }
        if(mode == Modes.GAP) {
            if(mc.player.inventoryContainer.getInventory().get(45).getItem() != Items.GOLDEN_APPLE) {
                switchToSlot(gapSlot, true);
            }
        }
        if(mode == Modes.POT) {
            if(mc.player.inventoryContainer.getInventory().get(45).getItem() != Items.POTIONITEM) {
                switchToSlot(strSlot, true);
            }
        }
        if(mode == Modes.TOTEM) {
            if(mc.player.inventoryContainer.getInventory().get(45).getItem() != Items.TOTEM_OF_UNDYING) {
                switchToSlot(totSlot, true);
            }
        }
    }

    private Modes toMode(String string) {
        switch(string) {
            case "Totem": {
                return Modes.TOTEM;
            }
            case "Gapple": {
                return Modes.GAP;
            }
            case "Crystal": {
                return Modes.CRYSTAL;
            }
        }
        return null;
    }

    private enum Modes {
        POT, CRYSTAL, TOTEM, GAP
    }
}
