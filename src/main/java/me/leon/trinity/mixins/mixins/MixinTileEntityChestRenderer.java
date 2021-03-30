package me.leon.trinity.mixins.mixins;

import me.leon.trinity.hacks.render.NoRender;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityShulkerBoxRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityChestRenderer.class)
public class MixinTileEntityChestRenderer extends TileEntitySpecialRenderer<TileEntityChest> implements IMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(TileEntityChest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo info) {
        if(!(x == y && y == z && x == 0.0)) {
            if(NoRender.chests.getValue() && ModuleManager.getMod(NoRender.class).isEnabled()) {
                info.cancel();
            }
        }
    }
}
