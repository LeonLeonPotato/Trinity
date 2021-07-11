package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.Setting;

import java.awt.*;

public class BooleanComponent extends ISetting<BooleanSetting> {
    protected BooleanComponent(IComponent parent, ButtonComponent superParent, Setting set, int offset) {
        super(parent, superParent, set, offset);
    }

    @Override
    public void render(Point point) {

    }

    @Override
    public void update(Point point) {

    }

    @Override
    public boolean buttonClick(int button, Point point) {
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        return false;
    }

    @Override
    public boolean keyTyped(int code) {
        return false;
    }

    @Override
    public float height() {
        return 0;
    }

    @Override
    public float xOffset() {
        return 0;
    }
}
