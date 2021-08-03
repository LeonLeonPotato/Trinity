package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.Setting;

import java.awt.*;

/**
 * @author Leon
 *
 * fyi: color shouldn't have any sub settings
 */
public class ColorComponent extends ISetting<ColorSetting> {
    public ColorComponent(IComponent parent, ButtonComponent superParent, Setting set, int offset) {
        super(parent, superParent, set, offset);
        set.getSubSettings().clear(); // prevent color sub settings
        subs.clear();
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
    public boolean keyTyped(char chr, int code) {
        return false;
    }

    @Override
    public float height() {
        return 14;
    }
}
