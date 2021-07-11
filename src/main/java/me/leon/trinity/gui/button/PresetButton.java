package me.leon.trinity.gui.button;

import me.leon.trinity.config.rewrite.PresetObj;
import me.leon.trinity.gui.FrameComponent;

import java.awt.*;

public class PresetButton extends IButton {
    private final PresetObj preset;

    public PresetButton(FrameComponent parent, PresetObj preset, int offset) {
        super(parent, offset);
        this.preset = preset;
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

    @Override
    public boolean keyTyped(int code) {
        return false;
    }

    @Override
    public float height() {
        return 0;
    }

    @Override
    public String description() {
        return null;
    }
}
