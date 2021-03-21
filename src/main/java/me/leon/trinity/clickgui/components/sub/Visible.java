package me.leon.trinity.clickgui.components.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.Button;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.Coloring;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;

public class Visible extends Component {
    public Module mod;
    public Button parent;
    public int offset;

    public Visible(Module mod, Button parent, int offset) {
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.parent.x + ClickGui.width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, new Color(0x2b2b2b));
        FontUtil.drawString("Visible: " + Coloring.getWHITE() + mod.visible, this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.nameColorSetting.getValue().getRGB());
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && this.parent.open) {
            if(button == 0) {
                mod.visible = !mod.visible;
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

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.parent.x && x < this.parent.parent.x + ClickGui.width && y > this.parent.parent.y + this.parent.offset + 14 + this.offset && y < this.parent.parent.y + this.parent.offset + 14 + this.offset + 14;
    }
}
