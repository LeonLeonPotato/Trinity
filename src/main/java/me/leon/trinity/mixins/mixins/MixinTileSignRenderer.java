package me.leon.trinity.mixins.mixins;

import me.leon.trinity.hacks.render.NoRender;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.tileentity.TileEntitySign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntitySignRenderer.class)
public class MixinTileSignRenderer implements IMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(TileEntitySign te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo info) {
		if (ModuleManager.getMod(NoRender.class).isEnabled() && NoRender.signs.getValue()) info.cancel();
	}
}
