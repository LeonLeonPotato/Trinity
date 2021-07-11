package me.leon.trinity.mixins.mixins;

import com.mojang.authlib.GameProfile;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.BlockPushEvent;
import me.leon.trinity.events.main.EventStopHandActive;
import me.leon.trinity.events.main.MoveEvent;
import me.leon.trinity.events.main.SpoofEvent;
import me.leon.trinity.hacks.misc.FreeCam;
import me.leon.trinity.hacks.render.FreeLook;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.managers.SpoofingManager;
import me.leon.trinity.mixins.IMixin;
import me.leon.trinity.utils.world.Rotation.RotationUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityPlayerSP.class, priority = 10000)
public class MixinEntityPlayerSP extends AbstractClientPlayer implements IMixin {
	public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
		super(worldIn, playerProfile);
	}

	@Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
	public void OnUpdateWalkingPlayer(CallbackInfo info) {
		SpoofEvent event = new SpoofEvent(EventStage.PRE);
		Trinity.dispatcher.post(event);

		if (event.isCancelled() && !SpoofingManager.cancel) {
			info.cancel();

			RotationUtils.updateRotationPackets(event);
		}
	}

	/**
	 * i took this from impact??????
	 *
	 * @param var1
	 */
	@Inject(
			method = {"isCurrentViewEntity"},
			at = {@At("HEAD")},
			cancellable = true
	)
	public void isCurrentViewEntity(CallbackInfoReturnable var1) {
		if (((FreeCam) ModuleManager.getMod(FreeCam.class)).check() || ModuleManager.getMod(FreeLook.class).isEnabled()) {
			var1.setReturnValue(true);
		}
	}

	@Inject(
			method = {"isUser"},
			at = {@At("HEAD")},
			cancellable = true
	)
	private void isUser(CallbackInfoReturnable var1) {
		if (((FreeCam) ModuleManager.getMod(FreeCam.class)).check() || ModuleManager.getMod(FreeLook.class).isEnabled()) {
			var1.setReturnValue(false);
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

		BlockPushEvent blockPushEvent = new BlockPushEvent(var1, var2, var3, EventStage.PRE);
		Trinity.dispatcher.post(blockPushEvent);

		if ((blockPushEvent).isCancelled())
			ci.cancel();
	}
}
