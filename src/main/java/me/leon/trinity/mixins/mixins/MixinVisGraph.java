package me.leon.trinity.mixins.mixins;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.EventSetOpaqueCube;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VisGraph.class)
public class MixinVisGraph implements IMixin {

	@Inject(method = "setOpaqueCube", at = @At("HEAD"), cancellable = true)
	public void setOpaqueCube(BlockPos pos, CallbackInfo info) {
		final EventSetOpaqueCube event = new EventSetOpaqueCube(EventStage.PRE);
		Trinity.dispatcher.post(event);

		if (event.isCancelled()) {
			info.cancel();
		}
	}
}
