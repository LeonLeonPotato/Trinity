package me.leon.trinity.hacks.render;

import me.leon.trinity.events.TrinityEvent;
import me.leon.trinity.events.main.EventRenderEntityCrystal;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.rendering.Tessellator;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Chams extends Module {
    public static ModeSetting mode = new ModeSetting("Mode", "Textured", "Normal", "Textured", "Wireframe");
    public static ModeSetting wallsMode = new ModeSetting("Walls", "XQZ", "XQZ", "Normal", "None");
    public static BooleanSetting staticC = new BooleanSetting("Static", false);
    public static SliderSetting width = new SliderSetting("Width", 0.1, 1, 4, false);
    public static ColorSetting color = new ColorSetting("Color", 119, 0, 255, 200, false);
    public static ColorSetting xqzColor = new ColorSetting("XQZ Color", 119, 0, 255, 200, false);

    public static BooleanSetting targeting = new BooleanSetting("Filters", true, false);
    public static SliderSetting range = new SliderSetting("Range", targeting, 10, 200, 300, true);
    public static BooleanSetting players = new BooleanSetting("Players", targeting, true);
    public static BooleanSetting mobs = new BooleanSetting("Mobs", targeting, true);
    public static BooleanSetting passive = new BooleanSetting("Passive", targeting, true);
    public static BooleanSetting neutral = new BooleanSetting("Neutral", targeting, true);
    public static BooleanSetting exp = new BooleanSetting("EXP", targeting, true);
    public static BooleanSetting items = new BooleanSetting("Items", targeting, true);
    public static BooleanSetting crystals = new BooleanSetting("Crystals", targeting, true);
    public static BooleanSetting misc = new BooleanSetting("Misc", targeting, true);

    public Chams() {
        super("Chams", "Render entities through walls", Category.RENDER);
    }

    @Override
    public String getHudInfo() {
        return mode.getValue();
    }

    @EventHandler
    private final Listener<EventRenderEntityCrystal> renderEntityCrystalListener = new Listener<>(event -> {
        chams(event, event.modelBase, event.entityIn, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
    });

    private void chams(TrinityEvent event, final ModelBase model, final Entity entity, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        boolean shadows = mc.gameSettings.entityShadows;
        mc.renderManager.setRenderShadow(false);

        switch (mode.getValue()) {
            case "Wireframe": {
                GL11.glPushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(1032, 6913);
                GL11.glDisable(3553);
                GL11.glDisable(2896);
                GL11.glEnable(2848);
                GL11.glEnable(3042);
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.glLineWidth((float)width.getValue());

                if (wallsMode.getValue().equalsIgnoreCase("XQZ")) {
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(false);

                    GL11.glDepthRange(0.1, 1.0f);
                    GL11.glDepthFunc(GL11.GL_GREATER);
                    Tessellator.color(xqzColor.getValue());
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glDepthFunc(GL11.GL_LESS);
                    GL11.glDepthRange(0.0f, 1.0f);

                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(false);

                    GlStateManager.clearColor(1,1,1,1);
                    Tessellator.color(color.getValue());
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(true);
                } else {
                    if(wallsMode.getValue().equalsIgnoreCase("Normal")) {
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glDepthMask(false);
                    }

                    Tessellator.color(color.getValue());
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

                    if(wallsMode.getValue().equalsIgnoreCase("Normal")) {
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        GL11.glDepthMask(true);
                    }
                }

                GL11.glPopAttrib();
                GL11.glPopMatrix();

                event.cancel();

                break;
            }
            case "Textured": {
                GL11.glPushAttrib(1048575);
                GL11.glDisable(3008);
                GL11.glDisable(3553);
                GL11.glDisable(2896);
                GL11.glEnable(3042);
                //GL11.glDisable(GL11.GL_CULL_FACE);
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GL11.glLineWidth(1.5f);
                GL11.glEnable(2960);

                Color visibleColor = color.getValue();
                Color hiddenColor = xqzColor.getValue();
                if (wallsMode.getValue().equalsIgnoreCase("XQZ")) {
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(false);

                    // walls
                    GL11.glDepthRange(0.01, 1.0f);
                    GL11.glDepthFunc(GL11.GL_GREATER);
                    GlStateManager.color((float) hiddenColor.getRed() / 255.0f, (float) hiddenColor.getGreen() / 255.0f, (float) hiddenColor.getBlue() / 255.0f,(float) hiddenColor.getAlpha() / 255.0f);
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glDepthFunc(GL11.GL_LESS);
                    GL11.glDepthRange(0.0f, 1.0f);

                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(false);

                    // normal
                    GlStateManager.clearColor(1,1,1,1);
                    GlStateManager.color((float) visibleColor.getRed() / 255.0f, (float) visibleColor.getGreen() / 255.0f, (float) visibleColor.getBlue() / 255.0f, (float) visibleColor.getAlpha() / 255f);
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

                } else {
                    GL11.glEnable(10754);

                    if(wallsMode.getValue().equalsIgnoreCase("Normal")) {
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glDepthMask(false);
                    }

                    GlStateManager.color((float) visibleColor.getRed() / 255.0f, (float) visibleColor.getGreen() / 255.0f, (float) visibleColor.getBlue() / 255.0f, (float) visibleColor.getAlpha() / 255f);
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

                    if(wallsMode.getValue().equalsIgnoreCase("Normal")) {
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        GL11.glDepthMask(true);
                    }
                }

                GL11.glEnable(3042);
                GL11.glEnable(2896);
                GL11.glEnable(3553);
                GL11.glEnable(3008);
                GL11.glPopAttrib();

                event.cancel();
                break;
            }
            case "Normal": {
                GL11.glDisable(GL11.GL_DEPTH_TEST);

                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

                GL11.glEnable(GL11.GL_DEPTH_TEST);

                event.cancel();
                break;
            }
        }

        mc.renderManager.setRenderShadow(shadows);
    }
}
