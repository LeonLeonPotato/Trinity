package me.leon.trinity.mixins.mixins;

import me.leon.trinity.hacks.misc.FreeCam;
import me.leon.trinity.hacks.player.InventoryMove;
import me.leon.trinity.hacks.render.FreeLook;
import me.leon.trinity.managers.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MovementInputFromOptions.class, priority = 10000)
public class MixinMovementInputFromOptions extends MovementInput {
	@Redirect(method = "updatePlayerMoveState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"))
	public boolean isKeyPressed(KeyBinding keyBinding) {
		if (!((FreeCam) ModuleManager.getMod(FreeCam.class)).check() && !ModuleManager.getMod(FreeLook.class).isEnabled()) {
			if (ModuleManager.getMod(InventoryMove.class).isEnabled()
					&& Minecraft.getMinecraft().currentScreen != null
					&& !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)
					&& Minecraft.getMinecraft().player != null) {
				return Keyboard.isKeyDown(keyBinding.getKeyCode());
			}
		} else {
			if (((FreeCam) ModuleManager.getMod(FreeCam.class)).check()) {
				return false;
			}
		}

		return keyBinding.isKeyDown();
	}
}
