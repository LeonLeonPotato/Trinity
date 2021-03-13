package me.leonleonpotato.ProtonClient.clickguiRewrite.components.sub;

import me.leonleonpotato.ProtonClient.ProtonMod;
import me.leonleonpotato.ProtonClient.clickguiRewrite.Component;
import me.leonleonpotato.ProtonClient.clickguiRewrite.components.Button;
import me.leonleonpotato.ProtonClient.events.ProtonEvent;
import me.leonleonpotato.ProtonClient.events.settingEvents.EventBooleanEnabled;
import me.leonleonpotato.ProtonClient.settings.Setting;
import me.leonleonpotato.ProtonClient.util.render.RenderUtils;

import java.awt.*;

public class Boolean extends Component {
    public Setting set;
    public Button parent;
    public int offset;

    public Boolean(Setting set, Button parent, int offset) {
        this.set = set;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.parent.x + 100, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, new Color(0x2b2b2b));
        if(set.getValBoolean()) {
            RenderUtils.drawRect(this.parent.parent.x + 100, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + this.offset + 14, new Color(0x787878));
        }
        RenderUtils.drawStringWithShadow(this.set.getName(), this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - fr.FONT_HEIGHT) / 2), 0xa9b7c6);
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isWithinButton(mouseX, mouseY) && this.parent.open) {
            if(button == 0) {
                this.set.setValBoolean(!this.set.getValBoolean());
                ProtonMod.settingsDispatcher.post(new EventBooleanEnabled(ProtonEvent.Era.PRE, this.set, this.set.getValBoolean()));
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
