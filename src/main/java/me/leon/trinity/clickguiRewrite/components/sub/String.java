package me.leonleonpotato.ProtonClient.clickguiRewrite.components.sub;

import me.leonleonpotato.ProtonClient.ProtonMod;
import me.leonleonpotato.ProtonClient.clickguiRewrite.Component;
import me.leonleonpotato.ProtonClient.clickguiRewrite.components.Button;
import me.leonleonpotato.ProtonClient.clickguiRewrite.components.Frame;
import me.leonleonpotato.ProtonClient.events.settingEvents.EventInputEnter;
import me.leonleonpotato.ProtonClient.settings.Setting;
import me.leonleonpotato.ProtonClient.util.render.RenderUtils;
import me.yagel15637.blitz.modifiers.EventEra;

import java.awt.*;

public class String extends Component {
    public Setting set;
    public Button parent;
    public int offset;
    boolean typing = false;
    private int timer = 0;

    public String(Setting set, Button parent, int offset) {
        this.set = set;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.parent.x + 100, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, new Color(0x2b2b2b));
        RenderUtils.drawStringWithShadow(this.set.getVal(), this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - fr.FONT_HEIGHT) / 2), 0xa9b7c6);
        if(this.set.DrawName()) RenderUtils.drawStringWithShadow(this.set.getName(), this.parent.parent.x + (100 - fr.getStringWidth(this.set.getName())), this.parent.parent.y + this.parent.offset + 14 + this.offset + ((14 - fr.FONT_HEIGHT) / 2), 0xa9b7c6);
        if(this.typing) {
            this.timer++;
            if(timer > 20 && timer <= 40) {
                int width = fr.getStringWidth(this.set.getVal());
                RenderUtils.drawRect(this.parent.parent.x + 7 + width + 2, this.parent.parent.y + this.parent.offset + this.offset + 14 + 12, this.parent.parent.x + 7 + width, this.parent.parent.y + this.parent.offset + this.offset + 14 + 2, new Color(0xa9b7c6));
            } else if(timer > 40) {
                this.timer = 0;
            }
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && this.parent.open) {
            if(button == 0) {
                for(Frame f : ProtonMod.instance.clickGui2.frames) {
                    for(Button b : f.comps) {
                        for(Component c : b.subs) {
                            if(c instanceof String) {
                                ((String) c).typing = false;
                            }
                        }
                    }
                }
                this.typing = true;
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
        if(this.typing) {
            java.lang.String charString = java.lang.String.valueOf(typedChar);

            boolean isntTypable = Character.isISOControl(typedChar);

            if((int) typedChar == 13) {
                this.set.setEnter(true);
                this.typing = false;
                ProtonMod.settingsDispatcher.post(new EventInputEnter(EventEra.PRE, this.set));
            }

            if(isntTypable) {
                if((int) typedChar == 8) {
                    this.set.delVal();
                }
            } else {
                this.set.setVal(this.set.getVal().concat(charString));
            }
        }
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.parent.x && x < this.parent.parent.x + 100 && y > this.parent.parent.y + this.parent.offset + 14 + this.offset && y < this.parent.parent.y + this.parent.offset + 14 + this.offset + 14;
    }

    @Override
    public int getHeight() {
        return 14;
    }
}
