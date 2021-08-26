package me.leon.trinity.mixins.mixins;

import me.leon.trinity.hacks.render.Chams;
import me.leon.trinity.hacks.render.NoRender;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.managers.SpoofingManager;
import me.leon.trinity.mixins.IMixin;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase implements IMixin {
	private EntityLivingBase b; // LOL

	@Inject(method = "renderArmorLayer", at = @At("HEAD"), cancellable = true)
	public void renderArmorLayer(EntityLivingBase p_Entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo info) {
		if (ModuleManager.getMod(NoRender.class).isEnabled() && NoRender.armor.getValue()) {
			info.cancel();
		}
		b = p_Entity;
	}

	@ModifyVariable(method = "renderArmorLayer", name = "headPitch", at = @At("HEAD"))
	public float getHeadPitch(float headPitch) {
		if(SpoofingManager.currentRotation != null && b == mc.player) return SpoofingManager.currentRotation.pitch;
		return headPitch;
	}
}
