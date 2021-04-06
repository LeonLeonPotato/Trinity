package me.leon.trinity.clickgui.components;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.config.rewrite.LoadConfig;
import me.leon.trinity.config.rewrite.PresetObj;
import me.leon.trinity.config.rewrite.SaveConfig;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventLoadPreset;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.misc.FontUtil;
import net.minecraft.client.gui.Gui;

public class PresetButton extends Component {
    public PresetObj preset;
    public Frame parent;
    public int offset;
    private boolean isHovered = false;
    private final int height;

    int x, y;

    public PresetButton(PresetObj preset, Frame parent, int offset) {
        this.preset = preset;
        this.parent = parent;
        this.offset = offset;
        this.height = 14;
    }

    @Override
    public void render() {
        Gui.drawRect(parent.x, this.parent.y + this.offset, parent.x + ClickGui.width, this.parent.y + 14 + this.offset, getColor(this.isHovered, Trinity.currentPreset.equals(this.preset)).getRGB());
        FontUtil.drawString(preset.name, this.parent.x + 5, this.parent.y + offset + ((14 - FontUtil.getFontHeight()) / 2f) + 1, ClickGUI.nameColorButton.getValue().getRGB());
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

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            Trinity.settingsDispatcher.post(new EventLoadPreset(EventStage.PRE, this.preset));

            SaveConfig.runStatic();

            Trinity.currentPreset = this.preset;

            LoadConfig.load();

            Trinity.settingsDispatcher.post(new EventLoadPreset(EventStage.POST, this.preset));
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
        return x > parent.x && x < parent.x + ClickGui.width && y > this.parent.y + this.offset && y < this.parent.y + 14 + this.offset;
    }
}
