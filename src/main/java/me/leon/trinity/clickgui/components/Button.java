package me.leon.trinity.clickgui.components;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.sub.ColorPicker;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.*;
import me.leon.trinity.clickgui.components.sub.String;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.util.ArrayList;

public class Button extends Component {
    public ArrayList<Component> subs;
    public Module mod;
    public int offset;
    public boolean open = false;
    public boolean hovered = false;
    public me.leon.trinity.clickgui.components.Frame parent;
    public int opY;

    public Button(Module mod, me.leon.trinity.clickgui.components.Frame parent, int offset) {
        this.parent = parent;
        this.mod = mod;
        this.offset = offset;
        this.subs = new ArrayList<>();
        this.opY = 0;

        for(Setting c : Trinity.settingManager.getSettingsByMod(mod.getName())) {
            if(c instanceof me.leon.trinity.setting.settings.Slider) {
                this.subs.add(new me.leon.trinity.clickgui.components.sub.Slider((Slider) c, this, this.opY));
                this.opY += 14;
            }
            if(c instanceof me.leon.trinity.setting.settings.Boolean) {
                this.subs.add(new me.leon.trinity.clickgui.components.sub.Boolean((me.leon.trinity.setting.settings.Boolean) c, this, this.opY));
                this.opY += 14;
            }
            if(c instanceof me.leon.trinity.setting.settings.Mode) {
                this.subs.add(new me.leon.trinity.clickgui.components.sub.Mode((Mode) c, this, this.opY));
                this.opY += 14;
            }
            if(c instanceof StringInput) {
                this.subs.add(new String((StringInput) c, this, this.opY));
                this.opY += 14;
            }
            if(c instanceof Color) {
                this.subs.add(new ColorPicker((Color) c, this, this.opY));
                this.opY += 14;
            }
            if(c instanceof SettingParent) {
                this.subs.add(new me.leon.trinity.clickgui.components.sub.SubSetting((SettingParent) c, this, this.opY));
                this.opY += 14;
            }
        }
        this.subs.add(new me.leon.trinity.clickgui.components.sub.Visible(mod, this, opY));
        this.opY += 14;
        this.subs.add(new me.leon.trinity.clickgui.components.sub.Keybind(mod, this, opY));
        this.opY += 14;
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.x + ClickGui.width, this.parent.y + offset + 14, this.parent.x, this.parent.y + offset, getColor(hovered, this.mod.isEnabled())); // hahaha 2b2b2b
        FontUtil.drawString(mod.getName(), this.parent.x + 5, this.parent.y + offset + ((14 - FontUtil.getFontHeight()) / 2) + 1, ClickGUI.nameColorButton.getValue().getRGB());
        FontUtil.drawString(this.open ? "-" : "...", (this.parent.x + ClickGui.width) - (FontUtil.getStringWidth(this.open ? "-" : "...") + 3), this.open ? (this.parent.y + offset + 2) : (this.parent.y + offset + ((14 - FontUtil.getFontHeight()) / 2) - 1), ClickGUI.nameColorButton.getValue().getRGB());
        if(this.open) {
            for(Component sub : subs) {
                sub.render();
            }
            if(ClickGUI.barMode.getValue().equals("Rainbow"))
                RenderUtils.drawRainbowRectVertical(this.parent.x + 5, this.parent.y + 16 + offset, this.parent.x + 3, this.getHeight() - 16, 2, 6, 200);
            else if(ClickGUI.barMode.getValue().equals("Static")) {
                RenderUtils.drawRect(this.parent.x + 5, this.parent.y + 14 + offset, this.parent.x + 3, this.getHeight() - 14, ClickGUI.barColor.getValue());
            }
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY) && this.parent.open;

        for(Component c : subs) {
            c.updateComponent(mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && this.parent.open) {
            if(button == 0) {
                this.mod.setEnabled(!this.mod.isEnabled());
            }

            if(button == 1) {
                this.open = !open;
                refresh();
                this.parent.refresh();
            }
        }
        if(this.parent.open) {
            for(Component c : subs) {
                c.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for(Component c : subs) {
            c.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public int getParentHeight() {
        return 15;
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        for(Component c : subs) {
            c.keyTyped(typedChar, key);
        }
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.x && x < this.parent.x + ClickGui.width && y > this.parent.y + offset && y < this.parent.y + offset + 14;
    }

    @Override
    public int getHeight() {
        if(this.open) {
            int return0 = 0;
            for(Component c : this.subs) {
                return0 += c.getHeight();
            }
            return 14 + return0;
        }
        return 14;
    }

    public void refresh() {
        if(this.open) {
            int a = 0;
            for(Component c : this.subs) {
                c.setOff(a);
                a += c.getHeight();
            }
        }
    }

    private java.awt.Color getColor(boolean hovered, boolean toggled) {
        if(hovered) {
            if(toggled) {
                return ClickGUI.enabledColor.getValue().brighter();
            } else {
                return ClickGUI.disabledColor.getValue().brighter();
            }
        } else {
            if(toggled) {
                return ClickGUI.enabledColor.getValue();
            } else {
                return ClickGUI.disabledColor.getValue();
            }
        }
    }
}
