package me.leon.trinity.utils.rendering;

import me.leon.trinity.utils.math.MathUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;

public class Rainbow {
    private int r;
    private int g;
    private int b;
    private modes mode;

    public Rainbow() {
        this.r = 255;
        this.g = 0;
        this.b = 0;
    }

    public void updateColor(int speed) {
        if(r >= 255 && b <= 0) {
            this.mode = modes.GREEN_U;
        } else
        if(g >= 255 && r <= 0) {
            this.mode = modes.BLUE_U;
        } else
        if(b >= 255 && g <= 0) {
            this.mode = modes.RED_U;
        }


        if(mode == modes.RED_U) {
            this.r = (int) MathUtils.clamp(0, 255, (r + speed));
            this.g = (int) MathUtils.clamp(0, 255, g);
            this.b = (int) MathUtils.clamp(0, 255, (b - speed));
        }
        if(mode == modes.GREEN_U) {
            this.r = (int) MathUtils.clamp(0, 255, (r - speed));
            this.g = (int) MathUtils.clamp(0, 255, (g + speed));
            this.b = (int) MathUtils.clamp(0, 255, b);
        }
        if(mode == modes.BLUE_U) {
            this.r = (int) MathUtils.clamp(0, 255, r);
            this.g = (int) MathUtils.clamp(0, 255, (g - speed));
            this.b = (int) MathUtils.clamp(0, 255, (b + speed));
        }
    }

    public Color getColor() {
        return new Color(r, g, b);
    }

    public void setColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    private enum modes {
        RED_U,
        GREEN_U,
        BLUE_U,
    }
}
