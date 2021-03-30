package me.leon.trinity.mixins.mixins;

import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.hacks.render.NoRender;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.client.renderer.tileentity.TileEntityEnderChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityEnderChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityEnderChestRenderer.class)
public class MixinTileEntityEnderChestRenderer extends TileEntitySpecialRenderer<TileEntityEnderChest> implements IMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(TileEntityEnderChest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo info) {
        if(!(x == y && y == z && x == 0.0)) {
            if(NoRender.EChests.getValue() && ModuleManager.getMod(NoRender.class).isEnabled()) {
                info.cancel();
            }
        }
    }
}
