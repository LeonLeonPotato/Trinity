package me.leon.trinity.hud;

import me.leon.trinity.clickgui.components.Frame;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.HudManager;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.utils.math.MathUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

public class HUDeditor extends GuiScreen {
    public float dragX, dragY;

    public HUDeditor() {

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if(keyCode == 1) {
            for(Component c : Trinity.hudManager.comps) {
                c.dragging = false;
            }

            mc.displayGuiScreen(null);
            ClickGUI.stopShader();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(ClickGUI.background.getValue().equalsIgnoreCase("Darken") || ClickGUI.background.getValue().equalsIgnoreCase("Both")) this.drawDefaultBackground();
        for(Component comp : Trinity.hudManager.comps) {
            comp.res = new ScaledResolution(mc);
            if(comp.visible) {
                comp.render();
            }
            if(comp.dragging) {
                float x = mouseX - dragX;
                float y = mouseY - dragY;

                if(me.leon.trinity.hacks.client.HUDeditor.clamp.getValue()) {
                    comp.y = (float) MathUtils.clamp(0, comp.res.getScaledHeight(), y, comp.height());
                    comp.x = (float) MathUtils.clamp(0, comp.res.getScaledWidth(), x, comp.width());
                } else {
                    comp.x = x;
                    comp.y = y;
                }
            }
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        boolean dragging = false;
        for(Component comp : Trinity.hudManager.comps) {
            if(dragging) continue;

            if(comp.onButton(mouseX, mouseY) && mouseButton == 0) {
                comp.dragging = true;
                dragging = true;
                this.dragX = mouseX - comp.x;
                this.dragY = mouseY - comp.y;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for(Component comp : Trinity.hudManager.comps) {
            comp.dragging = false;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return ClickGUI.pause.getValue();
    }
}
