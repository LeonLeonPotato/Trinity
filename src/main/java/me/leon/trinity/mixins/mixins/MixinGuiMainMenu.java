package me.leon.trinity.mixins.mixins;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.misc.FontUtil;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = GuiMainMenu.class/*, priority = 1006*/)
public class MixinGuiMainMenu extends GuiScreen {

	@Inject(method = "drawScreen", at = @At("TAIL"), cancellable = true)
	public void drawText(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
		FontUtil.drawString(Trinity.CLIENTNAME + ChatFormatting.WHITE + " " + Trinity.VERSION, 2, 2, new Color(139, 48, 171).getRGB());
	}
}
