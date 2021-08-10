package me.leon.trinity.gui;

import me.leon.trinity.gui.frame.FrameComponent;
import me.leon.trinity.gui.frame.IFrame;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.misc.BezierCurve;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.Rainbow;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.leon.trinity.utils.world.Timer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class ClickGui extends GuiScreen {
    private static ArrayList<IComponent> totalComponents; // not implemented yet
    private static ArrayList<IFrame> frames;
    private static IComponent hovered; // setting descriptions later?
    private static long endTime = 0, rawTime = 0;
    private static float todo = 0;
    private static final BezierCurve curve = new BezierCurve(0, 120, 120, 0);

    /*
    private Timer scrollTimer = new Timer();
    private float scrollSpeed = 0.0f;;

    private float scrollProgress = 0.0f;
    private float prevScrollProgress = 0.0f;
    private float renderScrollProgress = prevScrollProgress + (scrollProgress - prevScrollProgress) * mc.getRenderPartialTicks();

     */

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

        for(IFrame c : frames) {
            Point p = new Point(mouseX, mouseY);
            c.update(p);
            c.render(p);
        }

        if(hovered != null)
            FontUtil.drawString(hovered.description(), mouseX + 3, mouseY + 3, Color.WHITE);
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        for(IFrame c : frames) {
            if(c.buttonClick(mouseButton, new Point(mouseX, mouseY))) break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if(keyCode == Keyboard.KEY_E) frames.forEach(e -> e.setY(10));
        if (keyCode == 1) {
            for (IFrame c : frames) {
                c.unload();
            }
            mc.displayGuiScreen(null);
            ClickGUI.stopShader();
            return;
        }

        for (IFrame c : frames) {
            c.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (IFrame c : frames) {
            c.buttonRelease(state, new Point(mouseX, mouseY));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return ClickGUI.pause.getValue();
    }

    private void scroll() {
        if(ClickGUI.scroll.getValue()) {
            /*
            switch (ClickGUI.scrollAnimations.getValue()){
                case "Bezier": {
                    frames.forEach(e -> e.setLastY(e.getY()));
                    final int dWheel = Mouse.getDWheel();
                    todo += dWheel;
                    if(dWheel != 0) {
                        if(endTime == 0) {
                            endTime = System.currentTimeMillis() + 1500;
                            rawTime = 1500;
                        } else {
                            endTime += 1500;
                            rawTime += 1500;
                        }
                    }
                    if (endTime != 0) {
                        final float progress = (float) MathUtils.clamp(0, 1, ((float) (endTime - System.currentTimeMillis())) / ((float) rawTime));
                        if(progress != 0) {
                            frames.forEach(e -> e.setY((float) (e.getLastY() + (curve.get(progress) / 90f) * todo * 0.01)));
                        } else {
                            endTime = 0;
                            rawTime = 0;
                            todo = 0;
                        }
                    }
                    break;
                }
                case "Half": {
                    scrollProgress = (scrollProgress + scrollSpeed);
                    scrollSpeed *= 0.5f;
                    if (scrollTimer.hasPassAndReset(100)) {
                        if (scrollProgress < 0.0) {
                            scrollSpeed = scrollProgress * -0.25f;
                        } else if (scrollProgress > maxScrollProgress) {
                            scrollSpeed = (scrollProgress - maxScrollProgress) * -0.25f;
                        }
                    }
                    break;
                }

                case "None": {
                    break;
                }
                */
            final float x = (float) (Mouse.getDWheel() * ClickGUI.scrollSpeed.intValue() * 0.1);
            frames.forEach(e -> e.setY(e.getY() + x));
        }
    }

    public static ArrayList<IComponent> getTotalComponents() {
        return totalComponents;
    }

    public static ArrayList<IFrame> getFrames() {
        return frames;
    }
}
