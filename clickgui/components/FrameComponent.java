package me.leon.trinity.clickgui.components;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.config.rewrite.PresetManager;
import me.leon.trinity.config.rewrite.PresetObj;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.hud.editor.HudComponent;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.util.ArrayList;

public class FrameComponent extends Component {
	public int x, y;
	public Category c;
	public boolean open = false;
	public boolean drag = false;
	public int dragX, dragY;
	public int modY;

	public ArrayList<ButtonComponent> comps;
	public ArrayList<me.leon.trinity.clickgui.components.PresetButton> presetButtons;

	public FrameComponent(Category c, int x, int y) {
		this.x = x;
		this.y = y;
		this.c = c;
		this.modY = 15;
		this.comps = new ArrayList<>();
		this.presetButtons = new ArrayList<>();

		for (Module m : Trinity.moduleManager.getModulesByCategory(c)) {
			this.comps.add(new ButtonComponent(m, this, modY));
			this.modY += 14;
		}

		if (this.c == Category.PRESETS) {
			this.presetButtons.clear();
			for (PresetObj p : PresetManager.presets) {
				me.leon.trinity.clickgui.components.PresetButton presetButton = new me.leon.trinity.clickgui.components.PresetButton(p, this, modY);
				this.presetButtons.add(presetButton);
				this.modY += 14;
			}
		}
	}

	@Override
	public void render() {
		RenderUtils.drawRect(x + ClickGui.width, y + 15, x, y, ClickGUI.frameColor.getValue());
		if (ClickGUI.rainbow.getValue())
			RenderUtils.drawRainbowRectHorizontal(x + (ClickGui.width - 1), y + 15, (ClickGui.width - 1), y + 14, 1, 1, 255);
		FontUtil.drawString(c.name(), this.x + 5, this.y + ((15 - FontUtil.getFontHeight()) / 2), ClickGUI.nameColorFrame.getValue().getRGB());
		if (this.open) {
			for (ButtonComponent b : comps) {
				b.render();
			}
			for (me.leon.trinity.clickgui.components.PresetButton b : presetButtons) {
				b.render();
			}
		}
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		if (drag) {
			this.x = mouseX - dragX;
			this.y = mouseY - dragY;
		}
		for (ButtonComponent b : comps) {
			b.updateComponent(mouseX, mouseY);
		}
		for (me.leon.trinity.clickgui.components.PresetButton b : presetButtons) {
			b.updateComponent(mouseX, mouseY);
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		boolean alreadyDragging = false;
		for (FrameComponent c : ClickGui.frames) {
			if (c.drag) {
				alreadyDragging = true;
			}
		}

		if (!alreadyDragging) {
			this.drag = isMouseOnButton(mouseX, mouseY) && button == 0;
		}
		if (drag) {
			this.dragX = mouseX - x;
			this.dragY = mouseY - y;
		}

		if (button == 1 && isMouseOnButton(mouseX, mouseY)) {
			this.open = !open;
		}

		if (this.open) {
			for (ButtonComponent c : comps) {
				c.mouseClicked(mouseX, mouseY, button);
			}
			for (me.leon.trinity.clickgui.components.PresetButton c : presetButtons) {
				c.mouseClicked(mouseX, mouseY, button);
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		this.drag = false;

		if (this.open) {
			for (ButtonComponent b : comps) {
				b.mouseReleased(mouseX, mouseY, mouseButton);
			}
			for (me.leon.trinity.clickgui.components.PresetButton b : presetButtons) {
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
		if (this.open) {
			for (ButtonComponent b : comps) {
				b.keyTyped(typedChar, key);
			}
			for (me.leon.trinity.clickgui.components.PresetButton b : presetButtons) {
				b.keyTyped(typedChar, key);
			}
		}
	}

	@Override
	public void setOff(int newOff) {

	}

	public void refresh() {
		int off = 15;
		for (ButtonComponent c : comps) {
			c.setOff(off);
			off += c.getHeight();
		}

		if (this.c == Category.PRESETS) {
			update(off);
		}
	}

	private void update(int tY) {
		this.presetButtons.clear();
		for (PresetObj p : PresetManager.presets) {
			me.leon.trinity.clickgui.components.PresetButton presetButton = new me.leon.trinity.clickgui.components.PresetButton(p, this, tY);
			this.presetButtons.add(presetButton);
			tY += 14;
		}
	}


	@Override
	public int getHeight() {
		return 15;
	}

	@Override
	public int getXOffset() {
		return 0;
	}

	public boolean isMouseOnButton(int x, int y) {
		return x >= this.x && y >= this.y && y <= this.y + 15 && x <= this.x + ClickGui.width;
	}
}
