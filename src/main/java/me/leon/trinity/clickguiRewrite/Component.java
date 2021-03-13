package me.leonleonpotato.ProtonClient.clickguiRewrite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class Component {
    protected final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    protected final Minecraft mc = Minecraft.getMinecraft();

    public abstract void render();
    public abstract void updateComponent(int mouseX, int mouseY);
    public abstract void mouseClicked(int mouseX, int mouseY, int button);
    public abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);
    public abstract int getParentHeight();
    public abstract void keyTyped(char typedChar, int key);
    public abstract void setOff(int newOff);
    public abstract int getHeight();
}
