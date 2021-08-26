package me.leon.trinity.hacks.render;

import io.netty.util.internal.ConcurrentSet;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.TrinityEvent;
import me.leon.trinity.events.main.EventRenderEntityCrystal;
import me.leon.trinity.events.main.EventRenderEntityModel;
import me.leon.trinity.events.main.EventTotemPop;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.rendering.ChamsUtil;
import me.leon.trinity.utils.rendering.Tessellator;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Chams extends Module {
    public static ModeSetting mode = new ModeSetting("Mode", "Textured", "Normal", "Textured", "Wireframe");
    public static ModeSetting wallsMode = new ModeSetting("Walls", "XQZ", "XQZ", "Normal", "None");
    public static SliderSetting width = new SliderSetting("Width", 0.1, 1, 4, false);
    public static BooleanSetting cancel = new BooleanSetting("Cancel", true);
    public static ColorSetting color = new ColorSetting("Color", 119, 0, 255, 200, false);
    public static ColorSetting xqzColor = new ColorSetting("XQZ Color", 119, 0, 255, 200, false);

    public static BooleanSetting targeting = new BooleanSetting("Targets", true, false);
    public static BooleanSetting players = new BooleanSetting("Players", targeting, true);
    public static BooleanSetting crystals = new BooleanSetting("Crystals", targeting, true);

    public static BooleanSetting pop = new BooleanSetting("PopChams", false);

    public static BooleanSetting extra = new BooleanSetting("Extra", pop, true);
    public static ModeSetting extraRender = new ModeSetting("Rendering", extra, "Textured", "Textured", "Wireframe", "Normal");
    public static ModeSetting popExtraMode = new ModeSetting("Mode", extra, "Heaven", "Hell", "Heaven");
    public static SliderSetting height = new SliderSetting("Height", extra, 0, 10, 50, true);
    public static BooleanSetting popExtraFade = new BooleanSetting("Fade", extra, true);
    public static BooleanSetting popExtraFreeze = new BooleanSetting("Freeze", extra, true);
    public static BooleanSetting syncTime = new BooleanSetting("SyncTime", extra, false);
    public static SliderSetting popExtraTime = new SliderSetting("Time", extra, 0, 3000, 10000, true, s -> !syncTime.getValue());
    public static ColorSetting popExtraColor = new ColorSetting("Color", extra, 119, 0, 255, 200, false);

    public static SliderSetting popTime = new SliderSetting("Time", pop, 0, 3000, 10000, true);
    public static ModeSetting popMode = new ModeSetting("Mode", pop, "Textured", "Normal", "Textured", "Wireframe");
    public static ModeSetting popWallsMode = new ModeSetting("Walls", pop, "XQZ", "XQZ", "Normal", "None");
    public static SliderSetting popWidth = new SliderSetting("Width", pop, 0.1, 1, 4, false);
    public static ModeSetting popCancel = new ModeSetting("Cancel", pop, "Skin", "Skin", "All", "None");
    public static BooleanSetting popFade = new BooleanSetting("Fade", pop, true);
    public static ColorSetting popColor = new ColorSetting("Color", pop, 119, 0, 255, 200, false);
    public static ColorSetting popXqzColor = new ColorSetting("XQZ Color", pop, 119, 0, 255, 200, false);

    private static Set<ExtraInfo> extraMap;
    private static Set<PopInfo> popMap;
    private static Set<PlayerRenderInfo> infoMap;

    private boolean isExtra = false; // hack
    private ExtraInfo info;
    private ChamsUtil util;

    public Chams() {
        super("Chams", "Render entities through walls", Category.RENDER);
        extraMap = new ConcurrentSet<>();
        popMap = new ConcurrentSet<>();
        infoMap = new ConcurrentSet<>();
    }

    @Override
    public String getHudInfo() {
        return mode.getValue();
    }

    public static boolean shouldRender(Entity e) {
        if(ModuleManager.getMod("Chams").isEnabled()) {
            if(e instanceof EntityEnderCrystal) return crystals.getValue();
            if(e instanceof EntityPlayer) return players.getValue();
        }
        return false;
    }

    @EventHandler
    private final Listener<EventRenderEntityCrystal> renderEntityCrystalListener = new Listener<>(event -> {
        chams(event, event.modelBase, event.entityIn, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale, false, 0, false);
    });

    @EventHandler
    private final Listener<EventRenderEntityModel> renderEntityModelListener = new Listener<>(event -> {
        updatePop();
        final PopInfo info = getPop(event.entityIn);
        final float progress = isPop(event.entityIn) ? (1 - (((info.time + popTime.intValue()) - System.currentTimeMillis()) / popTime.floatValue())) : 0;

        if(popCancel.is("None", "Skin") && !isExtra && isPop(event.entityIn)) {
            chams(event, event.modelBase, event.entityIn, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale, false, 0, false);
            chams(event, event.modelBase, event.entityIn, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale, true, progress, false);
        } else
            chams(event, event.modelBase, event.entityIn, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale, isPop(event.entityIn), progress, isExtra);
    });

    @EventHandler
    private final Listener<EventTotemPop> totemListener = new Listener<>(event -> {
        final PlayerRenderInfo info = getInfo(event.getEntity());
        if(info != null) {
            if(extra.getValue())
                extraMap.add(new ExtraInfo(event.getEntity(), info.model, info.limbSwing, info.limbSwingAmount, info.ageInTicks, info.headPitch, info.yaw, info.lastYaw, info.yawOffset, info.lastYawOffset));
        }
        if(pop.getValue()) {
            if(isPop(event.getEntity())) popMap.remove(getPop(event.getEntity()));
            popMap.add(new PopInfo(event.getEntity()));
        }
    });

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if(util == null) util = new ChamsUtil();
        renderExtra();
    }

    public void chams(TrinityEvent event, final ModelBase model, final Entity entity, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale, boolean pop, float progress, boolean extra) {
        if(!shouldRender(entity)) return;
        boolean shadows = mc.gameSettings.entityShadows;
        mc.renderManager.setRenderShadow(false);

        if(entity instanceof EntityPlayer) {
            final PlayerRenderInfo info = getInfo(entity);
            final EntityPlayer en = (EntityPlayer) entity;

            if(info != null) infoMap.remove(info);
            infoMap.add(new PlayerRenderInfo(en, (ModelPlayer) model, limbSwing, limbSwingAmount, ageInTicks, headPitch, en.rotationYawHead, en.prevRotationYawHead, en.renderYawOffset, en.prevRenderYawOffset));
        }

        final String renderMode = extra ? extraRender.getValue() : pop ? popMode.getValue() : mode.getValue();
        final String hiddenMode = extra || pop ? popWallsMode.getValue() : wallsMode.getValue();
        final float renderWidth = extra || pop ? popWidth.floatValue() : width.floatValue();
        final String cancelModel =  extra || pop ? popCancel.getValue() : String.valueOf((cancel.getValue()));

        final float extraProgress = extra ? (1 - (((info.time + popExtraTime.intValue()) - System.currentTimeMillis()) / popExtraTime.floatValue())) : 0;

        GL11.glPushMatrix();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0156862745f);

        final boolean cancel = cancelModel.equalsIgnoreCase("All") || cancelModel.equalsIgnoreCase("true") || cancelModel.equalsIgnoreCase("Skin");

        switch (renderMode) {
            case "Wireframe": {
                GL11.glPushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(1032, 6913);
                GL11.glDisable(3553);
                GL11.glEnable(2848);
                GL11.glEnable(GL11.GL_BLEND);
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.glLineWidth(renderWidth);

                if (hiddenMode.equalsIgnoreCase("XQZ")) {
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(false);

                    GL11.glDepthRange(0.1, 1.0f);
                    GL11.glDepthFunc(GL11.GL_GREATER);
                    Tessellator.color(getColor(progress, extraProgress, pop, extra, true));
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glDepthFunc(GL11.GL_LESS);
                    GL11.glDepthRange(0.0f, 1.0f);

                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(false);

                    Tessellator.color(getColor(progress, extraProgress, pop, extra, false));
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                } else {
                    GL11.glEnable(GL11.GL_DEPTH_TEST);

                    if(hiddenMode.equalsIgnoreCase("Normal")) {
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                    }

                    GL11.glDepthMask(false);

                    Tessellator.color(getColor(progress, extraProgress, pop, extra, false));
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(true);

                    if(hiddenMode.equalsIgnoreCase("Normal")) {
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                    }
                }

                GL11.glPopAttrib();
                GL11.glPopMatrix();

                if(cancel) event.cancel();

                break;
            }
            case "Textured": {
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GL11.glLineWidth(1.5f);

                if (hiddenMode.equalsIgnoreCase("XQZ")) {
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(false);

                    // walls
                    GL11.glDepthRange(0.01, 1.0f);
                    GL11.glDepthFunc(GL11.GL_GREATER);
                    Tessellator.color(getColor(progress, extraProgress, pop, extra, true));
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glDepthFunc(GL11.GL_LESS);
                    GL11.glDepthRange(0.0, 1.0f);


                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(false);

                    // normal
                    Tessellator.color(getColor(progress, extraProgress, pop, extra, false));
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                } else {
                    if(hiddenMode.equalsIgnoreCase("Normal")) {
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glDepthMask(false);
                    }

                    Tessellator.color(getColor(progress, extraProgress, pop, extra, false));
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

                    if(hiddenMode.equalsIgnoreCase("Normal")) {
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        GL11.glDepthMask(true);
                    }
                }

                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopAttrib();
                GL11.glPopMatrix();

                if(cancel) event.cancel();
                break;
            }
            case "Normal": {
                GL11.glEnable(32823);
                GL11.glPolygonOffset(1.0f, -1100000.0f);

                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

                GL11.glPolygonOffset(1.0f, 1000000.0f);
                GL11.glDisable(32823);

                event.cancel();
                break;
            }
        }

        GL11.glPopMatrix();
        mc.renderManager.setRenderShadow(shadows);
    }

    private void renderExtra() {
        for(ExtraInfo extraInfo : extraMap) {
            if(extraInfo.time + popExtraTime.intValue() < System.currentTimeMillis()) {
                extraMap.remove(extraInfo);
                return;
            }

            final Entity entity = extraInfo.entity;

            final Render<Entity> renderer = mc.renderManager.getEntityRenderObject(entity);
            if (renderer == null) continue;

            final float partialTicks = mc.getRenderPartialTicks();
            final float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
            final Vec3d pos = new Vec3d(extraInfo.x, extraInfo.y + ((1 - ((extraInfo.time + popExtraTime.intValue()) - System.currentTimeMillis()) / popExtraTime.floatValue()) * (height.floatValue() * (popExtraMode.is("Heaven") ? 1 : -1))), extraInfo.z).subtract(mc.renderManager.renderPosX, mc.renderManager.renderPosY, mc.renderManager.renderPosZ);

            isExtra = true;
            info = extraInfo;
            if(popExtraFreeze.getValue()) {
                util.doRender(info, pos.x, pos.y, pos.z, mc.getRenderPartialTicks());
            } else renderer.doRender(entity, pos.x, pos.y, pos.z, yaw, mc.getRenderPartialTicks());
            info = null;
            isExtra = false;
        }
    }

    private void updatePop() {
        final long time = System.currentTimeMillis();
        popMap.removeIf(e -> (e.time + popTime.intValue()) < time);
    }

    private Color getColor(float progress, float progress0, boolean flag, boolean flag2, boolean hidden) {
        if(flag2) {
            if(hidden) {
                return popExtraFade.getValue() ? lower(popXqzColor.getValue(), progress0) : popXqzColor.getValue();
            } else {
                return popExtraFade.getValue() ? lower(popExtraColor.getValue(), progress0) : popExtraColor.getValue();
            }
        } else if(flag) {
            return popFade.getValue() ? lower(hidden ? popXqzColor.getValue() : popColor.getValue(), progress) : hidden ? popXqzColor.getValue() : popColor.getValue();
        } else {
            return hidden ? xqzColor.getValue() : color.getValue();
        }
    }

    private Color lower(Color color, float progress) {
        return new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                (int) (color.getAlpha() * (1 - progress))
        );
    }

    private boolean isPop(Entity entity) {
        return getPop(entity) != null;
    }

    private boolean isExtra(Entity entity) {
        for(ExtraInfo info : extraMap) {
            if(info.entity == entity) {
                return true;
            }
        }
        return false;
    }

    public PopInfo getPop(Entity entity) {
        for(PopInfo info : popMap) {
            if(info.entity == entity) {
                return info;
            }
        }
        return null;
    }

    public PlayerRenderInfo getInfo(Entity entity) {
        for(PlayerRenderInfo info : infoMap) {
            if(info.entity == entity) {
                return info;
            }
        }
        return null;
    }

    public static class ExtraInfo {
        public double x, y, z;
        public EntityLivingBase entity;
        public long time;

        public ModelPlayer model;
        public final float limbSwing;
        public final float limbSwingAmount;
        public final float ageInTicks;
        public final float headPitch;
        public final float yaw, lastYaw, yawOffset, lastYawOffset;

        public ExtraInfo(EntityLivingBase entity, ModelPlayer model, float limbSwing, float limbSwingAmount, float ageInTicks, float headPitch, float yaw, float lastYaw, float yawOffset, float lastYawOffset) {
            this.entity = entity;
            this.model = model;
            this.limbSwing = limbSwing;
            this.limbSwingAmount = limbSwingAmount;
            this.ageInTicks = ageInTicks;
            this.headPitch = headPitch;
            this.yaw = yaw;
            this.lastYaw = lastYaw;
            this.yawOffset = yawOffset;
            this.lastYawOffset = lastYawOffset;

            time = System.currentTimeMillis();
            x = entity.posX;
            y = entity.posY;
            z = entity.posZ;
        }
    }

    private static class PopInfo {
        public Entity entity;
        public long time;

        private PopInfo(Entity entity) {
            this.entity = entity;
            this.time = System.currentTimeMillis();
        }
    }

    private static class PlayerRenderInfo {
        public final EntityPlayer entity;
        public final ModelPlayer model;
        public final float limbSwing;
        public final float limbSwingAmount;
        public final float ageInTicks;
        public final float headPitch;
        public final float yaw, lastYaw, yawOffset, lastYawOffset;

        public PlayerRenderInfo(EntityPlayer entity, ModelPlayer model, float limbSwing, float limbSwingAmount, float ageInTicks, float headPitch, float yaw, float lastYaw, float yawOffset, float lastYawOffset) {
            this.entity = entity;
            this.model = model;
            this.limbSwing = limbSwing;
            this.limbSwingAmount = limbSwingAmount;
            this.ageInTicks = ageInTicks;
            this.headPitch = headPitch;
            this.yaw = yaw;
            this.lastYaw = lastYaw;
            this.yawOffset = yawOffset;
            this.lastYawOffset = lastYawOffset;
        }
    }
}
