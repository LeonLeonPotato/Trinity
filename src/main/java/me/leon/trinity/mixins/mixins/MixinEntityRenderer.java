package me.leon.trinity.mixins.mixins;

import com.google.common.base.Predicate;
import me.leon.trinity.hacks.exploits.EntityMine;
import me.leon.trinity.hacks.misc.FreeCam;
import me.leon.trinity.hacks.render.FreeLook;
import me.leon.trinity.hacks.render.NoRender;
import me.leon.trinity.hacks.render.Tracers;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer implements IMixin {
	@Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
	public void hurtCameraEffect(float ticks, CallbackInfo info) {
		if (ModuleManager.getMod(NoRender.class).isEnabled() && NoRender.hurtCam.getValue())
			info.cancel();
	}

	@Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
	public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
		if (((EntityMine) ModuleManager.getMod(EntityMine.class)).check()) {
			return new ArrayList<>();
		} else {
			return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
		}
	}

	@Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;"))
	public Entity getMouseOver(Minecraft minecraft) {
		if (((FreeCam) ModuleManager.getMod(FreeCam.class)).check() || ModuleManager.getMod(FreeLook.class).isEnabled()) {
			return Minecraft.getMinecraft().player;
		}
		return Minecraft.getMinecraft().renderViewEntity;
	}
}
