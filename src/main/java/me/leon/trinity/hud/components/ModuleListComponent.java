package me.leon.trinity.hud.components;

import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.HUD;
import me.leon.trinity.hacks.client.HUDeditor;
import me.leon.trinity.hud.AnchorPoint;
import me.leon.trinity.hud.Component;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.Rainbow;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ModuleListComponent extends Component {
    public ModuleListComponent() {
        super("ModuleListComponent");
        this.visible = true;
        this.x = res.getScaledWidth();
        this.y = 0;
        this.anchorPoint = AnchorPoint.TOPRIGHT;
    }

    private HashMap<Module, Float> map = new HashMap<>();
    private static int yL = 0;
    private final Alpha alpha = new Alpha();
    private boolean updated = false;
    private final Rainbow rainbow = new Rainbow();

    @Override
    public void render() {
        if(HUDeditor.MLCBackground.getValue().equalsIgnoreCase("Fill"))
            this.drawBackground();
        yL = 0;
        map.clear();
        int alphaBefore = this.alpha.alpha;
        boolean up = this.alpha.up;
        this.rainbow.push();
        for(Module mod : ModuleManager.modules) {
            if(mod.isEnabled() && mod.visible) {
                map.put(mod, FontUtil.getStringWidth(mod.getName()));
            }
        }
        switch (getQuad()) {
            case TOPRIGHT: {
                map = sortByValue(map, false);
                for(Module mod : map.keySet()) {
                    final Color color = getColor();
                    final float a = (this.x + this.width()) - FontUtil.getStringWidth(mod.getName());
                    if(HUDeditor.MLCLine.getValue()) {
                        drawBox(a - 1, this.y + yL + FontUtil.getFontHeight(), a - 3, this.y + yL, color);
                    }
                    if(HUDeditor.MLCBackground.getValue().equalsIgnoreCase("Lines")) {
                        drawBox((this.x + this.width()), this.y + yL + FontUtil.getFontHeight(), a - 1, this.y + yL, HUDeditor.MLCBackgroundColor.getValue());
                    }
                    FontUtil.drawString(mod.getName(), a, this.y + yL + 1, color.getRGB());
                    yL += FontUtil.getFontHeight();
                }
                break;
            }
            case TOPLEFT: {
                map = sortByValue(map, false);
                for(Module mod : map.keySet()) {
                    final Color color = getColor();
                    final float a = this.x + FontUtil.getStringWidth(mod.getName());
                    if(HUDeditor.MLCLine.getValue()) {
                        drawBox(a + 3, this.y + yL + FontUtil.getFontHeight(), a + 1, this.y + yL, color);
                    }
                    if(HUDeditor.MLCBackground.getValue().equalsIgnoreCase("Lines")) {
                        drawBox(a + 1, this.y + yL + FontUtil.getFontHeight(), this.x, this.y + yL, HUDeditor.MLCBackgroundColor.getValue());
                    }
                    FontUtil.drawString(mod.getName(), this.x, this.y + yL + 1, color.getRGB());
                    yL += FontUtil.getFontHeight();
                }
                break;
            }
            case BOTTOMRIGHT: {
                map = sortByValue(map, true);
                for(Module mod : map.keySet()) {
                    final Color color = getColor();
                    final float a = (this.x + this.width()) - FontUtil.getStringWidth(mod.getName());
                    if(HUDeditor.MLCLine.getValue()) {
                        drawBox(a - 1, this.y + yL + FontUtil.getFontHeight(), a - 3, this.y + yL, color);
                    }
                    if(HUDeditor.MLCBackground.getValue().equalsIgnoreCase("Lines")) {
                        drawBox((this.x + this.width()), this.y + yL + FontUtil.getFontHeight(), a - 1, this.y + yL, HUDeditor.MLCBackgroundColor.getValue());
                    }
                    FontUtil.drawString(mod.getName(), (this.x + this.width()) - FontUtil.getStringWidth(mod.getName()), this.y + yL + 1, getColor().getRGB());
                    yL += FontUtil.getFontHeight();
                }
                break;
            }
            case BOTTOMLEFT: {
                map = sortByValue(map, true);
                for(Module mod : map.keySet()) {
                    final Color color = getColor();
                    final float a = this.x + FontUtil.getStringWidth(mod.getName());
                    if(HUDeditor.MLCLine.getValue()) {
                        drawBox(a + 3, this.y + yL + FontUtil.getFontHeight(), a + 1, this.y + yL, color);
                    }
                    if(HUDeditor.MLCBackground.getValue().equalsIgnoreCase("Lines")) {
                        drawBox(a + 1, this.y + yL + FontUtil.getFontHeight(), this.x, this.y + yL, HUDeditor.MLCBackgroundColor.getValue());
                    }
                    FontUtil.drawString(mod.getName(), this.x, this.y + yL + 1, getColor().getRGB());
                    yL += FontUtil.getFontHeight();
                }
                break;
            }
        }
        this.updated = false;
        if(HUDeditor.MLCMode.getValue().equalsIgnoreCase("AlphaPulse")) {
            this.alpha.up = up;
            this.alpha.alpha = alphaBefore;
            this.alpha.updateAlpha(3);
        };
        this.rainbow.pop();
        this.rainbow.updateColor(3);
    }

    @Override
    public float width() {
        return getMax();
    }

    @Override
    public float height() {
        return yL;
    }

    private quads getQuad() {
        if(this.anchorPoint != null) {
            switch (this.anchorPoint) {
                case TOPLEFT: return quads.TOPLEFT;
                case TOPRIGHT: return quads.TOPRIGHT;
                case BOTTOMLEFT: return quads.BOTTOMLEFT;
                case BOTTOMRIGHT: return quads.BOTTOMRIGHT;
            }
        } else {
            final ScaledResolution sr = new ScaledResolution(mc);
            final float a = sr.getScaledWidth() / 2f;
            final float b = sr.getScaledHeight() / 2f;
            if(this.x <= a && this.y <= b) {
                return quads.TOPLEFT;
            }
            if(this.x >= a && this.y <= b) {
                return quads.TOPRIGHT;
            }
            if(this.x <= a && this.y >= b) {
                return quads.BOTTOMLEFT;
            }
            if(this.x >= a && this.y >= b) {
                return quads.BOTTOMRIGHT;
            }
        }
        return quads.TOPRIGHT;
    }

    private float getMax() {
        float max = 0;
        for(Float a : map.values()) {
            if(a > max) {
                max = a;
            }
        }
        return max + (HUDeditor.MLCLine.getValue() ? 3 : 0);
    }

    private Color getColor() {
        if(!updated && HUDeditor.MLCMode.getValue().equalsIgnoreCase("AlphaPulseStatic")) {
            this.alpha.updateAlpha(3);
            this.updated = true;
        }
        if(HUDeditor.MLCMode.getValue().equalsIgnoreCase("RainbowStatic")) {
            return HUDeditor.MLCcolor.getValue();
        } else
        if(HUDeditor.MLCMode.getValue().equalsIgnoreCase("Rainbow")) {
            rainbow.updateColor(30);
            return rainbow.getColor();
        } else
        if(HUDeditor.MLCMode.getValue().equalsIgnoreCase("AlphaPulse")) {
            alpha.updateAlpha(30);
            return new Color(HUDeditor.MLCcolor.r, HUDeditor.MLCcolor.g, HUDeditor.MLCcolor.b, alpha.alpha);
        } else
        if(HUDeditor.MLCMode.getValue().equalsIgnoreCase("Static")) {
            return Color.MAGENTA;
        } else
        if(HUDeditor.MLCMode.getValue().equalsIgnoreCase("AlphaPulseStatic")) {
            return new Color(HUDeditor.MLCcolor.r, HUDeditor.MLCcolor.g, HUDeditor.MLCcolor.b, alpha.alpha);
        }
        return null;
    }

    public static HashMap<Module, Float> sortByValue(HashMap<Module, Float> hm, boolean up)
    {
        List<Map.Entry<Module, Float> > list =
                new LinkedList<>(hm.entrySet());

        if(up) {
            list.sort(Map.Entry.comparingByValue());
        } else {
            list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
        }

        HashMap<Module, Float> temp = new LinkedHashMap<>();
        for (Map.Entry<Module, Float> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private enum quads {
        TOPRIGHT, TOPLEFT, BOTTOMRIGHT, BOTTOMLEFT
    }

    private final static class Alpha {
        private boolean up = true;
        private int alpha;
        private Alpha() { alpha = 100; }

        private void updateAlpha(int a) {
            if(alpha >= 255) {
                up = false;
            } else
            if(alpha <= 100) {
                up = true;
            }
            alpha = (int) MathUtils.clamp(100, 255, alpha + (up ? a : -a));
        }
    }
}
