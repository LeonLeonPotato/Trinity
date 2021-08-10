package me.leon.trinity.gui.frame;

import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.IButton;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.hud.Component;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.GuiUtils;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public abstract class IFrame implements IComponent {
    protected float x, y, lastY, dragX, dragY;
    protected boolean open, dragging;
    protected Category category;
    protected ArrayList<IButton> buttons;

    protected IFrame(Category c, float x, float y){
        this.category = c;
        this.x = x;
        this.y = y;
        this.dragging = false;
        this.open = false;
        this.buttons = new ArrayList<>();
    }

    @Override
    public float height() {
        float h = 15;
        if(open) {
            for(IButton b : buttons) {
                h += b.height();
            }
        }
        return h;
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

    protected boolean onButton(Point point) {
        return GuiUtils.onButton(x, y, x + getWidth(), y + 15, point);
    }

    protected void drawBack() {
        drawRect(x, y, x + getWidth(), y + 14, ClickGUI.frameColor.getValue());

        switch (ClickGUI.frameSeparator.getValue()) {
            case "Normal": {
                RenderUtils.drawRect(x, y + 14, x + getWidth(), y + 15, ClickGUI.separatorColor.getValue());
                break;
            }
            case "Rolling": {
                RenderUtils.drawRainbowRectHorizontal(x, y + 14, x + getWidth(), y + 15, (int) (ClickGUI.separatorColor.speed / 5f), ClickGUI.separatorColor.getA(), ClickGUI.separatorColor.getValue(), true);
                break;
            }
            default: {
                break;
            }
        }

        FontUtil.drawString(category.name(), x + 5, y + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.nameColorButton.getValue());
    }

    protected void updateOffset() {
        int offset = 15;
        for (IButton button : buttons) {
            button.setOffset(offset);
            offset += button.height();
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getLastY() {
        return lastY;
    }

    public void setLastY(float y) {
        this.lastY = y;
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

    public ArrayList<IButton> getButtons() {
        return buttons;
    }

    public boolean isDragging() {
        return dragging;
    }

    public IFrame setDragging(boolean dragging) {
        this.dragging = dragging;
        return this;
    }

    public boolean isOpen() {
        return open;
    }

    public IFrame setOpen(boolean open) {
        this.open = open;
        return this;
    }
}
