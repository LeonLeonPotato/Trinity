package me.leon.trinity.hacks.render;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.sub.SubBoolean;
import net.minecraft.block.BlockSign;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRender extends Module {
    public static Boolean items = new Boolean("Items", false);
    public static Boolean hurtCam = new Boolean("HurtCam", true);
    public static Boolean bosslist = new Boolean("BossList", false);
    public static Boolean fire = new Boolean("Fire", true);
    public static Boolean water = new Boolean("Water", true);
    public static Boolean signs = new Boolean("Signs", false);
    public static Boolean armor = new Boolean("Armor", false);
    public static Boolean weather = new Boolean("Weather", false);
    public static Boolean noCluster = new Boolean("NoCluster", true);
    public static SettingParent storage = new SettingParent("Storage", true, false);
    public static SubBoolean shulkers = new SubBoolean("Shulkers", storage, false);
    public static SubBoolean chests = new SubBoolean("Chests", storage, false);
    public static SubBoolean EChests = new SubBoolean("Ender Chests", storage, false);

    public NoRender() {
        super("NoRender", "Prevents rendering of certain stuff", Category.RENDER);
    }

    @SubscribeEvent
    public void onRenderBlock(RenderBlockOverlayEvent event) {
        if(fire.getValue() && event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.FIRE))
        {
            event.setCanceled(true);
        }

        if (water.getValue() && event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.WATER))
        {
            event.setCanceled(true);
        }
    }
}
