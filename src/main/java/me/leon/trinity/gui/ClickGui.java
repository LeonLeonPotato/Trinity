package me.leon.trinity.gui;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.BezierCurve;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.world.Timer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGui extends GuiScreen {
    private static ArrayList<IComponent> totalComponents;
    private static ArrayList<FrameComponent> frames;
    private static IComponent hovered; // setting descriptions later?
    private static long startTime = System.currentTimeMillis();
    private static float todo = 0;
    private static final BezierCurve curve = new BezierCurve(0, 120, 120, 0);

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
        if(ClickGUI.scroll.getValue()) {
            switch (ClickGUI.scrollAnimations.getValue()){
                case "Bezier": {
                    todo += Mouse.getDWheel();

                    if (todo != 0) {
                        float plus = Math.abs(todo) * 100f;

                        final double t = (System.currentTimeMillis() - startTime) / plus;

                        final float cur_c = (float) ((curve.get(t)) / 120f);
                        final float cur = cur_c * todo;

                        frames.forEach(e -> e.setY(e.getY() + cur));
                        todo -= cur;

                        if (t >= 1) {
                            startTime = System.currentTimeMillis();
                        }
                    } else {
                        startTime = System.currentTimeMillis();
                    }
                }
                case "Half": {
                    todo += Mouse.getDWheel();
                    final float cur = todo * 0.6f;
                    if(Math.abs(todo) <= 0.5) {
                        todo = 0;
                    }
                    frames.forEach(e -> e.setY(e.getY() + cur));
                }
                case "None": {
                    frames.forEach(e -> e.setY(e.getY() + Mouse.getDWheel()));
                }
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
