package me.leon.trinity.hud;

import me.leon.trinity.clickgui.components.Frame;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.HudManager;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.utils.math.MathUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
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

            final Point[] anchorPoints = new Point[] {
                    new Point(0, 0),
                    new Point(comp.res.getScaledWidth(), 0),
                    new Point(0, comp.res.getScaledHeight()),
                    new Point(comp.res.getScaledWidth(), comp.res.getScaledHeight())
            };

            if(comp.visible) {
                comp.render();
            }

            if(comp.dragging) {
                float x = mouseX - dragX;
                float y = mouseY - dragY;

                if(me.leon.trinity.hacks.client.HUDeditor.anchor.getValue()) {
                    for(Point p : anchorPoints) {
                        if(intersects(p.x - 10, p.y - 10, p.x + 10, p.y + 10, x, y, comp.width(), comp.height())) { // with blood and tears ):
                            x = p.x;
                            y = p.y;
                        }
                    }
                }
                if(me.leon.trinity.hacks.client.HUDeditor.clamp.getValue()) {
                    x = (float) MathUtils.clamp(0, comp.res.getScaledWidth(), x, comp.width());
                    y = (float) MathUtils.clamp(0, comp.res.getScaledHeight(), y, comp.height());
                }
                comp.x = x;
                comp.y = y;
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

    private boolean intersects(int x, int y, int x1, int y1, float x2, float y2, float x3, float y3) {
        boolean found = false;
        int[][] points = { {x, y}, {x1, y1}, {x, y1}, {x1, y} };
        float[][] points0 = { {x2, y2}, {x3, y3}, {x3, y2}, {x2, y3} };

        for(int[] point : points) {
            if(check(x2, y2, x2 + x3, y2 + y3, point[0], point[1])) { // comp check, see if POINT is inside COMP
                found = true;
            }
        }
        for(float[] point : points0) {
            if(check(x, y, x + x1, y + y1, (int) point[0], (int) point[1])) { // anchor check, see if COMP is inside ANCHOR
                found = true;
            }
        }
        return found;
    }

    private boolean check(float x, float y, float x1, float y1, int mX, int mY) {
        return mX >= x && mX <= x1 && mY >= y && mY <= y1;
    }
}
