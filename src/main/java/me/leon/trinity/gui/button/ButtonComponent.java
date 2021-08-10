package me.leon.trinity.gui.button;

import me.leon.trinity.gui.frame.FrameComponent;
import me.leon.trinity.gui.setting.*;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.rewrite.*;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class ButtonComponent extends IButton {
    private final ArrayList<ISetting<?>> settings;
    private final Module mod;

    private long aniEnd;
    private float p1, p2;

    public ButtonComponent(FrameComponent parent, Module mod, int offset) {
        super(parent, offset);
        this.mod = mod;
        this.settings = new ArrayList<>();

        int s_off = offset + 14;
        for(Setting s : mod.getSettings()) {
            if(s instanceof ColorSetting)       settings.add(new ColorComponent(this, this, s, s_off));
            if(s instanceof BooleanSetting)     settings.add(new BooleanComponent(this, this, s, s_off));
            if(s instanceof SliderSetting)      settings.add(new SliderComponent(this, this, s, s_off));
            if(s instanceof KeybindSetting)     settings.add(new KeybindComponent(this, this, s, s_off));
            if(s instanceof ModeSetting)        settings.add(new ModeComponent(this, this, s, s_off));
            if(s instanceof TextBoxSetting)     settings.add(new TextBoxComponent(this, this, s, s_off));
            s_off += 14;
        }
        settings.add(new KeybindComponent(this, this, s_off));
        s_off += 14;
        settings.add(new ModeComponent(this, this, s_off));
    }

    @Override
    public void render(Point point) {
        drawBack(point, mod.getName(), mod.isEnabled());

        final float progress = 1f - ((aniEnd - System.currentTimeMillis()) / 500f);
        final float y = getParent().getY() + offset;
        final float x = getParent().getX() + getWidth();
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

        updateOffset();
        if(open) {
            getSets().forEach(e -> { e.render(point);});
        }
    }

    @Override
    public void update(Point point) {
        if(open) settings.forEach(e -> e.update(point));
    }

    @Override
    public void unload() {
        settings.forEach(ISetting::unload);
    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(onButton(point)) {
            switch (button) {
                case 0: {
                    mod.toggle();
                    break;
                }
                case 1: {
                    open = !open;
                    aniEnd = System.currentTimeMillis() + 500;
                    break;
                }
            }
            return true;
        }
        if(open) {
            for (ISetting<?> set : getSets()) {
                if (set.buttonClick(button, point)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        if(open) {
            for(ISetting<?> set : getSets()) {
                if(set.buttonRelease(button, point)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char chr, int code) {
        if(open) {
            for(ISetting<?> set : getSets()) {
                if(set.keyTyped(chr, code)) return true;
            }
        }
        return false;
    }

    @Override
    public float height() {
        if(open) {
            int h = 14;
            for(ISetting<?> s : getSets()) { h += s.height(); }
            return h;
        } else return 14;
    }

    @Override
    public String description() {
        return mod.getDescription();
    }

    private void updateOffset() {
        int offset = getOffset() + 14;
        for(ISetting<?> s : getSets()) {
            s.setOffset(offset);
            offset += s.height();
        }
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

    private ArrayList<ISetting<?>> getSets() {
        final ArrayList<ISetting<?>> toReturn = new ArrayList<>();
        for(ISetting<?> s : settings) {
            if(s.getSet() == null) {
                toReturn.add(s);
                continue;
            }

            if(s.getSet().test()) {
                toReturn.add(s);
            }
        }
        return toReturn;
    }
}
