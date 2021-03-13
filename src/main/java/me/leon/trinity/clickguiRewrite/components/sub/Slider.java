package me.leon.trinity.clickguiRewrite.components.sub;

import me.leon.trinity.clickguiRewrite.components.Button;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.leonleonpotato.ProtonClient.clickguiRewrite.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Slider extends Component {
    public me.leon.trinity.setting.settings.Slider set;
    public Button parent;
    public int offset;
    public boolean hovered = false;
    public double renderWidth = 0;
    public boolean dragging = false;

    public Slider(me.leon.trinity.setting.settings.Slider set, Button parent, int offset) {
        this.set = set;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.parent.x + 100, this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x, this.parent.parent.y + this.parent.offset + this.offset + 14, new Color(0x2b2b2b));
        RenderUtils.drawRect((float) (this.parent.parent.x + renderWidth + 7), this.parent.parent.y + this.parent.offset + this.offset + 14 + 14, this.parent.parent.x + 7, this.parent.parent.y + this.parent.offset + this.offset + 14, new Color(0xA3959595, true));
        RenderUtils.drawStringWithShadow(this.set.getName() + ": " + this.set.getValDouble(), this.parent.parent.x + 7, this.parent.parent.y + this.offset + this.parent.offset + 14 + ((14 - fr.FONT_HEIGHT)) / 2, 0xa9b7c6);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && this.parent.open && this.parent.parent.open) {
            if(button == 0) {
                this.dragging = true;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
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

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);

        double diff = Math.min(93, Math.max(0, (mouseX - 7) - this.parent.parent.x));

        double min = set.getMin();
        double max = set.getMax();

        renderWidth = (int) ((93) * (set.getValDouble() - min) / (max - min));

        if (dragging) {
            if (diff == 0) {
                set.setValDouble(set.getMin());
            }
            else {
                double newValue = roundToPlace(((diff / 93) * (max - min) + min), 2);
                set.setValDouble(newValue);
            }
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.parent.x + 7 && x < this.parent.parent.x + 100 && y > this.parent.parent.y + this.parent.offset + this.offset + 14 && y < this.parent.parent.y + this.parent.offset + this.offset + 14 + 14;
    }

    private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
