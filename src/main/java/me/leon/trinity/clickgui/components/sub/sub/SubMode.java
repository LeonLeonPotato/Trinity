package me.leon.trinity.clickgui.components.sub.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.sub.SubSetting;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;

public class SubMode extends Component {
    public me.leon.trinity.setting.settings.sub.SubMode set;
    public SubSetting parent;
    public int offset;
    public boolean open = false;

    public SubMode(me.leon.trinity.setting.settings.sub.SubMode set, SubSetting parent, int offset) {
        this.set = set;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.parent.parent.x + ClickGui.width, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 14, this.parent.parent.parent.x, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14, new Color(0x2b2b2b));
        FontUtil.drawString(this.set.name + ": " + this.set.getValue(), this.parent.parent.parent.x + 13, (int) (this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + ((14 - FontUtil.getFontHeight()) / 2f)), 0xa9b7c6);
        int opY = 0;
        if(this.open) {
            for(java.lang.String name : this.set.getValues()) {
                RenderUtils.drawRect(this.parent.parent.parent.x + ClickGui.width, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 42 + opY, this.parent.parent.parent.x, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 28 + opY + 14, new Color(0x2b2b2b));
                FontUtil.drawString(name, this.parent.parent.parent.x + 15, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 28 + opY + ((14 - FontUtil.getFontHeight()) / 2f), 0xa9b7c6);
                opY += 14;
            }
            RenderUtils.drawRainbowRectVertical(this.parent.parent.parent.x + 13, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 31, this.parent.parent.parent.x + 11, opY + 12, 3, 6, 200);
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        int place = this.set.getValues().indexOf(this.set.getValue());
        if(isMouseOnButtonMain(mouseX, mouseY) && this.parent.open) {
            if(button == 0) {
                this.set.setValue(this.set.getValues().get(add(this.set.getValues().size(), place)));
            }
            if(button == 1) {
                this.open = !open;
                this.parent.refresh();
                this.parent.parent.refresh();
                this.parent.parent.parent.refresh();
            }
        }

        if(this.open) {
            int places = 2;
            for(java.lang.String ignored : this.set.getValues()) {
                if(isMouseOnButtonOff(mouseX, mouseY, places)) {
                    if(button == 0) {
                        this.set.setValue(this.set.getValues().get(places - 2));
                    }
                }
                places++;
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

    public boolean isMouseOnButtonMain(int x, int y) {
        return x > this.parent.parent.parent.x && x < this.parent.parent.parent.x + ClickGui.width && y > this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 && y < this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 14;
    }

    public boolean isMouseOnButtonOff(int x, int y, int place) {
        return x > this.parent.parent.parent.x && x < this.parent.parent.parent.x + ClickGui.width && y > this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + (14 * place) && y < this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + (14 * place);
    }

    @Override
    public int getHeight() {
        if(this.open) {
            return 14 + (this.set.getValues().size() * 14);
        }
        return 14;
    }

    private int add(int size, int val) {
        if(val + 1 >= size) {
            return 0;
        }
        return val + 1;
    }
}
