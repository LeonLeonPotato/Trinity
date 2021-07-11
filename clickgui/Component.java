package me.leon.trinity.clickgui;

import net.minecraft.client.Minecraft;

public abstract class Component {
	protected final Minecraft mc = Minecraft.getMinecraft();

	public abstract void render();

	public abstract void updateComponent(int mouseX, int mouseY);

	public abstract void mouseClicked(int mouseX, int mouseY, int button);

	public abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);

	public abstract int getParentHeight();

	public abstract void keyTyped(char typedChar, int key);

	public abstract void setOff(int newOff);

	public abstract int getHeight();

	public abstract int getXOffset();

	public abstract int getYOffset();
}
