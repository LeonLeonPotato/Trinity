package me.leon.trinity.clickguiRewrite.components;

import me.leon.trinity.clickguiRewrite.components.sub.ColorPicker;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.leonleonpotato.ProtonClient.clickguiRewrite.Component;
import me.leonleonpotato.ProtonClient.clickguiRewrite.components.sub.Boolean;
import me.leonleonpotato.ProtonClient.clickguiRewrite.components.sub.String;

import java.awt.*;
import java.util.ArrayList;

public class Button extends Component {
    public ArrayList<Component> subs;
    public Module mod;
    public int offset;
    public boolean open = false;
    public boolean hovered = false;
    public Frame parent;
    public int opY;

    public Button(Module mod, Frame parent, int offset) {
        this.parent = parent;
        this.mod = mod;
        this.offset = offset;
        this.subs = new ArrayList<>();
        this.opY = 0;

        for(Setting c : ProtonMod.instance.settingsManager.getSettingByModName(mod.getName())) {
            if(c instanceof me.leon.trinity.setting.settings.Slider) {
                this.subs.add(new me.leon.trinity.clickguiRewrite.components.sub.Slider((Slider) c, this, this.opY));
                this.opY += 14;
            }
            if(c instanceof me.leon.trinity.setting.settings.Boolean) {
                this.subs.add(new Boolean(c, this, this.opY));
                this.opY += 14;
            }
            if(c instanceof me.leon.trinity.setting.settings.Mode) {
                this.subs.add(new me.leonleonpotato.ProtonClient.clickguiRewrite.components.sub.Mode(c, this, this.opY));
                this.opY += 14;
            }
            if(c.isString()) {
                this.subs.add(new String(c, this, this.opY));
                this.opY += 14;
            }
            if(c.isColor()) {
                this.subs.add(new ColorPicker(c, this, this.opY));
                this.opY += 14;
            }
        }
        this.subs.add(new me.leonleonpotato.ProtonClient.clickguiRewrite.components.sub.Visible(mod, this, opY));
        this.opY += 14;
        this.subs.add(new me.leonleonpotato.ProtonClient.clickguiRewrite.components.sub.Keybind(mod, this, opY));
        this.opY += 14;
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.x + 100, this.parent.y + offset + 14, this.parent.x, this.parent.y + offset, this.hovered ? this.mod.isToggled() ? new Color(0xA3D5D5D5, true).brighter() : new Color(0x2b2b2b).brighter() : this.mod.isToggled() ?  new Color(0xA3D5D5D5, true) : new Color(0x2b2b2b)); // hahaha 2b2b2b
        RenderUtils.drawStringWithShadow(mod.getName(), this.parent.x + 5, this.parent.y + offset + ((14 - fr.FONT_HEIGHT) / 2) + 1, 0xa9b7c6);
        RenderUtils.drawStringWithShadow(this.open ? "-" : "...", (this.parent.x + 100) - (fr.getStringWidth(this.open ? "-" : "...") + 3), this.open ? (this.parent.y + offset + 2) : (this.parent.y + offset + ((14 - fr.FONT_HEIGHT) / 2) - 1), 0xa9b7c6);
        if(this.open) {
            for(Component sub : subs) {
                sub.render();
            }
            RenderUtils.drawRainbowRectVertical(this.parent.x + 5, this.parent.y + 16 + offset, this.parent.x + 3, this.getHeight() - 16, 2, 6, 200);
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
                this.mod.setToggled(!this.mod.isToggled());
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
        return x > this.parent.x && x < this.parent.x + 100 && y > this.parent.y + offset && y < this.parent.y + offset + 14;
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
}
