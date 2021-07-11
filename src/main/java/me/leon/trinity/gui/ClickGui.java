package me.leon.trinity.gui;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.misc.BezierCurve;
import me.leon.trinity.utils.misc.FontUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGui extends GuiScreen {
    private static ArrayList<IComponent> totalComponents;
    private static ArrayList<FrameComponent> frames;
    private static IComponent hovered; // setting descriptions later?
    private static long startTime = 0;
    private static int todo;
    private static float current;
    private static final BezierCurve curve = new BezierCurve(0, 200, 200, 200);

    public ClickGui() {
        totalComponents = new ArrayList<>();
        frames = new ArrayList<>();
        int x = 10;
        for (Category value : Category.values()) {
            frames.add(new FrameComponent(value, x, 10));
            x += ClickGUI.width.intValue() + 3;
        }
    }

    @Override
    public void initGui() {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGUI.background.is("Darken", "Both")) this.drawDefaultBackground();

        hovered = null; // reset hovered

        scroll();

        for(FrameComponent c : frames) {
            Point p = new Point(mouseX, mouseY);
            c.update(p);
            c.render(p);
        }

        if(hovered != null)
            FontUtil.drawString(hovered.description(), mouseX + 3, mouseY + 3, Color.WHITE);
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        for(FrameComponent c : frames) {
            if(c.buttonClick(mouseButton, new Point(mouseX, mouseY))) break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            for (FrameComponent c : frames) {
                c.setDragging(false);
                c.unload();
            }
            mc.displayGuiScreen(null);
            ClickGUI.stopShader();
            return;
        }

        for (FrameComponent c : frames) {
            c.keyTyped(keyCode);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (FrameComponent c : frames) {
            c.buttonRelease(state, new Point(mouseX, mouseY));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return ClickGUI.pause.getValue();
    }

    private void scroll() {
        Trinity.LOGGER.info(curve.get((System.currentTimeMillis() % 500) / 500f) / 200f);
        if(1==1) return;

        if(ClickGUI.scroll.getValue()) {
            todo += Mouse.getDWheel();
            current = (float) (curve.get((System.currentTimeMillis() - startTime) / 500f) / 200f);
            final float left = todo - current;

            if(todo != 0) {
                if(todo > 0) {
                    frames.forEach(e -> e.setY(e.getY() + current));
                } else if (todo < 0) {
                    frames.forEach(e -> e.setY(e.getY() - current));
                }

                if(curve.isFinished()) {
                    todo = 0;
                    startTime = System.currentTimeMillis();
                    curve.setFinished(false);
                }

            } else {
                startTime = System.currentTimeMillis();
            }
        }
    }

    public static ArrayList<IComponent> getTotalComponents() {
        return totalComponents;
    }

    public static ArrayList<FrameComponent> getFrames() {
        return frames;
    }
}
