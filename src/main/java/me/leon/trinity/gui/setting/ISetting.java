package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.button.IButton;
import me.leon.trinity.gui.frame.FrameComponent;
import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.setting.rewrite.*;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.GuiUtils;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public abstract class ISetting<T extends Setting> implements IComponent {
    protected final ArrayList<ISetting<?>> subs;
    protected final IComponent parent;
    protected final IButton superParent;
    protected final T set;
    protected int offset;

    protected boolean open = false;

    protected long aniEnd;
    protected float p1, p2;

    @SuppressWarnings("unchecked")
    protected ISetting(IComponent parent, IButton superParent, Setting set, int offset) {
        this.parent = parent;
        this.superParent = superParent;
        this.set = (T) set;
        this.offset = offset;

        this.subs = new ArrayList<>();
        int off = offset + 14;
        if(set != null) {
            for(Setting s : set.getSubSettings()) {
                if(s instanceof ColorSetting)       subs.add(new ColorComponent(this, superParent, s, off));
                if(s instanceof BooleanSetting)     subs.add(new BooleanComponent(this, superParent, s, off));
                if(s instanceof ModeSetting)        subs.add(new ModeComponent(this, superParent, s, off));
                if(s instanceof SliderSetting)      subs.add(new SliderComponent(this, superParent, s, off));
                if(s instanceof KeybindSetting)     subs.add(new KeybindComponent(this, superParent, s, off));
                if(s instanceof TextBoxSetting)     subs.add(new TextBoxComponent(this, superParent, s, off));
                off += 14;
            }
        }
    }

    protected void drawBack(Point p, String name, boolean enabled) {
        final float realY = superParent.parent().getY() + offset;
        final float realX = superParent.parent().getX();

        drawRect(realX, realY, realX + getWidth(), realY + 14, getColor(p, enabled));
        FontUtil.drawString(name, realX + xOffset(), realY + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.settingNameColor.getValue());
    }

    protected void drawArrow() {
        final float progress = 1f - ((aniEnd - System.currentTimeMillis()) / 500f);
        final float y = superParent.getParent().getY() + offset;
        final float x = superParent.getParent().getX() + getWidth();
        if(progress < 1) {
            if(open) {
                p1 = y + 9 - (progress * 4);
                p2 = y + 5 + (progress * 4);
            } else {
                p1 = y + 5 + (progress * 4);
                p2 = y + 9 - (progress * 4);
            }
        } else {
            if(open) {
                p1 = y + 5;
                p2 = y + 9;
            } else {
                p1 = y + 9;
                p2 = y + 5;
            }
        }

        RenderUtils.drawLine(x - 7, p1, x - 5, p2, 1, Color.WHITE);
        RenderUtils.drawLine(x - 5, p2, x - 3, p1, 1, Color.WHITE);
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

    protected FrameComponent getFrame() {
        return superParent.parent();
    }

    protected boolean onButton(Point point) {
        return GuiUtils.onButton(superParent.parent().getX(), superParent.parent().getY() + offset, superParent.parent().getX() + getWidth(), superParent.parent().getY() + offset + 14, point);
    }

    protected void updateOffset() {
        int offset = 14 + this.offset;
        for (ISetting<?> button : subs) {
            button.setOffset(offset);
            offset += button.height();
        }
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

    @Override
    public float height() {
        int h = 14;
        if(open && !subs.isEmpty()) {
            for(IComponent c : subs) {
                h += c.height();
            }
        }
        return h;
    }

    @Override
    public float xOffset() {
        return parent.xOffset() + 3;
    }

    public ArrayList<ISetting<?>> getSubs() {
        return subs;
    }

    public IComponent getParent() {
        return parent;
    }

    public IButton getSuperParent() {
        return superParent;
    }

    public T getSet() {
        return set;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    protected ArrayList<ISetting<?>> getSets() {
        final ArrayList<ISetting<?>> toReturn = new ArrayList<>();
        for(ISetting<?> s : subs) {
            if(s.getSet().test()) {
                toReturn.add(s);
            }
        }
        return toReturn;
    }
}
