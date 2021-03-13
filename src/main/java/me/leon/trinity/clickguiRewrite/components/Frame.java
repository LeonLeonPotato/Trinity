package me.leon.trinity.clickguiRewrite.components;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.leonleonpotato.ProtonClient.clickguiRewrite.Component;


import java.util.ArrayList;

public class Frame extends Component {
    public Category c;
    public int x, y;

    public boolean open = false;
    public boolean drag = false;
    public int dragX, dragY;
    public int modY;

    public ArrayList<Button> comps;
    public ArrayList<me.leonleonpotato.ProtonClient.clickguiRewrite.components.PresetButton> presetButtons;

    public Frame(Category c, int x, int y, int offset) {
        this.c = c;
        this.x = x;
        this.y = y;
        this.modY = 15;
        this.comps = new ArrayList<>();
        this.presetButtons = new ArrayList<>();

        for(Module m : Trinity.moduleManager.getModulesInCategory(c)) {
            this.comps.add(new Button(m, this, modY));
            this.modY += 14;
        }

        if(this.c == Category.PRESETS)
        {
            this.presetButtons.clear();
            for(Preset p : ProtonMod.instance.presetManager.presets) {
                me.leonleonpotato.ProtonClient.clickguiRewrite.components.PresetButton presetButton = new me.leonleonpotato.ProtonClient.clickguiRewrite.components.PresetButton(p, this, modY);
                this.presetButtons.add(presetButton);
                this.modY += 14;
            }
        }
    }

    @Override
    public void render() {
        RenderUtils.drawRect(x + 100, y + 14, x, y, new Color(0x3c3f41));
        RenderUtils.drawRainbowRectHorizontal(x + 98, y + 15, 98, y + 14, 2, 2, 255);
        RenderUtils.drawStringWithShadow(c.name(), this.x + 5, this.y + ((15 - fr.FONT_HEIGHT) / 2), 0xa9b7c6);
        if(this.open) {
            for(Button b : comps) {
                b.render();
            }
            for(me.leonleonpotato.ProtonClient.clickguiRewrite.components.PresetButton b : presetButtons) {
                b.render();
            }
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        if(drag) {
            this.x = mouseX - dragX;
            this.y = mouseY - dragY;
        }
        for(Button b : comps) {
            b.updateComponent(mouseX, mouseY);
        }
        for(me.leonleonpotato.ProtonClient.clickguiRewrite.components.PresetButton b : presetButtons) {
            b.updateComponent(mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean alreadyDragging = false;
        for(Frame c : ProtonMod.instance.clickGui2.frames) {
            if(c.drag) {
                alreadyDragging = true;
            }
        }

        if(!alreadyDragging) {
            this.drag = isMouseOnButton(mouseX, mouseY) && button == 0;
        }
        if(drag) {
            this.dragX = mouseX - x;
            this.dragY = mouseY - y;
        }

        if(button == 1 && isMouseOnButton(mouseX, mouseY)) {
            this.open = !open;
        }

        if(this.open) {
            for(Button c : comps) {
                c.mouseClicked(mouseX, mouseY, button);
            }
            for(me.leonleonpotato.ProtonClient.clickguiRewrite.components.PresetButton c : presetButtons) {
                c.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.drag = false;

        if(this.open) {
            for(Button b : comps) {
                b.mouseReleased(mouseX, mouseY, mouseButton);
            }
            for(me.leonleonpotato.ProtonClient.clickguiRewrite.components.PresetButton b : presetButtons) {
                b.mouseReleased(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public int getParentHeight() {
        return 0;
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(this.open) {
            for(Button b : comps) {
                b.keyTyped(typedChar, key);
            }
            for(me.leonleonpotato.ProtonClient.clickguiRewrite.components.PresetButton b : presetButtons) {
                b.keyTyped(typedChar, key);
            }
        }
    }

    @Override
    public void setOff(int newOff) {

    }

    public void refresh() {
        int off = 15;
        for(Component c : comps) {
            c.setOff(off);
            off += c.getHeight();
        }

        if(this.c == Category.PRESETS) {
            update(off);
        }
    }

    private void update(int tY) {
        this.presetButtons.clear();
        for(Preset p : ProtonMod.instance.presetManager.presets) {
            me.leonleonpotato.ProtonClient.clickguiRewrite.components.PresetButton presetButton = new me.leonleonpotato.ProtonClient.clickguiRewrite.components.PresetButton(p, this, tY);
            this.presetButtons.add(presetButton);
            tY += 14;
        }
    }


    @Override
    public int getHeight() {
        return 15;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x >= this.x && y >= this.y && y <= this.y + 15 && x <= this.x + 100;
    }
}
