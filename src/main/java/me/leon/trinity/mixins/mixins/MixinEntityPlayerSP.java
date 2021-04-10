package me.leon.trinity.mixins.mixins;

import com.mojang.authlib.GameProfile;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.BlockPushEvent;
import me.leon.trinity.events.main.EventStopHandActive;
import me.leon.trinity.events.main.MoveEvent;
import me.leon.trinity.events.main.RotationEvent;
import me.leon.trinity.hacks.misc.FreeCam;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.world.Rotation.RotationUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer {
    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void OnUpdateWalkingPlayer(CallbackInfo info) {
        RotationEvent event = new RotationEvent(EventStage.PRE);
        Trinity.dispatcher.post(event);

        if (event.isCancelled()) {
            info.cancel();

            RotationUtils.updateRotationPackets(event);
        }
    }

    @Inject(method = "resetActiveHand", at = @At("HEAD"), cancellable = true)
    public void resetActiveHand(CallbackInfo info) {
        EventStopHandActive event = new EventStopHandActive(EventStage.PRE);
        Trinity.dispatcher.post(event);

        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(AbstractClientPlayer player, MoverType type, double x, double y, double z) {
        MoveEvent event = new MoveEvent(type, x, y, z, EventStage.PRE);
        Trinity.dispatcher.post(event);

        if (!event.isCancelled()) {
            super.move(type, x, y, z);
        } else {
            super.move(type, event.x, event.y, event.z);
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocks(double var1, double var2, double var3, CallbackInfoReturnable ci) {

        BlockPushEvent blockPushEvent = new BlockPushEvent(var1,var2,var3,EventStage.PRE);
        Trinity.dispatcher.post(blockPushEvent);

        if ((blockPushEvent).isCancelled())
            ci.cancel();
    }
}
