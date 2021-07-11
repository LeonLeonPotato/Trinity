package me.leon.trinity.clickgui;

import me.leon.trinity.clickgui.components.ButtonComponent;
import me.leon.trinity.clickgui.components.FrameComponent;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.utils.misc.FontUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

/**
 * taken right from proton
 *
 * @author leon
 *
 * rewritten
 * 6/29/2021
 */
public class ClickGui extends GuiScreen {
	public static int width = (int) ClickGUI.width.getValue();
	public static ArrayList<FrameComponent> frames;
	public int offset = 0;

	public ClickGui() {
		frames = new ArrayList<>();
		int frameX = 10;
		for (Category c : Category.values()) {
			frames.add(new FrameComponent(c, frameX, 10));
			frameX += (width + 3);
		}
		width = (int) ClickGUI.width.getValue();
	}

	public static FrameComponent getFrameFromCategory(String name) {
		for (FrameComponent frame : frames) {
			if (frame.c.name().equalsIgnoreCase(name)) {
				return frame;
			}
		}
		return null;
	}

	@Override
	public void initGui() {
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (ClickGUI.background.getValue().equalsIgnoreCase("Darken") || ClickGUI.background.getValue().equalsIgnoreCase("Both"))
			this.drawDefaultBackground();
		width = (int) ClickGUI.width.getValue();
		int DOffset = Mouse.getDWheel();
		for (FrameComponent c : frames) {
			c.render();
			c.updateComponent(mouseX, mouseY);
			if (ClickGUI.scroll.getValue())
				c.y = (int) ((DOffset * (ClickGUI.speed.getValue() / 100)) + c.y);
		}
		for (FrameComponent c : frames) {
			for (ButtonComponent b : c.comps) {
				if (b.hovered) {
					GL11.glPushMatrix();
					GL11.glScalef(0.7f, 0.7f, 0.7f);
					FontUtil.drawString(b.mod.getDescription(), (int) (mouseX * (1 / 0.7)), (int) (mouseY * (1 / 0.7)) - 7, 0xcb712f);
					GL11.glPopMatrix();
				}
			}
		}
	}

	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
		for (FrameComponent c : frames) {
			c.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (keyCode == 1) {
			for (FrameComponent c : frames) {
				c.drag = false;
			}
			mc.displayGuiScreen(null);
			ClickGUI.stopShader();
			return;
		}

		for (FrameComponent c : frames) {
			c.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		for (FrameComponent c : frames) {
			c.mouseReleased(mouseX, mouseY, state);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return ClickGUI.pause.getValue();
	}
}
