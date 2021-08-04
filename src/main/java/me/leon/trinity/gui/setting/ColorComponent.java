package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.ClickGui;
import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.leon.trinity.utils.rendering.skeet.Quad;

import java.awt.*;

/**
 * @author Leon
 *
 * fyi: color shouldn't have any sub settings
 */
public class ColorComponent extends ISetting<ColorSetting> {
    private boolean draggingHue = false;
    private boolean draggingAlpha = false;
    private boolean draggingSpeed = false;

    public ColorComponent(IComponent parent, ButtonComponent superParent, Setting set, int offset) {
        super(parent, superParent, set, offset);

        set.getSubSettings().clear(); // prevent color sub settings
        subs.clear();
    }

    @Override
    public void render(Point point) {
        if(!open) {
            drawBack(point, set.getName(), false);
            drawRect(getFrame().getX() + getWidth() - 13, getFrame().getY() + offset + 2, getFrame().getX() + getWidth() - 3, getFrame().getY() + offset + 12, set.getValue());
        } else {
            float realY = getFrame().getY() + offset;

            final float realX = getFrame().getX();
            final float width = getFrame().getWidth();
            final float[] hsb = Color.RGBtoHSB(set.getR(), set.getG(), set.getB(), new float[3]);

            drawRect(realX, realY, realX + getWidth(), realY + 14, getColor(point, false)); // background for name
            drawRect(realX, realY + 14, realX + getWidth(), realY + 14 + width + 13, ClickGUI.backgroundColor.getValue()); // background for the picker
            FontUtil.drawString(set.getName(), realX + xOffset(), realY + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.settingNameColor.getValue());

            RenderUtils.drawColorPickerSquare(realX + xOffset(), realY + 14, realX + width, realY + 14 + (width - xOffset()), hsb[0], set.getA());
            RenderUtils.drawOutlineRect(realX + xOffset(), realY + 14, realX + getWidth(), realY + 14 + (getWidth() - xOffset()), 1f, Color.WHITE);

            realY += 17 + (width - xOffset());

            RenderUtils.scissor(new Quad(realX + xOffset() - 1, realY - 1, realX + getWidth() + 1, realY + 11));
            int xOff = 0;
            while (xOff < realX + getWidth() + 10) {
                RenderUtils.drawRect(xOff, realY, xOff + 5, realY + 5, xOff % 2 == 0 ? new Color(0, 0, 0, 50) : new Color(255, 255, 255, 50));
                RenderUtils.drawRect(xOff, realY + 5, xOff + 5, realY + 10, xOff % 2 == 0 ? new Color(255, 255, 255, 50) : new Color(0, 0, 0, 50));
                xOff += 5;
            }
            RenderUtils.restoreScissor();

            RenderUtils.drawGradientRect(realX + xOffset(), realY, realX + getWidth(), realY + 10, new Color(0, 0, 0, 0), set.getValue(), new Color(0, 0, 0, 0), set.getValue());
            RenderUtils.drawOutlineRect(realX + xOffset(), realY, realX + getWidth(), realY + 10, 1f, Color.WHITE);

            realY += 13;

            RenderUtils.drawGradientRect(realX + xOffset(), realY, realX + getWidth(), realY + 10, new Color(0, 0, 0, 0), set.getValue(), new Color(0, 0, 0, 0), set.getValue());
            RenderUtils.drawOutlineRect(realX + xOffset(), realY, realX + getWidth(), realY + 10, 1f, Color.WHITE);
        }
    }

    @Override
    public void update(Point point) {

    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(onButton(point)) {
            if(button == 1) {
                open = !open;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        return false;
    }

    @Override
    public boolean keyTyped(char chr, int code) {
        return false;
    }

    @Override
    public float height() {
        if(open) {
            return (getWidth() + 14) + (13);
        } else return 14;
    }
}
