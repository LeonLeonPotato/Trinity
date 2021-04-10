package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.utils.world.Timer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class AutoArmor extends Module {
    public static Slider delay = new Slider("Delay", 0, 200, 1000, true);
    public static Boolean elytra = new Boolean("PreferElytra", false);
    public static Boolean noThorns = new Boolean("NoThorns", true);
    public static Boolean searchCrafting = new Boolean("CraftingSlots", true);

    public AutoArmor() {
        super("AutoArmor", "Replaces armor", Category.COMBAT);
    }

    private final Timer timer = new Timer();
    private final ArrayList<Item> leggings = new ArrayList<>(Arrays.asList(Items.LEATHER_LEGGINGS, Items.IRON_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.DIAMOND_LEGGINGS));
    private final ArrayList<Item> helmets = new ArrayList<>(Arrays.asList(Items.LEATHER_HELMET, Items.IRON_HELMET, Items.GOLDEN_HELMET, Items.CHAINMAIL_HELMET, Items.DIAMOND_HELMET));
    private final ArrayList<Item> chestplates = new ArrayList<>(Arrays.asList(Items.LEATHER_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.ELYTRA));
    private final ArrayList<Item> boots = new ArrayList<>(Arrays.asList(Items.LEATHER_BOOTS, Items.IRON_BOOTS, Items.GOLDEN_BOOTS, Items.CHAINMAIL_BOOTS, Items.DIAMOND_BOOTS));

    public static boolean pause = false;
    public static boolean checkCrafting = false;

    @Override
    public void onUpdate() {
        if(nullCheck()) return;
        if(pause) return;

        final int[] refined = refineList(mapArmor());
        int switched = 0;
        boolean hadtowait = false;

        for(int a = 0; a < 4; a++) {
            if(refined[a] != -1) {
                final ItemStack armor = mc.player.inventoryContainer.getInventory().get(a + 5);

                if(armor == null || armor.getItem() == Items.AIR) {
                    if(!timer.passAndReset((int) delay.getValue())) {
                        hadtowait = true;
                        continue;
                    }

                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, refined[a], 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, a + 5, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, refined[a], 0, ClickType.PICKUP, mc.player);
                    switched++;
                }
            }
        }

        if(!hadtowait) {
            if(switched == 0) checkCrafting = false;
        }
    }

    private ArrayList<Integer> mapArmor() {
        ArrayList<Integer> map = new ArrayList<>();
        if(checkCrafting || searchCrafting.getValue()) {
            for(int a = 1; a < 5; a++) {
                final Item item = mc.player.inventoryContainer.getInventory().get(a).getItem();
                if(item instanceof ItemArmor || item instanceof ItemElytra) {
                    map.add(a);
                }
            }
        }
        for(int a = 9; a < 36; a++) {
            final Item item = mc.player.inventoryContainer.getInventory().get(a).getItem();
            if(item instanceof ItemArmor || item instanceof ItemElytra) {
                map.add(a);
            }
        }
        return map;
    }

    private int[] refineList(ArrayList<Integer> map) {
        int legSlot = -1;
        int chestSlot = -1;
        int bootsSlot = -1;
        int helmetSlot = -1;
        for(int slot : map) {
            final ItemStack item = mc.player.inventoryContainer.getInventory().get(slot);

            if(EnchantmentHelper.getEnchantments(item).containsKey(Enchantment.getEnchantmentByID(7)) && noThorns.getValue()) {
                continue;
            }

            if(leggings.contains(item.getItem())) {
                if(legSlot == -1) {
                    legSlot = slot;
                } else {
                    final Item leg = mc.player.inventoryContainer.getInventory().get(legSlot).getItem();
                    if(((ItemArmor) item.getItem()).damageReduceAmount < ((ItemArmor) leg).damageReduceAmount) {
                        legSlot = slot;
                    }
                }
            } else
            if(boots.contains(item.getItem())) {
                if(bootsSlot == -1) {
                    bootsSlot = slot;
                } else {
                    final Item boot = mc.player.inventoryContainer.getInventory().get(bootsSlot).getItem();
                    if(((ItemArmor) item.getItem()).damageReduceAmount < ((ItemArmor) boot).damageReduceAmount) {
                        bootsSlot = slot;
                    }
                }
            } else
            if(chestplates.contains(item.getItem())) {
                if(chestSlot == -1) {
                    chestSlot = slot;
                } else {
                    final Item chest = mc.player.inventoryContainer.getInventory().get(chestSlot).getItem();
                    if(item.getItem() instanceof ItemElytra && elytra.getValue()) {
                        chestSlot = slot;
                    }
                    if(chest instanceof ItemElytra && !(item.getItem() instanceof ItemElytra)) {
                        continue;
                    }
                    if(item.getItem() instanceof ItemArmor && chest instanceof ItemArmor) {
                        if(((ItemArmor) item.getItem()).damageReduceAmount < ((ItemArmor) chest).damageReduceAmount) {
                            chestSlot = slot;
                        }
                    }
                }
            } else
            if(helmets.contains(item.getItem())) {
                if(helmetSlot == -1) {
                    helmetSlot = slot;
                } else {
                    final Item helmet = mc.player.inventoryContainer.getInventory().get(helmetSlot).getItem();
                    if(((ItemArmor) item.getItem()).damageReduceAmount < ((ItemArmor) helmet).damageReduceAmount) {
                        helmetSlot = slot;
                    }
                }
            }
        }

        return new int[] { helmetSlot, chestSlot, legSlot, bootsSlot };
    }

    /**
    @Override
    public void onUpdate() {
        if(nullCheck()) return;
        if(pause) return;
        if(timer.passAndReset((int) delay.getValue())) return;

        int[] armorSlots = findArmor();
        for(int a = 0; a < 4; a++) {
            final ItemStack armor = mc.player.inventoryContainer.getInventory().get(a + 5);
            if(armor == null || armor.getItem() == Items.AIR) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, armorSlots[a], 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, a + 5, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, armorSlots[a], 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    private int[] findArmor() {
        int[] toReturn = { -1, -1, -1, -1 };

        if(searchCrafting.getValue() || checkCrafting) {
            for(int b = 0; b < 4; b++) {
                for(int a = 1; a < 5; a++) {
                    final ItemStack stack = mc.player.inventoryContainer.getInventory().get(a);
                    if(toReturn[b] == -1) {
                        if((stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemElytra && b == 3))
                            toReturn[b] = a;
                    } else {
                        if(check(stack, mc.player.inventoryContainer.getInventory().get(toReturn[b])))
                        {
                            toReturn[b] = a;
                        }
                    }
                }
            }
            checkCrafting = false;
        }
        for(int b = 0; b < 4; b++) {
            for(int a = 9; a < 36; a++) {
                final ItemStack stack = mc.player.inventoryContainer.getInventory().get(a);
                if(toReturn[b] == -1) {
                    if((stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemElytra && b == 3))
                        toReturn[b] = a;
                } else {
                    if(check(stack, mc.player.inventoryContainer.getInventory().get(toReturn[b])))
                    {
                        toReturn[b] = a;
                    }
                }
            }
        }

        return toReturn;
    }


    private boolean check(ItemStack one, ItemStack two) {
        if(!(one.getItem() instanceof ItemArmor) && !(two.getItem() instanceof ItemElytra)) {
            return false;
        }

        final Item twoArmor = two.getItem();
        final Item oneArmor = one.getItem();

        if(EnchantmentHelper.getEnchantments(one).containsKey(Enchantment.getEnchantmentByID(7)) && noThorns.getValue()) {
            return false;
        }

        if(oneArmor instanceof ItemArmor && twoArmor instanceof ItemArmor) {
            return ((ItemArmor) oneArmor).damageReduceAmount < ((ItemArmor) twoArmor).damageReduceAmount;
        } else if (elytra.getValue()){
            if(oneArmor instanceof ItemElytra && twoArmor instanceof ItemArmor) {
                return true;
            }
            return !(oneArmor instanceof ItemArmor) || !(twoArmor instanceof ItemElytra);
        }

        return true;
    }

    **/
}
