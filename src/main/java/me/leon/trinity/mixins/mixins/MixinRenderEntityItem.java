package me.leon.trinity.mixins.mixins;

import me.leon.trinity.hacks.render.NoRender;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderEntityItem.class)
public class MixinRenderEntityItem implements IMixin {
    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if(ModuleManager.getMod(NoRender.class).isEnabled() && NoRender.items.getValue()) {
            info.cancel();
        }
    }
}
