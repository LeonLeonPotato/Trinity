package me.leon.trinity.gui.setting;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.gui.button.IButton;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.Setting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ModeComponent extends ISetting<ModeSetting> {
    public ModeComponent(IComponent parent, ButtonComponent superParent, int offset) {
        super(parent, superParent, null, offset);
    }

    public ModeComponent(IComponent parent, IButton superParent, Setting set, int offset) {
        super(parent, superParent, set, offset);
    }

    @Override
    public void render(Point point) {
        if(set != null) {
            drawBack(point, set.getName() + " " + ChatFormatting.WHITE + set.getValue(), false);
            updateOffset();
            if(open) getSets().forEach(e -> e.render(point));
        } else drawBack(point, "Visible: " + (((ButtonComponent) superParent).getMod().isVisible() ? "True" : "False"), false);
    }

    @Override
    public void update(Point point) {
        if(open) getSets().forEach(e -> e.update(point));
    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(onButton(point)) {
            switch (button) {
                case 1: {
                    open = !open;
                    return true;
                }
                case 0: {
                    if(set != null) {
                        int v = set.getValues().indexOf(set.getValue());
                        if(v == set.getValues().size() - 1) v = 0; else v += 1;
                        set.setValue(set.getValues().get(v));
                        return true;
                    } else {
                        ((ButtonComponent) superParent).getMod().setVisible(!((ButtonComponent) superParent).getMod().isVisible());
                    }
                }
            }
        }
        if(open) {
            for(IComponent c : getSets()) {
                if(c.buttonClick(button, point)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        if(open) {
            for (ISetting<?> sub : getSets()) {
                if (sub.buttonRelease(button, point)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char chr, int code) {
        if(open) {
            for (ISetting<?> sub : getSets()) {
                if (sub.keyTyped(chr, code)) return true;
            }
        }
        return false;
    }
}
