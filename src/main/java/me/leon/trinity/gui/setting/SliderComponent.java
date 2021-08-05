package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.GuiUtils;

import java.awt.*;

public class SliderComponent extends ISetting<SliderSetting> {
    private boolean dragging = false;

    public SliderComponent(IComponent parent, ButtonComponent superParent, Setting set, int offset) {
        super(parent, superParent, set, offset);
    }

    @Override
    public void render(Point point) {
        final float realY = superParent.parent().getY() + offset;
        final float realX = superParent.parent().getX();

        drawRect(realX, realY, realX + getWidth(), realY + 14, getColor(point, false));
        float width = (float) (xOffset() + ((set.getValue() - set.getMin()) / (set.getMax() - set.getMin())) * (getWidth() - xOffset()));
        drawRect(getFrame().getX() + xOffset(), getFrame().getY() + offset, getFrame().getX() + width, getFrame().getY() + offset + 14, ClickGUI.sliderColor.getValue());
        FontUtil.drawString(set.getName() + " " + set.getValue(), realX + xOffset(), realY + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.settingNameColor.getValue());
    }

    @Override
    public void update(Point point) {
        if(dragging) {
            if(point.x == getFrame().getX() + getWidth()) {
                set.setValue(set.getMax());
            } else if (point.x == getFrame().getX() + xOffset()) {
                set.setValue(set.getMin());
            } else {
                GuiUtils.slider(set, point.x, getFrame().getX() + xOffset(), getWidth() - xOffset());
            }
        }
        subs.forEach(e -> e.update(point));
    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(onButton(point)) {
            dragging = true;
            return true;
        }
        for (ISetting<?> sub : subs) {
            if(sub.buttonClick(button, point)) return true;
        }
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        dragging = false;
        for (ISetting<?> sub : subs) {
            if(sub.buttonRelease(button, point)) return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char chr, int code) {
        for (ISetting<?> sub : subs) {
            if(sub.keyTyped(chr, code)) return true;
        }
        return false;
    }
}
