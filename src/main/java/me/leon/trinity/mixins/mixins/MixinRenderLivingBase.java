package me.leon.trinity.mixins.mixins;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.EventRenderEntityModel;
import me.leon.trinity.hacks.render.NoRender;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.managers.SpoofingManager;
import me.leon.trinity.mixins.IMixin;
import me.leon.trinity.utils.world.Rotation.RotationUtils;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> implements IMixin {

	protected MixinRenderLivingBase(final RenderManager renderManager, final ModelBase modelBaseIn, final float shadowSizeIn) {
		super(renderManager);
	}

	@Redirect(method = {"renderModel"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
	private void renderModelHook(final ModelBase modelBase, final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
		final EventRenderEntityModel event = new EventRenderEntityModel(EventStage.PRE, modelBase, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		Trinity.dispatcher.post(event);

		if (!event.isCancelled()) {
			if(entityIn == mc.player && SpoofingManager.currentRotation != null) {
				modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, SpoofingManager.currentRotation.pitch, scale);
			} else {
				modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			}
		}
	}

	@Inject(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
	private void renderModelWrapper(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo info) {
		if (ModuleManager.getMod("NoRender").isEnabled() && NoRender.noCluster.getValue() && entitylivingbaseIn != mc.player && mc.player.getDistance(entitylivingbaseIn) < 1)
			GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
	}

	@Inject(method = {"doRender"}, at = {@At("HEAD")})
	public void doRenderPre(final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks, final CallbackInfo info) {
	}

	@Inject(method = {"doRender"}, at = {@At("RETURN")})
	public void doRenderPost(final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks, final CallbackInfo info) {
	}
}

