package me.leon.trinity.clickgui.components.sub;

import me.leon.trinity.clickgui.components.Button;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.Color;

public class Boolean extends Component {
    public me.leon.trinity.setting.settings.Boolean set;
    public Button parent;
    public int offset;

    public Boolean(me.leon.trinity.setting.settings.Boolean set, Button parent, int offset) {
        this.set = set;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.parent.x + 100, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, new Color(0x2b2b2b));
        if(set.getValue()) {
            RenderUtils.drawRect(this.parent.parent.x + 100, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + this.offset + 14, new Color(0x787878));
        }
        FontUtil.drawString(this.set.name, this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - FontUtil.getFontHeight()) / 2f), 0xa9b7c6);
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isWithinButton(mouseX, mouseY) && this.parent.open) {
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
        return x > this.parent.parent.x && x < this.parent.parent.x + 100 && y > this.parent.parent.y + this.parent.offset + this.offset + 14 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14;
    }

    @Override
    public int getHeight() {
        return 14;
    }
}
