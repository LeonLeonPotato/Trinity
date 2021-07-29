package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.setting.rewrite.TextBoxSetting;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.leon.trinity.utils.rendering.skeet.Quad;

import java.awt.*;

public class TextBoxComponent extends ISetting<TextBoxSetting> {
    private boolean typing = false;

    public TextBoxComponent(IComponent parent, ButtonComponent superParent, Setting set, int offset) {
        super(parent, superParent, set, offset);
    }

    @Override
    public void render(Point point) {
        RenderUtils.scissor(new Quad(getFrame().getX(), getFrame().getY() + offset, getFrame().getX() + getWidth(), getFrame().getY() + offset + 14));
        drawBack(point, set.getValue(), false);
    }

    @Override
    public void update(Point point) {
    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(onButton(point)) {
            switch (button) {
                case 1: {
                    typing = true;
                    break;
                }
                case 2: {
                    open = !open;
                    break;
                }
            }
        }
        for (ISetting<?> sub : subs) {
            if(sub.buttonClick(button, point)) return true;
        }
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        for (ISetting<?> sub : subs) {
            if(sub.buttonRelease(button, point)) return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(int code) {
        return false;
    }
}
