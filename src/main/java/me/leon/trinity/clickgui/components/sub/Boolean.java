package me.leon.trinity.clickgui.components.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.Button;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventBooleanToggle;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.awt.*;

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
        RenderUtils.drawRect(this.parent.parent.x + ClickGui.width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, ClickGUI.backgroundColor.getValue());
        if(set.getValue()) {
            RenderUtils.drawRect(this.parent.parent.x + ClickGui.width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x + 5, this.parent.parent.y + this.parent.offset + this.offset + 14, this.set.getValue() ? ClickGUI.enabledBooleanColor.getValue() : ClickGUI.disabledBooleanColor.getValue());
        }
        FontUtil.drawString(this.set.name, this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - FontUtil.getFontHeight()) / 2f), ClickGUI.nameColorSetting.getValue().getRGB());
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isWithinButton(mouseX, mouseY) && this.parent.open) {
            if(button == 0) {
                final EventBooleanToggle event = new EventBooleanToggle(EventStage.PRE, this.set, !this.set.getValue());
                Trinity.settingsDispatcher.post(event);

                if(!event.isCancelled())
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
        return x > this.parent.parent.x && x < this.parent.parent.x + ClickGui.width && y > this.parent.parent.y + this.parent.offset + this.offset + 14 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14;
    }

    @Override
    public int getHeight() {
        return 14;
    }
}
