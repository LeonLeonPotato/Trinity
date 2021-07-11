package me.leon.trinity.mixins.mixins;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.EventClickBlock;
import me.leon.trinity.events.main.EventDamageBlock;
import me.leon.trinity.events.main.EventDestroyBlock;
import me.leon.trinity.main.Trinity;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {
	@Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
	public void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> info) {
		final EventDamageBlock event = new EventDamageBlock(EventStage.PRE, posBlock, directionFacing);
		Trinity.dispatcher.post(event);

		if (event.isCancelled()) {
			info.setReturnValue(false);
			info.cancel();
		}
	}

	@Inject(method = "onPlayerDestroyBlock", at = @At("HEAD"), cancellable = true)
	public void onPlayerDestroyBlock(BlockPos posBlock, CallbackInfoReturnable<Boolean> info) {
		final EventDestroyBlock event = new EventDestroyBlock(EventStage.PRE, posBlock);
		Trinity.dispatcher.post(event);

		if (event.isCancelled()) {
			info.setReturnValue(false);
			info.cancel();
		}
	}

	@Inject(method = "clickBlock", at = @At("HEAD"), cancellable = true)
	public void clickBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> info) {
		final EventClickBlock event = new EventClickBlock(EventStage.PRE, posBlock, directionFacing);
		Trinity.dispatcher.post(event);

		if (event.isCancelled()) {
			info.setReturnValue(false);
			info.cancel();
		}
	}
}
