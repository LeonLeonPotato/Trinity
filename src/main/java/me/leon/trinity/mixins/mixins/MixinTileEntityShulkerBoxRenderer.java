package me.leon.trinity.mixins.mixins;

import me.leon.trinity.hacks.render.NoRender;
import me.leon.trinity.managers.ModuleManager;
import net.minecraft.client.renderer.tileentity.TileEntityShulkerBoxRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityShulkerBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityShulkerBoxRenderer.class)
public class MixinTileEntityShulkerBoxRenderer extends TileEntitySpecialRenderer<TileEntityShulkerBox> {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(TileEntityShulkerBox te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo info) {
        if(!(x == y && y == z && x == 0.0)) {
            if(NoRender.shulkers.getValue() && ModuleManager.getMod(NoRender.class).isEnabled()) {
                info.cancel();
            }
        }
    }
}
