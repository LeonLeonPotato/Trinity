package me.leon.trinity.mixins.mixins;

import me.leon.trinity.hacks.render.NoRender;
import me.leon.trinity.managers.ModuleManager;
import net.minecraft.client.gui.GuiBossOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiBossOverlay.class)
public class MixinGuiBossOverlay {

	@Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
	private void renderBossHealth(CallbackInfo info) {
		if (ModuleManager.getMod(NoRender.class).isEnabled() && NoRender.bosslist.getValue())
			info.cancel();
	}
}
