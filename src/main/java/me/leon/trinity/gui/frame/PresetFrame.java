package me.leon.trinity.gui.frame;

import me.leon.trinity.hacks.Category;

import java.awt.*;

public class PresetFrame extends IFrame {
    public PresetFrame(Category c, float x, float y) {
        super(c, x, y);
    }

    @Override
    public void render(Point point) {

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
}
