package me.leonleonpotato.ProtonClient.clickguiRewrite.components;

import me.leonleonpotato.ProtonClient.ProtonMod;
import me.leonleonpotato.ProtonClient.clickguiRewrite.Component;
import me.leonleonpotato.ProtonClient.config.Preset;
import me.leonleonpotato.ProtonClient.config.loadConfig;
import me.leonleonpotato.ProtonClient.config.saveConfig;
import me.leonleonpotato.ProtonClient.util.render.RenderUtils;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class PresetButton extends Component {
    public Preset preset;
    public Frame parent;
    public int offset;
    private boolean isHovered;
    private final int height;

    int x, y;

    public PresetButton(Preset preset, Frame parent, int offset) {
        this.preset = preset;
        this.parent = parent;
        this.offset = offset;
        this.height = 12;
    }

    @Override
    public void render() {
        Gui.drawRect(parent.x, this.parent.y + this.offset, parent.x + 100, this.parent.y + 14 + this.offset, this.isHovered ? this.preset == ProtonMod.curPreset ? new Color(0xA3D5D5D5, true).brighter().getRGB() : new Color(0x2b2b2b).brighter().getRGB() : this.preset == ProtonMod.curPreset ?  new Color(0xA3D5D5D5, true).getRGB() : new Color(0x2b2b2b).getRGB());
        RenderUtils.drawStringWithShadow(this.preset.name, this.parent.x + 5, this.parent.y + offset + ((14 - fr.FONT_HEIGHT) / 2) + 1, 0xa9b7c6);
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = isMouseOnButton(mouseX, mouseY);
        this.x = mouseX;
        this.y = mouseY;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {

            saveConfig.saveModules();
            saveConfig.saveBinds();
            saveConfig.saveSettings();
            saveConfig.saveSearch();
            saveConfig.saveHud();
            saveConfig.saveFriends();
            saveConfig.saveGui();

            ProtonMod.curPreset = this.preset;

            loadConfig.LoadConfig.loadGUI();
            loadConfig.LoadConfig.loadFriends();
            loadConfig.LoadConfig.loadHud();
            loadConfig.LoadConfig.loadSearch();
            loadConfig.LoadConfig.loadBinds();
            loadConfig.LoadConfig.loadModules(true);
            loadConfig.LoadConfig.loadSettings();

        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public int getParentHeight() {
        return 15;
    }

    @Override
    public void keyTyped(char typedChar, int key) {

    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > parent.x && x < parent.x + 100 && y > this.parent.y + this.offset && y < this.parent.y + 14 + this.offset;
    }
}
