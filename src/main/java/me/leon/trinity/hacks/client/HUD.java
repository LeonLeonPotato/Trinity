package me.leon.trinity.hacks.client;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hud.Component;
import me.leon.trinity.hud.HUDeditor;
import me.leon.trinity.main.Trinity;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {
    public HUD() {
        super("HUD", "On screen overlay", Category.CLIENT);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if ((mc.currentScreen instanceof HUDeditor) || (mc.currentScreen instanceof ClickGui)) return;

        for(Component c : Trinity.hudManager.comps) {
            c.res = new ScaledResolution(mc);
            if(c.visible) {
                //GlStateManager.pushMatrix();
                c.render();
                //GlStateManager.popMatrix();
            }
        }
    }
}
