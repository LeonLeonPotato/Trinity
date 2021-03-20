package me.leon.trinity.hacks.misc;

import baritone.Baritone;
import baritone.launch.mixins.MixinEntityRenderer;
import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.events.main.EventRenderEntityModel;
import me.leon.trinity.events.main.MoveEvent;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.Slider;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class FreeCam extends Module {
    private static Mode mode = new Mode("Mode", "Camera", "Camera", "Normal");
    private static Slider speed = new Slider("Speed", 0, 10, 20, false);

    public FreeCam() {
        super("FreeCam", "Out-of-body experience", Category.MISC);
    }

    private EntityZombie other;
    private EntityOtherPlayerMP other0;

    @Override
    public void onDisable() {
        if(nullCheck()) return;
        if(other == null || other0 == null) return;
        mc.player.setInvisible(false);
        mc.world.removeEntity(other);
        mc.world.removeEntity(other0);
        setRender(mc.player);
    }

    @SuppressWarnings("all")
    @SubscribeEvent
    public void onFast(TickEvent event) {
        if(nullCheck()) {
            this.setEnabled(false);
            return;
        }
        if(other == null || other0 == null) return;

        /*
        other0.copyLocationAndAnglesFrom(mc.player);
        other0.inventory.copyInventory(mc.player.inventory);
        other0.rotationYawHead = mc.player.rotationYawHead;
        other0.limbSwing = mc.player.limbSwing;
        other0.limbSwingAmount = mc.player.limbSwingAmount;

         */

        float playerSpeed = (float) (speed.getValue() / 10);
        float moveForward = getMoveForward();
        float moveStrafe = getMoveStrafe();
        float rotationYaw = other.rotationYaw;

        double x = ((moveForward * playerSpeed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))) + (moveStrafe * playerSpeed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))));
        double z = ((moveForward * playerSpeed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))) - (moveStrafe * playerSpeed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))));

        other.noClip = true;
        other.move(MoverType.PLAYER, x, getY(), z);
        other.noClip = true;
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if(mc.currentScreen != null) return;

        double cameraYaw = other.rotationYaw;
        double cameraPitch = other.rotationPitch;

        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F;

        double dx = Mouse.getDX() * f1 * 0.15D;
        double dy = Mouse.getDY() * f1 * 0.15D;

        cameraYaw = cameraYaw + dx;
        cameraPitch = cameraPitch - dy;

        cameraPitch = MathHelper.clamp(cameraPitch, -90.0F, 90.0F);

        other.rotationPitch = (float) cameraPitch;
        other.rotationYaw = (float) cameraYaw;
    }

    @EventHandler
    private final Listener<MoveEvent> eventMove = new Listener<>(event -> {
        if(event.type == MoverType.SELF) {
            //event.cancel();
        }
    });

    @Override
    public void onEnable() {
        if(nullCheck()) return;

        this.other = new EntityZombie(mc.world);
        other.copyLocationAndAnglesFrom(mc.player);
        other.setInvisible(true);
        mc.world.addEntityToWorld(1234, other);

        this.other0 = new EntityOtherPlayerMP(mc.world, mc.player.gameProfile);
        other0.copyLocationAndAnglesFrom(mc.player);
        mc.world.addEntityToWorld(1235, other0);
        setRender(other);
    }

    private float getMoveForward() {
        if(mc.gameSettings.keyBindForward.isKeyDown() && mc.gameSettings.keyBindBack.isKeyDown()) {
            return 0.0f;
        }
        if(mc.gameSettings.keyBindBack.isKeyDown()) {
            return -1.0f;
        }
        if(mc.gameSettings.keyBindForward.isKeyDown()) {
            return 1.0f;
        }
        return 0.0f;
    }

    private float getMoveStrafe() {
        if(mc.gameSettings.keyBindLeft.isKeyDown() && mc.gameSettings.keyBindRight.isKeyDown()) {
            return 0.0f;
        }
        if(mc.gameSettings.keyBindRight.isKeyDown()) {
            return -1.0f;
        }
        if(mc.gameSettings.keyBindLeft.isKeyDown()) {
            return 1.0f;
        }
        return 0.0f;
    }

    private double getY() {
        if(mc.gameSettings.keyBindSneak.isKeyDown() && mc.gameSettings.keyBindJump.isKeyDown()) {
            return 0.0f;
        }
        if(mc.gameSettings.keyBindJump.isKeyDown()) {
            return (speed.getValue() / 10);
        }
        if(mc.gameSettings.keyBindSneak.isKeyDown()) {
            return -(speed.getValue() / 10);
        }
        return 0.0;
    }

    private void setRender(Entity entity) {
        mc.renderViewEntity = entity;
        if (Trinity.moduleManager.getMod(ClickGUI.class).isEnabled()) {
            if(ClickGUI.background.getValue().equalsIgnoreCase("Blur") || ClickGUI.background.getValue().equalsIgnoreCase("Both")) {
                mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
            }
        }
    }
}
