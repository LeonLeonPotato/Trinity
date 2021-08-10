package me.leon.trinity.gui.button;

import me.leon.trinity.config.rewrite.Preset;
import me.leon.trinity.gui.frame.FrameComponent;
import me.leon.trinity.gui.setting.BooleanComponent;
import me.leon.trinity.gui.setting.TextBoxComponent;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.TextBoxSetting;

import java.awt.*;

/**
 * @author Leon
 *
 * lots of stuff are hardcoded into here because why not
 */
public class PresetButton extends IButton {
    private final Preset preset;
    private final TextBoxComponent name;
    private final BooleanComponent delete;

    public PresetButton(FrameComponent parent, Preset preset, int offset) {
        super(parent, offset);
        this.preset = preset;
        name = new TextBoxComponent(parent, this, new TextBoxSetting("Name", "trollage"), offset + 14);
        delete = new BooleanComponent(parent, this, new BooleanSetting("Delete", false), offset + 14);
    }

    @Override
    public void render(Point point) {
        drawBack(point, preset.name, Trinity.currentPreset == preset);
    }

    @Override
    public void update(Point point) {
        if(!name.isTyping()) preset.name = name.getSet().getValue();
    }

    @Override
    public void unload() {

    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(onButton(point) && button == 0) {
            preset.load();
        }
        return true;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        return false;
    }

    @Override
    public boolean keyTyped(char chr, int code) {
        return !open && name.keyTyped(chr, code);
    }

    @Override
    public float height() {
        return open ? (28 + name.height()) : 14;
    }

    @Override
    public String description() {
        return null;
    }
}
