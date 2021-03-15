package me.leon.trinity.clickgui.components.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.Button;
import me.leon.trinity.hacks.client.Font;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.sub.SubBoolean;
import me.leon.trinity.setting.settings.sub.SubColor;
import me.leon.trinity.setting.settings.sub.SubMode;
import me.leon.trinity.setting.settings.sub.SubSlider;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class SubSetting extends Component {
    public boolean open;
    public Button parent;
    public SettingParent set;
    public int offset;
    public ArrayList<Component> comps;
    public int setY;

    public SubSetting(SettingParent set, Button parent, int offset) {
        this.set = set;
        this.parent = parent;
        this.offset = offset;
        this.comps = new ArrayList<>();
        this.setY = 14;
        for(Setting set0 : Trinity.settingManager.getSubSettingsBySetting(this.set)) {
            if(set0 instanceof SubSlider) {
                this.comps.add(new me.leon.trinity.clickgui.components.sub.sub.SubSlider((SubSlider) set0, this, setY));
                this.setY += 14;
            }
            if(set0 instanceof SubMode) {
                this.comps.add(new me.leon.trinity.clickgui.components.sub.sub.SubMode((SubMode) set0, this, setY));
                this.setY += 14;
            }
            if(set0 instanceof SubBoolean) {
                this.comps.add(new me.leon.trinity.clickgui.components.sub.sub.SubBoolean((SubBoolean) set0, this, setY));
                this.setY += 14;
            }
            if(set0 instanceof SubColor) {
                this.comps.add(new me.leon.trinity.clickgui.components.sub.sub.SubColorPicker((SubColor) set0, this, setY));
                this.setY += 14;
            }
        }
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.parent.x + ClickGui.width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, new Color(0x2b2b2b));
        if(set.getValue()) {
            RenderUtils.drawRect(this.parent.parent.x + ClickGui.width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + this.offset + 14, new Color(0x787878));
        }
        FontUtil.drawString(this.set.name, this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - FontUtil.getFontHeight()) / 2f), 0xa9b7c6);
        FontUtil.drawString(Font.enabled() ? this.open ? "--" : "+" : this.open ? "-" : "+", this.parent.parent.x + (ClickGui.width - 10), this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - FontUtil.getFontHeight()) / 2f), 0xa9b7c6);
        if(open) {
            for(Component c : this.comps) {
                c.render();
            }
            RenderUtils.drawRainbowRectVertical(this.parent.parent.x + 10, this.parent.parent.y + this.parent.offset + this.offset + 31, this.parent.parent.x + 8, getHeight() - 2, 3, 6, 200);
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        if(open) {
            for(Component c : this.comps) {
                c.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isWithinButton(mouseX, mouseY) && button == 1) {
            this.open = !open;
            refresh();
            parent.refresh();
            parent.parent.refresh();
        }
        if(set.canEnable() && isWithinButton(mouseX, mouseY) && button == 0) {
            this.set.setEnabled(!set.getValue());
        }
        if(open) {
            for(Component c : this.comps) {
                c.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if(open) {
            for(Component c : this.comps) {
                c.mouseReleased(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public int getParentHeight() {
        return 14;
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(open) {
            for(Component c : this.comps) {
                c.keyTyped(typedChar, key);
            }
        }
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public int getHeight() {
        if(!open) return 14;
        int a = 14;
        for(Component c : this.comps) {
            a += c.getHeight();
        }
        return a;
    }

    public void refresh() {
        if(this.open) {
            int a = 0;
            for(Component c : this.comps) {
                c.setOff(a);
                a += c.getHeight();
            }
        }
    }

    public boolean isWithinButton(int x, int y) {
        return x > this.parent.parent.x && x < this.parent.parent.x + ClickGui.width && y > this.parent.parent.y + this.parent.offset + this.offset + 14 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14;
    }
}
