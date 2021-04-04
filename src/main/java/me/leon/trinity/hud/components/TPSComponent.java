package me.leon.trinity.hud.components;

import me.leon.trinity.hud.Component;
import me.leon.trinity.managers.TickrateManager;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.Coloring;
import net.minecraft.client.Minecraft;

public class TPSComponent extends Component {
    public TPSComponent() {
        super("TPSComponent");
        this.visible = true;
        this.x = 0;
        this.y = res.getScaledHeight() / 2f - 10;
    }

    @Override
    public void render() {
        drawBackground();
        FontUtil.drawString("TPS: " + Coloring.getWHITE() + TickrateManager.getTPS(), this.x + 1, this.y + 1, getTextColor());
    }

    @Override
    public float width() {
        return FontUtil.getStringWidth("TPS: " + Coloring.getWHITE() + TickrateManager.getTPS()) + 3;
    }

    @Override
    public float height() {
        return FontUtil.getFontHeight();
    }

    @Override
    public boolean ButtonCheck(float x, float y, float w, float h, int mX, int mY) {
        return mX >= x && mX <= (x + w) && mY >= y - 1 && mY <= (y + h);
    }
}
