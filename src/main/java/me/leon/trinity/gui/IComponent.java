package me.leon.trinity.gui;

import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.utils.rendering.RenderUtils;
import net.minecraft.client.Minecraft;

import java.awt.*;

public interface IComponent {
    Minecraft mc = Minecraft.getMinecraft();

    void render(Point point);
    void update(Point point);
    void unload();

    boolean buttonClick(int button, Point point);
    boolean buttonRelease(int button, Point point);
    boolean keyTyped(char chr, int code);

    float height();
    float xOffset();

    IComponent parent();
    String description();

    default float getWidth() {
        return ClickGUI.width.floatValue();
    }
    default void drawRect(int x, int y, int x1, int y1, Color color) {
        RenderUtils.drawRect(Math.min(x, x1), Math.min(y, y1), Math.max(x, x1), Math.max(y, y1), color);
    }
    default void drawRect(float x, float y, float x1, float y1, Color color) {
        RenderUtils.drawRect(Math.min(x, x1), Math.min(y, y1), Math.max(x, x1), Math.max(y, y1), color);
    }
}
