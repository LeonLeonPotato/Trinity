package me.leon.trinity.gui;

import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.utils.rendering.GuiUtils;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class FrameComponent implements IComponent {
    private float x, y;
    private final Category category;
    private boolean dragging = false;
    private boolean open = false;
    private float dragX, dragY;
    private final ArrayList<ButtonComponent> buttons;

    public FrameComponent(Category category, float x, float y) {
        this.category = category;
        this.x = x;
        this.y = y;

        this.buttons = new ArrayList<>();

        int offset = 0;
        for(Module e : ModuleManager.getMods(category)) {
            buttons.add(new ButtonComponent(this, e, offset));
            offset += 14;
        }
    }

    @Override
    public void render(Point point) {
        RenderUtils.drawRect(x, y, x + getWidth(), y + 14, ClickGUI.frameColor.getValue());

        switch (ClickGUI.frameSeparator.getValue()) {
            case "Normal": {
                RenderUtils.drawRect(x, y + 14, x + getWidth(), y + 15, ClickGUI.separatorColor.getValue());
                break;
            }
            case "Rolling": {
                RenderUtils.drawRainbowRectHorizontal(x, y + 14, x + getWidth(), y + 15, (float) ClickGUI.separatorColor.speed, ClickGUI.separatorColor.getA(), ClickGUI.separatorColor.getValue());
                break;
            }
            default: {
                break;
            }
        }

        buttons.forEach(e -> e.render(point));
    }

    @Override
    public void update(Point point) {
        if(dragging) {
            x = point.x - dragX;
            y = point.y - dragY;
        }
        buttons.forEach(e -> e.update(point));
    }

    @Override
    public void unload() {
        dragging = false;
    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(button == 0) {
            if(GuiUtils.onButton(x, y, x + getWidth(), y + 15, point)) {
                dragging = true;
                dragX = point.x - x;
                dragY = point.y - y;
                return true;
            }
            for(ButtonComponent c : buttons) {
                if(c.buttonClick(button, point)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        dragging = false;

        for(ButtonComponent c :  buttons) {
            if(c.buttonRelease(button, point))
                return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(int code) {
        for(ButtonComponent c : buttons) {
            if(c.keyTyped(code)) return true;
        }
        return false;
    }

    @Override
    public float height() {
        int a = 15;
        for(ButtonComponent c : buttons) {
            a += c.height();
        }
        return a;
    }

    @Override
    public float xOffset() {
        return 0;
    }

    @Override
    public IComponent parent() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Category getCategory() {
        return category;
    }

    public ArrayList<ButtonComponent> getButtons() {
        return buttons;
    }

    public boolean isDragging() {
        return dragging;
    }

    public FrameComponent setDragging(boolean dragging) {
        this.dragging = dragging;
        return this;
    }

    public boolean isOpen() {
        return open;
    }

    public FrameComponent setOpen(boolean open) {
        this.open = open;
        return this;
    }
}
