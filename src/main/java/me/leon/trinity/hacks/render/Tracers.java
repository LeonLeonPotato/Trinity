package me.leon.trinity.hacks.render;

import io.netty.util.internal.MathUtil;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.rendering.Rainbow;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.leon.trinity.utils.rendering.Tessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Tracers extends Module {
    public static SliderSetting width = new SliderSetting("Width", 0.1, 0.5, 5, false);
    public static ColorSetting color = new ColorSetting("Color", 255, 0, 0, 100, false);
    public static BooleanSetting extra = new BooleanSetting("Extra", true);
    public static BooleanSetting targeting = new BooleanSetting("Filters", true, false);
    public static SliderSetting range = new SliderSetting("Range", targeting, 10, 200, 300, true);
    public static BooleanSetting players = new BooleanSetting("Players", targeting, true);
    public static BooleanSetting mobs = new BooleanSetting("Mobs", targeting, true);
    public static BooleanSetting passive = new BooleanSetting("Passive", targeting, true);
    public static BooleanSetting neutral = new BooleanSetting("Neutral", targeting, true);
    public static BooleanSetting vehicles = new BooleanSetting("Vehicles", targeting, true);
    public static BooleanSetting items = new BooleanSetting("Items", targeting, true);
    public static BooleanSetting crystals = new BooleanSetting("Crystals", targeting, true);

    public Tracers() {
        super("Tracers", "Draws lines to entities", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        for (Entity entity : EntityUtils.getESPTargets(players.getValue(), neutral.getValue(), mobs.getValue(), vehicles.getValue(), passive.getValue(), items.getValue(), crystals.getValue(), range.getValue()))
        {
            Tessellator.drawTracerLine(entity, event, color.getValue(), (float) width.getValue(), extra.getValue());
        }
    }
}
