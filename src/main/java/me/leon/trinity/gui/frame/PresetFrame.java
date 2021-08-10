package me.leon.trinity.gui.frame;

import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.managers.ModuleManager;

import java.awt.*;

/**
 * @author Leon
 *
 * hardcoded again
 */
public class PresetFrame extends IFrame {
    public PresetFrame(Category c, float x, float y) {
        super(c, x, y);
    }

    @Override
    public void render(Point point) {
        if(open) {

        }
    }

    @Override
    public void update(Point point) {

    }

    @Override
    public void unload() {

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
}
