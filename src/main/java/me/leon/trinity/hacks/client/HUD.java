package me.leon.trinity.hacks.client;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.components.sub.String;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hud.AnchorPoint;
import me.leon.trinity.hud.Component;
import me.leon.trinity.hud.HUDeditor;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.StringInput;
import me.leon.trinity.utils.math.MathUtils;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HUD extends Module {
    public static StringInput input = new StringInput("aaa", "aaa");

    public HUD() {
        super("HUD", "On screen overlay", Category.CLIENT);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (mc.currentScreen instanceof HUDeditor || mc.currentScreen instanceof ClickGui || mc.gameSettings.showDebugInfo) return;

        final ScaledResolution sr = new ScaledResolution(mc);
        final HashMap<AnchorPoint, Point> map = new HashMap<>();
        //map.put(AnchorPoint.CENTER, new Point(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2));
        map.put(AnchorPoint.TOPLEFT, new Point(0, 0));
        map.put(AnchorPoint.TOPRIGHT, new Point(sr.getScaledWidth(), 0));
        map.put(AnchorPoint.BOTTOMLEFT, new Point(0, sr.getScaledHeight()));
        map.put(AnchorPoint.BOTTOMRIGHT, new Point(sr.getScaledWidth(), sr.getScaledHeight()));

        for(Component comp : Trinity.hudManager.comps) {
            comp.res = sr;

            float x = comp.x;
            float y = comp.y;

            if(me.leon.trinity.hacks.client.HUDeditor.anchor.getValue()) {
                if(comp.anchorPoint != null) {
                    final Point p = map.get(comp.anchorPoint);
                    if(comp.anchorPoint == AnchorPoint.BOTTOMLEFT) {
                        y = p.y - comp.height();
                        x = p.x;
                    } else
                    if(comp.anchorPoint == AnchorPoint.BOTTOMRIGHT){
                        y = p.y - comp.height();
                        x = p.x - comp.width();
                    } else
                    if(comp.anchorPoint == AnchorPoint.TOPRIGHT) {
                        y = p.y;
                        x = p.x - comp.width();
                    } else {
                        x = map.get(comp.anchorPoint).x;
                        y = map.get(comp.anchorPoint).y;
                    }
                }
            }

            comp.x = x;
            comp.y = y;

            if(comp.visible) {
                comp.render();
            }
        }
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
