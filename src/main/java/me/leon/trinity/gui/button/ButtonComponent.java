package me.leon.trinity.gui.button;

import me.leon.trinity.gui.FrameComponent;
import me.leon.trinity.gui.setting.ISetting;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.managers.SettingManager;
import me.leon.trinity.setting.rewrite.Setting;

import java.awt.*;
import java.util.ArrayList;

public class ButtonComponent extends IButton {
    private final ArrayList<ISetting<?>> settings;
    private final Module mod;
    private boolean open;

    public ButtonComponent(FrameComponent parent, Module mod, int offset) {
        super(parent, offset);
        this.mod = mod;
        this.open = false;

        this.settings = new ArrayList<>();

        int s_off = offset + 14;
        for(Setting s : SettingManager.getSettings(mod)) {
            s_off += 14;
        }
    }

    @Override
    public void render(Point point) {
        drawBack(point, mod.getName(), mod.isEnabled());
        settings.forEach(e -> e.render(point));
    }

    @Override
    public void update(Point point) {
        settings.forEach(e -> e.update(point));
    }

    @Override
    public void unload() {
        settings.forEach(ISetting::unload);
    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(button == 0 && onButton(point)) {
            mod.toggle();
        }
        if(onButton(point)) return true;
        for(ISetting<?> set : settings) {
            if(set.buttonClick(button, point)) return true;
        }
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        if(onButton(point)) return true;
        for(ISetting<?> set : settings) {
            if(set.buttonRelease(button, point)) return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(int code) {
        for(ISetting<?> set : settings) {
            if(set.keyTyped(code)) return true;
        }
        return false;
    }

    @Override
    public float height() {
        if(open) {
            int h = 14;
            for(ISetting<?> s : settings) { h += s.height(); }
            return h;
        } else return 14;
    }

    @Override
    public String description() {
        return mod.getDescription();
    }

    public ArrayList<ISetting<?>> getSettings() {
        return settings;
    }

    public Module getMod() {
        return mod;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
