package me.leon.trinity.clickgui.components.sub.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.sub.SubSetting;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;

public class SubBoolean extends Component {
    public me.leon.trinity.setting.settings.sub.SubBoolean set;
    public SubSetting parent;
    public int offset;

    public SubBoolean(me.leon.trinity.setting.settings.sub.SubBoolean set, SubSetting parent, int offset) {
        this.set = set;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.parent.parent.x + ClickGui.width, this.parent.parent.parent.y + this.parent.offset + this.parent.parent.offset + this.offset + 14 + 14 + 14, this.parent.parent.parent.x, this.parent.parent.parent.y + this.parent.offset + this.parent.parent.offset + this.offset + 14 + 14, ClickGUI.backgroundColor.getValue());
        if(set.getValue()) {
            RenderUtils.drawRect(this.parent.parent.parent.x + ClickGui.width, this.parent.parent.parent.y + this.parent.offset + this.parent.parent.offset + this.offset + 14 + 14 + 14, this.parent.parent.parent.x + 11, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14, this.set.getValue() ? ClickGUI.enabledBooleanColor.getValue() : ClickGUI.disabledBooleanColor.getValue());
        }
        FontUtil.drawString(this.set.name, this.parent.parent.parent.x + 13, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + 14 + 14 + this.offset + ((14 - FontUtil.getFontHeight()) / 2f), 0xa9b7c6);
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isWithinButton(mouseX, mouseY) && this.parent.open && this.parent.parent.open && this.parent.parent.parent.open) {
            if(button == 0) {
                this.set.setEnabled(!this.set.getValue());
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public int getParentHeight() {
        return 14;
    }

    @Override
    public void keyTyped(char typedChar, int key) {

    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    public boolean isWithinButton(int x, int y) {
        return x > this.parent.parent.parent.x && x < this.parent.parent.parent.x + ClickGui.width && y > this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 && y < this.parent.parent.parent.y + this.parent.offset + this.parent.parent.offset + this.offset + 14 + 14 + 14;
    }

    @Override
    public int getHeight() {
        return 14;
    }
}
