package me.leon.trinity.mixins.mixins;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.RotationEvent;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.world.Rotation.RotationUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {
    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void OnUpdateWalkingPlayer(CallbackInfo info) {
        RotationEvent event = new RotationEvent(EventStage.PRE);
        Trinity.dispatcher.post(event);

        if (event.isCancelled()) {
            info.cancel();

            RotationUtils.updateRotationPackets(event);
        }
    }
}
