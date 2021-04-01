package me.leon.trinity.clickgui.components.sub.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.sub.SubSetting;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.Coloring;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SubSlider extends Component {
    public me.leon.trinity.setting.settings.sub.SubSlider set;
    public SubSetting parent;
    public int offset;
    public boolean hovered = false;
    public double renderWidth = 0;
    public boolean dragging = false;

    public SubSlider(me.leon.trinity.setting.settings.sub.SubSlider set, SubSetting parent, int offset) {
        this.set = set;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.parent.parent.x + ClickGui.width, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 14, this.parent.parent.parent.x, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14, ClickGUI.backgroundColor.getValue());
        RenderUtils.drawRect((float) (this.parent.parent.parent.x + renderWidth + 7 + 4), this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 14, this.parent.parent.parent.x + 7 + 4, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14, ClickGUI.sliderColor.getValue());
        FontUtil.drawString(this.set.name + ": " + Coloring.getWHITE() + this.set.getValue(), this.parent.parent.parent.x + 7 + 6, this.parent.parent.parent.y + this.parent.parent.offset + this.offset + this.parent.offset + 14 + 14 + ((14 - FontUtil.getFontHeight())) / 2f, 0xa9b7c6);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && this.parent.open && this.parent.parent.open) {
            if(button == 0) {
                this.dragging = true;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
    }

    @Override
    public int getParentHeight() {
        return 14;
    }

    @Override
    public void keyTyped(char typedChar, int key) {

    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);

        double diff = Math.min(ClickGui.width - 11, Math.max(0, (mouseX - 11) - this.parent.parent.parent.x));

        double min = set.getMin();
        double max = set.getMax();

        renderWidth = (int) ((ClickGui.width - 11) * (set.getValue() - min) / (max - min));

        if (dragging) {
            if (diff == 0) {
                set.setValue(set.getMin());
            }
            else {
                double newValue = roundToPlace(((diff / (ClickGui.width - 11)) * (max - min) + min), set.isOnlyInt() ? 0 : 2);
                set.setValue(MathUtils.clamp(min, max, newValue));
            }
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.parent.parent.x + 13 && x < this.parent.parent.parent.x + ClickGui.width && y > this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 && y < this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 14;
    }

    private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
