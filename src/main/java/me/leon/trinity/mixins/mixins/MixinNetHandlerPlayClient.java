package me.leon.trinity.mixins.mixins;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.EventSpawnObject;
import me.leon.trinity.main.Trinity;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketSpawnObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    @Inject(method = "handleSpawnObject", at = @At("RETURN"), cancellable = true)
    public void handleSpawnObject(SPacketSpawnObject packet, CallbackInfo info) {
        final EventSpawnObject event = new EventSpawnObject(EventStage.POST, packet);
        Trinity.dispatcher.post(event);
    }
}
