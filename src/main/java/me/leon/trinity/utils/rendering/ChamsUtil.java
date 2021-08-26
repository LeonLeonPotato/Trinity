package me.leon.trinity.utils.rendering;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import me.leon.trinity.hacks.render.Chams;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ChamsUtil extends RenderPlayer {
    public ChamsUtil() {
        super(Minecraft.instance.renderManager);
    }

    public void doRender(Chams.ExtraInfo info, double x, double y, double z, float partialTicks) {
        final AbstractClientPlayer entity = (AbstractClientPlayer) info.entity;

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel.swingProgress = 0;
        boolean shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();
        this.mainModel.isRiding = shouldSit;
        this.mainModel.isChild = entity.isChild();

        try {
            float f = this.interpolateRotation(info.lastYawOffset, info.yawOffset, partialTicks);
            float f1 = this.interpolateRotation(info.lastYaw, info.yaw, partialTicks);
            float f2 = f1 - f;

            this.renderLivingAt(entity, x, y, z);
            this.applyRotations(entity, info.ageInTicks, f, partialTicks);
            float f4 = this.prepareScale(entity, partialTicks);

            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, 0, 0, partialTicks);
            this.mainModel.setRotationAngles(0, 0, info.ageInTicks, f2, info.headPitch, f4, entity);
            boolean flag1 = this.setDoRenderBrightness((AbstractClientPlayer) entity, partialTicks);
            this.renderModel(entity, 0, 0, info.ageInTicks, f2, info.headPitch, f4);
            if (flag1) {
                this.unsetBrightness();
            }

            GlStateManager.depthMask(true);
            GlStateManager.disableRescaleNormal();
        } catch (Exception var20) {
            Trinity.LOGGER.info("Couldn't render entity! ");
            var20.printStackTrace();
        }

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean bindEntityTexture(@NotNull AbstractClientPlayer e) {
        ResourceLocation resourcelocation = this.getEntityTexture(e);
        if (resourcelocation == null) {
            this.bindTexture(DefaultPlayerSkin.getDefaultSkin(e.entityUniqueID));
            return false;
        } else {
            this.bindTexture(resourcelocation);
            return true;
        }
    }

    protected void renderModel(@NotNull AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean flag = this.isVisible(entitylivingbaseIn);
        boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().player);
        if (flag || flag1) {
            if (!this.bindEntityTexture(entitylivingbaseIn)) {
                return;
            }

            if (flag1) {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }

            TrinityEvent event = new TrinityEvent(EventStage.PRE);
            ((Chams) ModuleManager.getMod(Chams.class)).chams(event, mainModel, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, false, 0, true);
            if(!event.isCancelled()) this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

            if (flag1) {
                GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
        }
    }
}
