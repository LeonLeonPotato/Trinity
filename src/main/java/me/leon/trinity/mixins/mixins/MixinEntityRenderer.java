package me.leon.trinity.mixins.mixins;

import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.mixins.IMixin;
import me.leon.trinity.hacks.render.NoRender;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer implements IMixin {
    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCameraEffect(float ticks, CallbackInfo info) {
        if (ModuleManager.getMod(NoRender.class).isEnabled() && NoRender.hurtCam.getValue())
            info.cancel();
    }
}
