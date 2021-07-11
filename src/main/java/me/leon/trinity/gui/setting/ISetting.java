package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.FrameComponent;
import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.GuiUtils;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public abstract class ISetting<T extends Setting> implements IComponent {
    protected final ArrayList<IComponent> subs;
    protected final IComponent parent;
    protected final ButtonComponent superParent;
    protected final T set;
    protected final int offset;

    @SuppressWarnings("unchecked")
    protected ISetting(IComponent parent, ButtonComponent superParent, Setting set, int offset) {
        this.parent = parent;
        this.superParent = superParent;
        this.set = (T) set;
        this.offset = offset;

        this.subs = new ArrayList<>();
    }

    protected void drawBack(Point p, String name, boolean enabled) {
        final float realY = superParent.parent().getY() + offset;
        final float realX = superParent.parent().getX();

        RenderUtils.drawRect(realX, realY, realX + getWidth(), realY + 14, getColor(p, enabled));
        FontUtil.drawString(name, realX + xOffset(), realY - ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.nameColorButton.getValue());
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
        return GuiUtils.onButton(superParent.parent().getX(), superParent.parent().getY() + offset, superParent.parent().getX() + getWidth(), superParent.parent().getY() + offset + 14, point);
    }

    @Override
    public void unload() {
        subs.forEach(IComponent::unload);
    }

    @Override
    public IComponent parent() {
        return parent;
    }

    @Override
    public String description() {
        return null;
    }
}
