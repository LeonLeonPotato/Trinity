package me.leon.trinity.mixins.mixins;

import me.leon.trinity.hacks.misc.FreeCam;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity implements IMixin {
    /*
    @SuppressWarnings("all")
    @Inject(method = "setSneaking", at = @At("HEAD"), cancellable = true)
    public void setSneak(boolean sneak, CallbackInfo info) {
        if(Trinity.moduleManager.getMod(FreeCam.class).isEnabled() && ((Entity) (Object) this) == mc.player && FreeCam.mode.getValue().equalsIgnoreCase("Camera")) {
            info.cancel();
        }
    }

     */
}
