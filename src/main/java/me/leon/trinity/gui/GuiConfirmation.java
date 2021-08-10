package me.leon.trinity.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiConfirmation extends GuiScreen {
    private GuiButton b;

    public GuiConfirmation() {
        ScaledResolution c = new ScaledResolution(mc);
        b = new GuiButton(100, c.getScaledWidth() / 2, c.getScaledHeight() / 2, "Test");
    }

}
