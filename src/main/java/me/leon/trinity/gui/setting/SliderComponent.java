package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.math.MathUtils;

import java.awt.*;

public class SliderComponent extends ISetting<SliderSetting> {
    private boolean dragging = false;

    public SliderComponent(IComponent parent, ButtonComponent superParent, Setting set, int offset) {
        super(parent, superParent, set, offset);
    }

    @Override
    public void render(Point point) {
        drawBack(point, set.getName(), false);
        float width = (float) (xOffset() + ((set.getValue() + set.getMin()) / set.getMax()) * (getWidth() - xOffset()));
        drawRect(getFrame().getX() + xOffset(), getFrame().getY() + offset, getFrame().getX(), width, ClickGUI.sliderColor.getValue());
    }

    @Override
    public void update(Point point) {
        if(dragging) {
            float absMouseX = point.x - (getFrame().getX() + xOffset()); // absolute mouse X from xOffset()
            float absWidth = getWidth() - xOffset();
            int relativeMouseX = (int) MathUtils.clamp(xOffset(), absWidth, absMouseX);

            if(relativeMouseX == absWidth) {
                set.setValue(set.getMax());
            } else if (relativeMouseX == xOffset()) {
                set.setValue(set.getMin());
            } else {
                set.setValue(set.getMin() + (((relativeMouseX - xOffset()) / absWidth) * (set.getMax() - set.getMin())));
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
    public boolean keyTyped(int code) {
        for (ISetting<?> sub : subs) {
            if(sub.keyTyped(code)) return true;
        }
        return false;
    }
}
