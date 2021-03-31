package me.leon.trinity.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public abstract class Component {
    public float x, y;
    public boolean dragging;
    public boolean visible;
    protected Minecraft mc = Minecraft.getMinecraft();
    public ScaledResolution res = new ScaledResolution(mc);

    public abstract void render();
    public abstract float width();
    public abstract float height();
    public boolean onButton(int x, int y) {
        return ButtonCheck(this.x, this.y, width(), height(), x, y);
    }

    protected boolean pCheck() {
        return mc.player == null;
    }

    protected boolean wCheck() {
        return mc.world == null;
    }

    protected boolean nullCheck() {
        return (mc.world == null && mc.player == null);
    }

    public boolean ButtonCheck(float x, float y, float w, float h, int mX, int mY) {
        return mX > x && mX < (x + w) && mY > y && mY < (y + h);
    }
}
