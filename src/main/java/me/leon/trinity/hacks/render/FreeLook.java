package me.leon.trinity.hacks.render;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.settings.Slider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class FreeLook extends Module {

    private static float cameraYaw;
    private static float cameraPitch;
    private static float playerPitch;
    private static float playerYaw;
    private static boolean enabled = false;

    public static Slider range = new Slider("Range", 0, 5, 7, false);

    public FreeLook() {
        super("FreeLook", "LUNAR MODE", Category.RENDER);
    }

    private void updateCamera(boolean start) {
        if(mc.currentScreen != null || !mc.inGameHasFocus) {
            return;
        }
        Entity view = mc.getRenderViewEntity();

        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F;

        double dx = Mouse.getDX() * f1 * 0.15D;
        double dy = Mouse.getDY() * f1 * 0.15D;

        cameraYaw += dx;
        cameraPitch += dy;

        cameraPitch = MathHelper.clamp(cameraPitch, -90.0F, 90.0F);
        cameraYaw = MathHelper.clamp(cameraYaw, (mc.player.rotationYaw + -100.0F), (mc.player.rotationYaw + 100.0F));

        mc.player.rotationYaw = mc.player.prevRotationYaw = playerYaw;
        mc.player.rotationPitch = mc.player.prevRotationPitch = playerPitch;

        if(start) {
            view.rotationPitch = cameraPitch;
            view.rotationYaw = cameraYaw;
        } else {
            view.rotationYaw = mc.player.rotationYaw - cameraYaw + playerYaw;
            view.prevRotationYaw = mc.player.prevRotationYaw - cameraYaw + playerYaw;

            view.rotationPitch = -playerPitch;
            view.prevRotationPitch = -playerPitch;
        }
    }

    public static void cameraEnabled(boolean start) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity player = mc.getRenderViewEntity();
        if (player == null && !mc.inGameHasFocus) {
            return;
        }
        if (mc.inGameHasFocus) {
            updateCamera2();
            if (start) {

                player.rotationYaw = player.prevRotationYaw = cameraYaw;
                player.rotationPitch = player.prevRotationPitch = -cameraPitch;

            } else {
                player.rotationYaw = mc.player.rotationYaw - cameraYaw + playerYaw;
                player.prevRotationYaw = mc.player.prevRotationYaw - cameraYaw + playerYaw;

                player.rotationPitch = -playerPitch;
                player.prevRotationPitch = -playerPitch;
            }
        }

    }

    private static void updateCamera2() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.inGameHasFocus) {

            float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;

            double dx = Mouse.getDX() * f1 * 0.15D;
            double dy = Mouse.getDY() * f1 * 0.15D;

            cameraYaw += dx;
            cameraPitch += dy;

            cameraPitch = MathHelper.clamp(cameraPitch, -90.0F, 90.0F);
            cameraYaw = MathHelper.clamp(cameraYaw, (playerYaw + -100.0F), (playerYaw + 100.0F));
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        this.setEnabled(false);
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if (nullCheck()) {
            return;
        }
        updateCamera(event.phase.equals(TickEvent.Phase.START));
        if(enabled) {
            cameraEnabled(event.phase == TickEvent.Phase.START);
            enabled = true;
        }
    }

    @Override
    public void onDisable() {
        enabled = false;
    }

    @Override
    public void onEnable() {
        playerPitch = cameraPitch = mc.player.rotationPitch;
        playerYaw = cameraYaw = mc.player.rotationYaw;
    }
}