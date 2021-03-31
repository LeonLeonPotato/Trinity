package me.leon.trinity.mixins.mixins;

import me.leon.trinity.hacks.render.NoRender;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld implements IMixin {
    @Inject(method = "getRainStrength", at = @At("HEAD"), cancellable = true)
    public void getRainStrength(float delta, CallbackInfoReturnable<Float> info)
    {
        if(ModuleManager.getMod(NoRender.class).isEnabled() && NoRender.weather.getValue()) {
            info.cancel();
            info.setReturnValue(0.0f);
        }
    }
}
