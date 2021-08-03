package me.leon.trinity.gui.frame;

import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.gui.button.IButton;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.utils.rendering.GuiUtils;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FrameComponent extends IFrame {

    public FrameComponent(Category c, float x, float y) {
        super(c, x, y);

        int offset = 15;
        for(Module mod : ModuleManager.getMods(category)) {
            buttons.add(new ButtonComponent(this, mod, offset));
            offset += 14;
        }
    }

    @Override
    public void render(Point point) {
        drawBack();
        if(open) {
            updateOffset();
            buttons.forEach(e -> e.render(point));
        }
    }

    @Override
    public void update(Point point) {
        if(dragging) {
            x = point.x - dragX;
            y = point.y - dragY;
        }
        if(open) buttons.forEach(e -> e.update(point));
    }

    @Override
    public void unload() {
        dragging = false;
    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if (GuiUtils.onButton(x, y, x + getWidth(), y + 15, point)) {
            switch (button) {
                case 0: {
                    dragging = true;
                    dragX = point.x - x;
                    dragY = point.y - y;
                    return true;
                }
                case 1: {
                    open = !open;
                    return true;
                }
                default: {
                    break;
                }
            }
        }
        updateOffset();
        if(open) {
            for(IButton c : buttons) {
                if(c.buttonClick(button, point)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        dragging = false;

        if(open) {
            for (IButton c : buttons) {
                if (c.buttonRelease(button, point))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char chr, int code) {
        if(open) {
            for (IButton c : buttons) {
                if (c.keyTyped(chr, code)) return true;
            }
        }
        return false;
    }
}
