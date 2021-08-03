package me.leon.trinity.gui.setting;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.Setting;

import java.awt.*;

public class ModeComponent extends ISetting<ModeSetting> {
    public ModeComponent(IComponent parent, ButtonComponent superParent, Setting set, int offset) {
        super(parent, superParent, set, offset);
    }

    @Override
    public void render(Point point) {
        drawBack(point, set.getName() + " " + ChatFormatting.WHITE + set.getValue(), false);
        updateOffset();
        if(open) subs.forEach(e -> e.render(point));
    }

    @Override
    public void update(Point point) {
        if(open) subs.forEach(e -> e.update(point));
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
                    int v = set.getValues().indexOf(set.getValue());
                    if(v == set.getValues().size() - 1) v = 0; else v += 1;
                    set.setValue(set.getValues().get(v));
                    return true;
                }
            }
        }
        if(open) {
            for(IComponent c : subs) {
                if(c.buttonClick(button, point)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        if(open) {
            for (ISetting<?> sub : subs) {
                if (sub.buttonRelease(button, point)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char chr, int code) {
        if(open) {
            for (ISetting<?> sub : subs) {
                if (sub.keyTyped(chr, code)) return true;
            }
        }
        return false;
    }
}
