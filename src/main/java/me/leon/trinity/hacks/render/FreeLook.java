package me.leon.trinity.hacks.render;

import baritone.api.utils.RotationUtils;
import com.mojang.authlib.GameProfile;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Slider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class FreeLook extends Module {

    private static float cameraYaw;
    private static float cameraPitch;
    private static EntityPlayerCamera camera;
    private static Entity renderViewOg;

    public static Slider range = new Slider("Range", 0, 5, 7, false);

    public FreeLook() {
        super("FreeLook", "LUNAR MODE", Category.RENDER);
    }

    private void updateCamera() {
        if (nullCheck() || camera == null) {
            return;
        }
        if (mc.inGameHasFocus) {
            float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;

            double dx = Mouse.getDX() * f1 * 0.15D;
            double dy = Mouse.getDY() * f1 * 0.15D;

            cameraYaw += dx;
            cameraPitch += dy * -1;

            cameraPitch = MathHelper.clamp(cameraPitch, -90.0F, 90.0F);
            cameraYaw = MathHelper.clamp(cameraYaw, (cameraYaw + -100.0F), (cameraYaw + 100.0F));

            camera.rotationPitch = cameraPitch;
            camera.rotationYaw = cameraYaw;
        }
    }

    private void updateCamera2() {
        double x = (mc.player.posX + (range.getValue() * Math.cos(Math.toRadians(cameraPitch))));
        double z = (mc.player.posY + (range.getValue() * Math.sin(Math.toRadians(cameraPitch))));
        double dist = Math.abs(mc.player.posX - x);

        double x1 = (mc.player.posX + (dist * Math.cos(Math.toRadians(cameraYaw - 90))));
        double z1 = (mc.player.posZ + (dist * Math.sin(Math.toRadians(cameraYaw - 90))));

        camera.setPosition(x1, z, z1);
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        this.setEnabled(false);
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if (nullCheck() || camera == null) {
            return;
        }
        updateCamera();
        updateCamera2();
    }

    @Override
    public void onEnable() {
        if(nullCheck()) return;
        cameraPitch = mc.player.rotationPitch;
        cameraYaw = mc.player.rotationYaw;
        camera = new EntityPlayerCamera(mc.world, mc.player.gameProfile);
        EntityPlayer player = mc.player;

        if (player != null)
        {
            camera.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            camera.setRotationYawHead(player.rotationYaw);
        }
        mc.world.addEntityToWorld(-9283, camera);
        mc.renderViewEntity = camera;
    }

    @Override
    public void onDisable() {
        if (nullCheck() || camera == null) {
            return;
        }
        mc.renderViewEntity = mc.player;
        mc.world.removeEntity(camera);
    }


    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        RenderManager manager = mc.getRenderManager();
        if (event.getEntityPlayer().isUser())
        {
            renderViewOg = camera;
            manager.renderViewEntity = mc.player;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        if(camera == null) return;
        mc.getRenderManager().renderEntityStatic(camera, event.getPartialTicks(), false);
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<EntityPlayer> event)
    {
        if (renderViewOg != null)
        {
            mc.getRenderManager().renderViewEntity = renderViewOg;
            renderViewOg = null;
        }
    }

    @SuppressWarnings("all")
    private class EntityPlayerCamera extends EntityOtherPlayerMP
    {
        public EntityPlayerCamera(World worldIn, GameProfile gameProfileIn)
        {
            super(worldIn, gameProfileIn);
        }

        @Override
        public boolean isInvisible()
        {
            return true;
        }

        @Override
        public boolean isInvisibleToPlayer(EntityPlayer player)
        {
            return true;
        }

        @Override
        public boolean isSpectator()
        {
            return false;
        }
    }
}