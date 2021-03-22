package me.leon.trinity.mixins.mixins;

import me.leon.trinity.main.Trinity;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer implements IMixin {
    @Shadow @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(method = "getLocationCape", at = @At(value = "RETURN"), cancellable = true)
    public void getCape(CallbackInfoReturnable<ResourceLocation> cir) {
        UUID uuid = getPlayerInfo().getGameProfile().getId();

        if (Trinity.capeManager.hasCape(uuid)) {
            cir.setReturnValue(Trinity.capeManager.getCapeForUUID(uuid));
        }
    }
}
