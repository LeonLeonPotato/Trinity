package me.leon.trinity.gui.setting;

import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.button.ButtonComponent;
import me.leon.trinity.gui.button.IButton;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.setting.rewrite.TextBoxSetting;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.GuiUtils;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.leon.trinity.utils.rendering.skeet.Quad;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatAllowedCharacters;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class TextBoxComponent extends ISetting<TextBoxSetting> {
    private boolean typing = false;

    private long aniEnd;
    private float strOffset;
    private boolean hasmoved;
    private int movetype; //0 = normal, 1 = delete
    private char deletedChar;

    public TextBoxComponent(IComponent parent, IButton superParent, Setting set, int offset) {
        super(parent, superParent, set, offset);
    }

    @Override
    public void render(Point point) {
        final float realY = superParent.parent().getY() + offset;
        final float realX = superParent.parent().getX();

        drawRect(realX, realY, realX + getWidth(), realY + 28, getColor(point, false));

        FontUtil.drawString(set.getName(), realX + xOffset() + (((getWidth() - xOffset()) - FontUtil.getStringWidth(set.getName())) / 2f), realY + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.settingNameColor.getValue());

        RenderUtils.scissor(new Quad(realX + xOffset() - 1, realY, realX + getWidth(), realY + 29));
        String one = set.getValue().substring(0, (int) MathUtils.clamp(0, set.getValue().length(), set.typeSpace));
        String two = set.getValue().substring((int) MathUtils.clamp(0, set.getValue().length(), set.typeSpace));
        updateStrOff();
        strOffset = (float) MathUtils.clamp(-1, Integer.MAX_VALUE, strOffset);
        FontUtil.drawString(one, realX + xOffset() - strOffset, realY + 14 + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.settingNameColor.getValue());
        float one_ = FontUtil.getStringWidth(one);
        if(typing && System.currentTimeMillis() % 800 > 400) RenderUtils.drawLine(realX + xOffset() + one_ - strOffset, realY + 14, realX + xOffset() + one_ - strOffset, realY + 28, 1f, Color.WHITE);
        FontUtil.drawString(two, realX + xOffset() - strOffset + one_ + 1, realY + 14 + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.settingNameColor.getValue());

        float progress = (float) (MathUtils.clamp(0, 1, (System.currentTimeMillis() - aniEnd) / 500f));
        final float half = ((getWidth() - xOffset()) / 2f);
        if(!typing) progress = 1 - progress;
        if(progress != 0)
        RenderUtils.drawLine((realX + xOffset() + half) - (progress * half), realY + 27, (realX + xOffset() + half) + (progress * half), realY + 27, 1f, new Color(255, 255, 255, 255));

        RenderUtils.restoreScissor();
        if(open) getSets().forEach(e -> e.render(point));
    }

    @Override
    public void update(Point point) {
    }

    @Override
    public boolean buttonClick(int button, Point point) {
        if(onButton(point)) {
            switch (button) {
                case 0: {
                    if(!typing) {
                        typing = true;
                        aniEnd = System.currentTimeMillis();
                    }
                    return true;
                }
                case 1: {
                    open = !open;
                    return true;
                }
            }
        } else if (typing) { typing = false; aniEnd = System.currentTimeMillis(); }

        for (ISetting<?> sub : getSets()) {
            if(sub.buttonClick(button, point)) return true;
        }
        return false;
    }

    @Override
    public boolean buttonRelease(int button, Point point) {
        for (ISetting<?> sub : getSets()) {
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
                        set.typeSpace--;
                        hasmoved = true;
                        movetype = 1;
                        deletedChar = set.getValue().charAt(typeSpace - 1);
                        set.setValue(one + two);
                    }
                    break;
                }
                case Keyboard.KEY_RIGHT: {
                    if(typeSpace < set.getValue().length()) {
                        set.typeSpace++;
                        hasmoved = true;
                        movetype = 0;
                    }
                    break;
                }
                case Keyboard.KEY_LEFT: {
                    if(typeSpace != 0) {
                        set.typeSpace--;
                        hasmoved = true;
                        movetype = 0;
                    }
                    break;
                }
                default: {
                    if(ChatAllowedCharacters.isAllowedCharacter(chr)) {
                        String one = set.getValue().substring(0, typeSpace);
                        String two = set.getValue().substring(typeSpace);
                        set.setValue(one + chr + two);
                        set.typeSpace++;
                        hasmoved = true;
                        movetype = 0;
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
            for(ISetting<?> s : getSets()) h += s.height();
            return h;
        } else return 28;
    }

    @Override
    protected boolean onButton(Point point) {
        return GuiUtils.onButton(superParent.parent().getX(), superParent.parent().getY() + offset, superParent.parent().getX() + getWidth(), superParent.parent().getY() + offset + 28, point);
    }

    private void updateStrOff() {
        final String before = split(set.typeSpace)[0];
        final float widthBefore = FontUtil.getStringWidth(before);
        final float width = getWidth() - xOffset() - 2;

        if(set.typeSpace == 0) {
            strOffset = 0;
            return;
        }

        if(hasmoved) {
            if(movetype == 0) {
                if(widthBefore > strOffset + width) {
                    strOffset = widthBefore - width;
                } else if(widthBefore < strOffset) {
                    StringBuilder b = new StringBuilder();
                    for(int a = before.length() - 1; a > 0; a--) {
                        if(strOffset - FontUtil.getStringWidth(b.toString()) <= widthBefore) break;
                        b.append(before.charAt(a));
                    }
                    strOffset -= FontUtil.getStringWidth(StringUtils.reverse(b.toString()));
                }
            } else if (movetype == 1) {
                strOffset -= FontUtil.getStringWidth(Character.toString(deletedChar));
            }
            hasmoved = false;
            movetype = -1;
        }
    }

    private String[] split(int c) {
        return new String[] {
                set.getValue().substring(0, (int) MathUtils.clamp(0, set.getValue().length(), c)), set.getValue().substring((int) MathUtils.clamp(0, set.getValue().length(), c))
        };
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}
