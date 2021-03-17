package me.leon.trinity.mixins.mixins;

import io.netty.channel.ChannelHandlerContext;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.EventPacketRecieve;
import me.leon.trinity.events.main.EventPacketSend;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager implements IMixin {
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        EventPacketSend event = new EventPacketSend(EventStage.PRE, packet);

        Trinity.dispatcher.post(event);

        if(event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void onChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        EventPacketRecieve event = new EventPacketRecieve(EventStage.PRE, packet);
        Trinity.dispatcher.post(event);

        if(event.isCancelled()) callbackInfo.cancel();
    }
}
