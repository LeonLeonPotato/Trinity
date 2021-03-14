package me.leon.trinity.clickgui.components.sub.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.sub.SubSetting;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.Rainbow;
import me.leon.trinity.utils.rendering.RenderUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class SubColorPicker extends Component {
    public me.leon.trinity.setting.settings.sub.SubColor set;
    public SubSetting parent;
    public int offset;
    public boolean open = false;
    public boolean draggingColor = false;
    public boolean draggingHue = false;
    public boolean draggingAlpha = false;
    public float renderAtHue;
    public float renderAtAlpha;
    public boolean saveRainbow = false;
    /**
     * [0] = x, [1] = y
     */
    public float[] circlePos;
    Rainbow rainbow0 = new Rainbow();

    public SubColorPicker(me.leon.trinity.setting.settings.sub.SubColor set, SubSetting parent, int offset) {
        MinecraftForge.EVENT_BUS.register(this);
        this.set = set;
        this.parent = parent;
        this.offset = offset;
        this.renderAtHue = Color.RGBtoHSB(this.set.r, this.set.g, this.set.b, new float[] {0, 0, 0})[0] * 60;
        this.circlePos = new float[] {Color.RGBtoHSB(this.set.r, this.set.g, this.set.b, new float[] {0, 0, 0})[1] * 60, Color.RGBtoHSB(this.set.r, this.set.g, this.set.b, new float[] {0, 0, 0})[2] * 60};
        this.renderAtAlpha = (this.set.a / 255f) * 60;
    }

    @Override
    public void render() {
        RenderUtils.drawRect(this.parent.parent.parent.x + ClickGui.width, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14, this.parent.parent.parent.x, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14, new Color(0x2b2b2b));
        FontUtil.drawString(this.set.name, this.parent.parent.parent.x + 7, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + ((14 - FontUtil.getFontHeight()) / 2f), 0xa9b7c6);
        RenderUtils.drawRect(this.parent.parent.parent.x + (ClickGui.width - 5), this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 12, this.parent.parent.parent.x + (ClickGui.width - 15), this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 2, this.set.getValue());
        if(this.open) {
            RenderUtils.drawRect(this.parent.parent.parent.x + ClickGui.width, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + ClickGui.width, this.parent.parent.parent.x, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14, new Color(0x2b2b2b));

            RenderUtils.drawAlphaRect(this.parent.parent.parent.x + 8, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 70, 67, 10, this.set.getValue());
            RenderUtils.drawRect(this.parent.parent.parent.x + 8 + renderAtAlpha + 1, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 81, this.parent.parent.parent.x + 8 + renderAtAlpha - 1, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 69, new Color(0xa9b7c6));

            RenderUtils.drawColorPickerSquare(this.parent.parent.parent.x + 15, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 3, 60, 60, (int) (this.renderAtHue * 6f), this.set.a);
            RenderUtils.drawCircle(this.parent.parent.parent.x + 15 + circlePos[0], this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 3 + circlePos[1], 2f, 0.2f, new Color(255, 255, 255, 255));

            RenderUtils.drawHueRect(this.parent.parent.parent.x + 80, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 3, 10, 60);
            RenderUtils.drawRect(this.parent.parent.parent.x + 92, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 3 + renderAtHue + 1, this.parent.parent.parent.x + 78, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 3 + renderAtHue - 1, new Color(0xa9b7c6));

            RenderUtils.drawRect(this.parent.parent.parent.x + 25, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 95, this.parent.parent.parent.x + 15, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 85, new Color(0xa9b7c6));
            FontUtil.drawString("Rainbow", this.parent.parent.parent.x + 30, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 85 + ((10 - FontUtil.getFontHeight()) / 2f), 0xa9b7c6);
            if(this.set.rainbow) {
                RenderUtils.drawRect(this.parent.parent.parent.x + 23, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 93, this.parent.parent.parent.x + 17, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 87, new Color(50, 243, 50));
            }

            RenderUtils.drawRainbowRectVertical(this.parent.parent.parent.x + 10, this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 28, this.parent.parent.parent.x + 8, 98, 3, 6, 200);
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        if(draggingHue) {
            this.renderAtHue = Math.min(60, Math.max(0, mouseY - (this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 3)));
        }
        if(draggingColor) {
            double y = Math.min(60, Math.max(0, mouseY - (this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 3)));
            double x = Math.min(60, Math.max(0, mouseX - (this.parent.parent.parent.x + 18)));

            this.circlePos[0] = (float) x;
            this.circlePos[1] = (float) y;
        }
        if(draggingAlpha) {
            this.renderAtAlpha = Math.min(67, Math.max(7, mouseX - (this.parent.parent.parent.x + 13)));
        }

        if(draggingAlpha || draggingColor || draggingHue || saveRainbow) {
            final Color color = new Color(Color.HSBtoRGB((renderAtHue * 6) / 360, (circlePos[0] * (10 / 6f)) / ClickGui.width, (circlePos[1] * (10 / 6f)) / ClickGui.width));

            this.set.r = (color.getRed());
            this.set.g = (color.getGreen());
            this.set.b = (color.getBlue());
            this.set.a = ((int) ((renderAtAlpha / 67) * 255));
            this.saveRainbow = false;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButtonMain(mouseX, mouseY) && this.parent.open) {
            if(button == 1) {
                this.open = !open;
                this.parent.parent.refresh();
                this.parent.parent.parent.refresh();
            }
        }
        if(isMouseOnButtonColor(mouseX, mouseY) && this.parent.open && this.open) {
            if(button == 0) {
                this.draggingColor = true;
            }
        }
        if(isMouseOnButtonHue(mouseX, mouseY) && this.parent.open && this.open) {
            if(button == 0) {
                this.draggingHue = true;
            }
        }
        if(isMouseOnButtonAlpha(mouseX, mouseY) && this.parent.open && this.open) {
            if(button == 0) {
                this.draggingAlpha = true;
            }
        }
        if(isMouseOnButtonRainbow(mouseX, mouseY) && this.parent.open && this.open) {
            if(button == 0) {
                this.set.rainbow = !this.set.rainbow;
                if(!this.set.rainbow) {
                    this.saveRainbow = true;
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.draggingAlpha = this.draggingHue = this.draggingColor = false;
    }

    @Override
    public int getParentHeight() {
        return 14;
    }

    @Override
    public void keyTyped(char typedChar, int key) {

    }

    public boolean isMouseOnButtonMain(int x, int y) {
        return x > this.parent.parent.parent.x && x < this.parent.parent.parent.x + ClickGui.width && y > this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 && y < this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14;
    }

    public boolean isMouseOnButtonHue(int x, int y) {
        return x > this.parent.parent.parent.x + 80 && x < this.parent.parent.parent.x + 90 && y > this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 3 && y < this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 3 + 60;
    }

    public boolean isMouseOnButtonColor(int x, int y) {
        return x > this.parent.parent.parent.x + 15 && x < this.parent.parent.parent.x + 75 && y > this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 3 && y < this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 3 + 60;
    }

    public boolean isMouseOnButtonAlpha(int x, int y) {
        return x > this.parent.parent.parent.x + 14 && x < this.parent.parent.parent.x + 75 && y > this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 70 && y < this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 80;
    }

    public boolean isMouseOnButtonRainbow(int x, int y) {
        return x > this.parent.parent.parent.x + 15 && x < this.parent.parent.parent.x + 25 && y > this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 85 && y < this.parent.parent.parent.y + this.parent.parent.offset + this.parent.offset + this.offset + 14 + 14 + 95;
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public int getHeight() {
        if(this.open) {
            return 14 + ClickGui.width;
        }
        return 14;
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if(this.set.rainbow) {
            rainbow0.updateColor(3);
            this.set.r = (rainbow0.getColor().getRed());
            this.set.g = (rainbow0.getColor().getBlue());
            this.set.b = (rainbow0.getColor().getGreen());
        }
    }
}
