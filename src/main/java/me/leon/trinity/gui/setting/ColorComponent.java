package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.gui.button.IButton;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.GuiUtils;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.leon.trinity.utils.rendering.skeet.Quad;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.awt.*;

/**
 * @author Leon
 *
 * fyi: color shouldn't have any sub settings
 */
public class ColorComponent extends ISetting<ColorSetting> {
    private boolean draggingPicker = false;
    private boolean draggingHue = false;
    private boolean draggingAlpha = false;
    private boolean draggingSpeed = false;

    private float pickerPosMin, pickerPosMax, huePosMin, huePosMax, alphaPosMin, alphaPosMax, speedPosMin, speedPosMax, rainbowMin, rainbowMax;

    public ColorComponent(IComponent parent, IButton superParent, Setting set, int offset) {
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

            drawRect(realX, realY, realX + getWidth(), realY + 14, getColor(point, false)); // background for name
            drawRect(realX, realY + 14, realX + getWidth(), realY + 14 + width + 13 + 13 + 14, ClickGUI.backgroundColor.getValue()); // background for the picker
            drawRect(realX + getWidth() - 13, realY + 2, realX + getWidth() - 3, realY + 12, set.getValue());
            FontUtil.drawString(set.getName(), realX + parent.xOffset() + 3, realY + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.settingNameColor.getValue());

            RenderUtils.drawColorPickerSquare(realX + xOffset(), realY + 14, realX + width, realY + 14 + (width - xOffset()), set.hue, set.getA());
            RenderUtils.drawOutlineRect(realX + xOffset(), realY + 14, realX + getWidth(), realY + 14 + (getWidth() - xOffset()), 1f, Color.WHITE);
            pickerPosMin = realY + 14;
            pickerPosMax = realY + 14 + (getWidth() - xOffset());

            RenderUtils.scissor(new Quad(realX + xOffset(), realY + 15, realX + getWidth(), realY + 14 + (getWidth() - xOffset())));
            RenderUtils.drawCircle(realX + xOffset() + (set.s * (width - xOffset())), realY + 14 + ((1 - set.br) * (width - xOffset())), 3, 1, Color.WHITE);
            RenderUtils.restoreScissor();

            realY += 17 + (width - xOffset());

            RenderUtils.drawHueRect(realX + xOffset(), realY, getWidth() - xOffset(), 10);
            RenderUtils.drawRect(realX + xOffset() + GuiUtils.sliderWidth(0, set.hue, 1, getWidth() - xOffset() - 1), realY, realX + xOffset() + 1 + GuiUtils.sliderWidth(0, (float) set.hue, 1, getWidth() - xOffset() - 1), realY + 10, Color.BLACK);
            RenderUtils.drawOutlineRect(realX + xOffset(), realY, realX + getWidth(), realY + 10, 1f, Color.WHITE);
            huePosMin = realY;
            huePosMax = realY + 10;

            realY += 13;

            RenderUtils.scissor(new Quad(realX + xOffset() - 1, realY - 1, realX + getWidth(), realY + 11));
            int xOff = 0;
            while (xOff < realX + getWidth() + 10) {
                RenderUtils.drawRect(xOff, realY, xOff + 5, realY + 5, xOff % 2 == 0 ? new Color(0, 0, 0, 50) : new Color(255, 255, 255, 50));
                RenderUtils.drawRect(xOff, realY + 5, xOff + 5, realY + 10, xOff % 2 == 0 ? new Color(255, 255, 255, 50) : new Color(0, 0, 0, 50));
                xOff += 5;
            }
            RenderUtils.restoreScissor();

            RenderUtils.drawGradientRect(realX + xOffset(), realY, realX + getWidth(), realY + 10, new Color(0, 0, 0, 0), RenderUtils.alpha(set.getValue(), 255), new Color(0, 0, 0, 0), RenderUtils.alpha(set.getValue(), 255));
            RenderUtils.drawRect(realX + xOffset() + GuiUtils.sliderWidth(0, set.getA(), 255, getWidth() - xOffset() - 1), realY, realX + xOffset() + 1 + GuiUtils.sliderWidth(0, set.getA(), 255, getWidth() - xOffset() - 1), realY + 10, Color.BLACK);
            RenderUtils.drawOutlineRect(realX + xOffset(), realY, realX + getWidth(), realY + 10, 1f, Color.WHITE);
            alphaPosMin = realY;
            alphaPosMax = realY + 10;

            realY += 13;

            RenderUtils.drawRect(realX + xOffset() + 13, realY, realX + xOffset() + 13 + GuiUtils.sliderWidth(0, (float) set.speed, 5, getWidth() - xOffset()), realY + 10, ClickGUI.sliderColor.getValue());
            FontUtil.drawString("Speed " + set.speed, realX + xOffset() + 16, realY + (10 - FontUtil.getFontHeight()) / 2f, Color.WHITE);
            RenderUtils.drawOutlineRect(realX + xOffset() + 13, realY, realX + getWidth(), realY + 10, 1f, Color.WHITE);
            speedPosMin = realY;
            speedPosMax = realY + 10;

            if(set.rainbow) RenderUtils.drawRect(realX + xOffset(), realY, realX + xOffset() + 10, realY + 10, new Color(0xff98ff98)); // ZWare.cc green lol
            RenderUtils.drawOutlineRect(realX + xOffset(), realY, realX + xOffset() + 10, realY + 10, 1f, Color.WHITE);
            rainbowMin = speedPosMin; // useless?
            rainbowMax = speedPosMax;

        }
    }

    @Override
    public void update(Point point) {
        final float realX = getFrame().getX();
        final float realY = getFrame().getY() + offset;

        if(draggingSpeed) {
            final float f1 = (float) GuiUtils.slider(0, 20, point.x, realX + xOffset() + 13, getWidth() - xOffset(), 0);
            set.speed = MathUtils.roundToPlace(f1 / 4f, 2);
        }
        if(draggingPicker) {
            set.s = (float) MathUtils.clamp(0, 1, (point.x - (realX + xOffset())) / (getWidth() - xOffset()));
            set.br = (float) (1 - MathUtils.clamp(0, 1, (point.y - (realY + 14)) / (getWidth() - xOffset())));
        }
        if(draggingHue) {
            set.hue = ((float) GuiUtils.slider(0, 360, point.x, realX + xOffset(), getWidth() - xOffset(), 2)) / 360f;
        }
        if(draggingAlpha) {
            set.setA((int) GuiUtils.slider(0, 255, point.x, realX + xOffset(), getWidth() - xOffset(), 0));
        }

        final Color color = new Color(Color.HSBtoRGB(set.hue, set.s, set.br));
        set.setR(color.getRed()).setG(color.getGreen()).setB(color.getBlue());
    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(onButton(point)) {
            if(button == 1) {
                open = !open;
            }
            return true;
        }

        if(open) {
            if(onSpeed(point) && button == 0) {
                draggingSpeed = true;
                return true;
            }
            if(onRainbow(point) && button == 0) {
                set.rainbow = !set.rainbow;
                return true;
            }
            if(onPicker(point) && button == 0) {
                draggingPicker = true;
                return true;
            }
            if(onHue(point) && button == 0) {
                draggingHue = true;
                return true;
            }
            if(onAlpha(point) && button == 0) {
                draggingAlpha = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        draggingPicker = false;
        draggingHue = false;
        draggingSpeed = false;
        draggingAlpha = false;
        return false;
    }

    @Override
    public boolean keyTyped(char chr, int code) {
        return false;
    }

    @Override
    public float height() {
        if(open) {
            return (getWidth() + 14) + (13) + (13) + (14);
        } else return 14;
    }

    @Override
    public float xOffset() {
        return parent.xOffset() + 3;
    }

    private boolean onSpeed(Point p) {
        return GuiUtils.onButton(getFrame().getX() + xOffset() + 13, speedPosMin, getFrame().getX() + getWidth(), speedPosMax, p);
    }

    private boolean onRainbow(Point p) {
        return GuiUtils.onButton(getFrame().getX() + xOffset(), rainbowMin, getFrame().getX() + xOffset() + 10, rainbowMax, p);
    }

    private boolean onPicker(Point p) {
        return GuiUtils.onButton(getFrame().getX() + xOffset(), pickerPosMin, getFrame().getX() + getWidth(), pickerPosMax, p);
    }

    private boolean onHue(Point p) {
        return GuiUtils.onButton(getFrame().getX() + xOffset(), huePosMin, getFrame().getX() + getWidth(), huePosMax, p);
    }

    private boolean onAlpha(Point p) {
        return GuiUtils.onButton(getFrame().getX() + xOffset(), alphaPosMin, getFrame().getX() + getWidth(), alphaPosMax, p);
    }
}
