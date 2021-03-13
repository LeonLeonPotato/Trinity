package me.leonleonpotato.ProtonClient.clickguiRewrite.components.sub;

import me.leonleonpotato.ProtonClient.clickguiRewrite.Component;
import me.leonleonpotato.ProtonClient.clickguiRewrite.components.Button;
import me.leonleonpotato.ProtonClient.hacks.Module;
import me.leonleonpotato.ProtonClient.util.render.RenderUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Keybind extends Component {
    public Module mod;
    public Button parent;
    public int offset;
    public boolean binding = false;
    private int keyTimer = 0;

    public Keybind(Module mod, Button parent, int offset) {
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void render() {
        this.keyTimer += 1;

        if(this.keyTimer >= 46) {
            this.keyTimer = 0;
        }
        RenderUtils.drawRect(this.parent.parent.x + 100, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, new Color(0x2b2b2b));
        RenderUtils.drawStringWithShadow(this.binding ? "Listening" + determineDots(keyTimer) : "Key: " + Keyboard.getKeyName(mod.getKey()), this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - fr.FONT_HEIGHT) / 2), 0xa9b7c6);
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && this.parent.open) {
            if(button == 0) {
                this.binding = !binding;
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
        if(this.binding) {
            if(key == KeyEvent.VK_DELETE || key == 211) {
                this.parent.mod.setKey(0);
            } else {
                this.parent.mod.setKey(key);
            }
            this.binding = false;
        }
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
        return x > this.parent.parent.x && x < this.parent.parent.x + 100 && y > this.parent.parent.y + this.parent.offset + 14 + this.offset && y < this.parent.parent.y + this.parent.offset + 14 + this.offset + 14;
    }

    private java.lang.String determineDots(int stage) {
        if(stage <= 15) {
            return ".";
        }
        if(stage <= 30 && stage > 15) {
            return "..";
        }
        if(stage <= 45 && stage > 30) {
            return "...";
        }
        return "";
    }
}
