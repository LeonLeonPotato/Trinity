package me.leon.trinity.gui.button;

import com.sun.org.apache.bcel.internal.generic.FSUB;
import me.leon.trinity.gui.frame.FrameComponent;
import me.leon.trinity.gui.IComponent;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.GuiUtils;

import java.awt.*;

public abstract class IButton implements IComponent {
    protected final FrameComponent parent;
    protected int offset;
    protected boolean open;

    protected IButton(FrameComponent parent, int offset) {
        this.parent = parent;
        this.offset = offset;
        this.open = false;
    }

    protected void drawBack(Point p, String name, boolean enabled) {
        final float realY = parent.getY() + offset;
        drawRect(parent.getX(), realY, parent.getX() + getWidth(), realY + 14, getColor(p, enabled));
        FontUtil.drawString(name, parent.getX() + xOffset(), realY + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.nameColorButton.getValue());
    }

    protected Color getColor(Point point, boolean enabled) {
        if(onButton(point)) {
            if(enabled) {
                return ClickGUI.enabledColor.getValue().brighter();
            } else {
                return ClickGUI.disabledColor.getValue().brighter();
            }
        } else {
            if(enabled) {
                return ClickGUI.enabledColor.getValue();
            } else {
                return ClickGUI.disabledColor.getValue();
            }
        }
    }

    protected boolean onButton(Point point) {
        return GuiUtils.onButton(parent.getX(), parent.getY() + offset, parent.getX() + getWidth(), parent.getY() + offset + 14, point);
    }

    @Override
    public FrameComponent parent() {
        return parent;
    }

    @Override
    public float xOffset() {
        return 3;
    }

    public FrameComponent getParent() {
        return parent;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
