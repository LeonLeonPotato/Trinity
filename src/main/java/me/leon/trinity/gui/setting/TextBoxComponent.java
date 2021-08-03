package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.setting.rewrite.TextBoxSetting;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.GuiUtils;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.leon.trinity.utils.rendering.skeet.Quad;
import net.minecraft.util.ChatAllowedCharacters;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class TextBoxComponent extends ISetting<TextBoxSetting> {
    private boolean typing = false;

    private long aniEnd;

    public TextBoxComponent(IComponent parent, ButtonComponent superParent, Setting set, int offset) {
        super(parent, superParent, set, offset);
    }

    @Override
    public void render(Point point) {
        RenderUtils.scissor(new Quad(getFrame().getX(), getFrame().getY() + offset, getFrame().getX() + getWidth(), getFrame().getY() + offset + 28));

        final float realY = superParent.parent().getY() + offset;
        final float realX = superParent.parent().getX();

        drawRect(realX, realY, realX + getWidth(), realY + 28, getColor(point, false));

        FontUtil.drawString(set.getName(), realX + xOffset() + (((getWidth() - xOffset()) - FontUtil.getStringWidth(set.getName())) / 2f), realY + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.settingNameColor.getValue());
        String one = set.getValue().substring(0, (int) MathUtils.clamp(0, set.getValue().length(), set.typeSpace));
        String two = set.getValue().substring((int) MathUtils.clamp(0, set.getValue().length(), set.typeSpace));
        FontUtil.drawString(one, realX + xOffset(), realY + 14 + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.settingNameColor.getValue());
        final float one_ = FontUtil.getStringWidth(one);
        if(typing && (System.currentTimeMillis() % 1000) / 1000f > 0.5)
            drawRect(realX + xOffset() + one_, realY + 14, realX + xOffset() + one_ + 1, realY + 28, new Color(255, 255, 255, 255));

        FontUtil.drawString(two, realX + xOffset() + one_ + 1, realY + 14 + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.settingNameColor.getValue());

        float progress = (float) (MathUtils.clamp(0, 1, (System.currentTimeMillis() - aniEnd) / 500f));
        final float half = ((getWidth() - xOffset()) / 2f);
        if(!typing) progress = 1 - progress;
        if(progress != 0)
        RenderUtils.drawLine((realX + xOffset() + half) - (progress * half), realY + 27, (realX + xOffset() + half) + (progress * half), realY + 27, 1f, new Color(255, 255, 255, 255));

        //RenderUtils.drawGradientLine(realX + xOffset(), realY, realX + getWidth() - 3, realY, 1f, RenderUtils.lower(Color.WHITE, 50), new Color(0, 0, 0, 0));
        //RenderUtils.drawGradientLine(realX + xOffset(), realY + 27, realX + getWidth() - 3, realY + 27, 1f, RenderUtils.lower(Color.WHITE, 50), new Color(0, 0, 0, 0));

        /*
        RenderUtils.drawGradientLine(
                realX + xOffset(),
                realY + ((28 - FontUtil.getFontHeight()) / 2f) + FontUtil.getFontHeight(),
                realX + getWidth() - 3,
                realY + ((28 - FontUtil.getFontHeight()) / 2f) + FontUtil.getFontHeight(),
                1f,
                RenderUtils.lower(Color.WHITE, 50), new Color(0, 0, 0, 0)
        );

         */
        RenderUtils.restoreScissor();
    }

    @Override
    public void update(Point point) {
    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(onButton(point)) {
            switch (button) {
                case 0: {
                    typing = true;
                    aniEnd = System.currentTimeMillis();
                    return true;
                }
                case 1: {
                    open = !open;
                    return true;
                }
            }
        } else { typing = false; aniEnd = System.currentTimeMillis(); }
        for (ISetting<?> sub : subs) {
            if(sub.buttonClick(button, point)) return true;
        }
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        for (ISetting<?> sub : subs) {
            if(sub.buttonRelease(button, point)) return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char chr, int code) {
        if(typing) {
            final int typeSpace = set.typeSpace;
            switch (code) {
                case 14: {
                    if(typeSpace >= 1) { // backspace
                        String one = set.getValue().substring(0, typeSpace - 1);
                        String two = set.getValue().substring(typeSpace);
                        set.setValue(one + two);
                        set.typeSpace--;
                    }
                    break;
                }
                case Keyboard.KEY_RIGHT: {
                    if(typeSpace < set.getValue().length()) {
                        set.typeSpace++;
                    }
                    break;
                }
                case Keyboard.KEY_LEFT: {
                    if(typeSpace != 0) {
                        set.typeSpace--;
                    }
                    break;
                }
                default: {
                    if(ChatAllowedCharacters.isAllowedCharacter(chr)) {
                        String one = set.getValue().substring(0, typeSpace);
                        String two = set.getValue().substring(typeSpace);
                        set.setValue(one + chr + two);
                        set.typeSpace++;
                    }
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public float height() {
        if(open) {
            int h = 28;
            for(ISetting<?> s : subs) h += s.height();
            return h;
        } else return 28;
    }

    @Override
    protected boolean onButton(Point point) {
        return GuiUtils.onButton(superParent.parent().getX(), superParent.parent().getY() + offset, superParent.parent().getX() + getWidth(), superParent.parent().getY() + offset + 28, point);
    }
}
