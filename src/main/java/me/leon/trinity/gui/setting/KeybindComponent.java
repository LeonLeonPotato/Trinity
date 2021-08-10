package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.gui.button.IButton;
import me.leon.trinity.setting.rewrite.KeybindSetting;
import me.leon.trinity.setting.rewrite.Setting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class KeybindComponent extends ISetting<KeybindSetting> {
    private boolean binding;

    public KeybindComponent(IComponent parent, ButtonComponent superParent, int offset) {
        super(parent, superParent, null, offset);
        this.binding = false;
    }

    public KeybindComponent(IComponent parent, IButton superParent, Setting set, int offset) {
        super(parent, superParent, set, offset);
        this.binding = false;
    }

    @Override
    public void render(Point point) {
        if(set != null) {
            drawBack(point, binding ? "Binding" + getDots() : "Bind: " + Keyboard.getKeyName(set.getKeycode()), false);
            if(open) getSets().forEach(e -> e.render(point));
        } else {
            drawBack(point, binding ? "Binding" + getDots() : "Bind: " + Keyboard.getKeyName(((ButtonComponent) superParent).getMod().getKey()), false);
        }
    }

    private String getDots() {
        String x = "";
        if(binding) {
            final float progress = ((int) (System.currentTimeMillis() % 3000L)) / 1000f;
            if(progress > 0 && progress <= 1) {
                x = ".";
            } else
            if(progress > 1 && progress <= 2) {
                x = "..";
            } else
            if(progress > 2 && progress <= 3) {
                x = "...";
            }
        }
        return x;
    }

    @Override
    public void update(Point point) {
        if(open) getSets().forEach(e -> e.update(point));
    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(onButton(point)) {
            switch (button) {
                case 0: {
                    binding = !binding;
                    return true;
                }
                case 1: {
                    open = !open;
                    return true;
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
        if(set != null) {
            if(open) {
                for (ISetting<?> sub : getSets()) {
                    if (sub.keyTyped(chr, code)) return true;
                }
            }
        } else if(binding) {
            ((ButtonComponent) superParent).getMod().setKey(code == Keyboard.KEY_DELETE ? Keyboard.KEY_NONE : code);
            binding = false;
            return true;
        }
        return false;
    }
}
