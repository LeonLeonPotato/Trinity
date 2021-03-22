package me.leon.trinity.hacks.misc;

import com.mojang.authlib.GameProfile;
import me.leon.trinity.events.main.MoveEvent;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.Slider;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class FreeCam extends Module {
    private static final Mode mode = new Mode("Mode", "Camera", "Camera", "Normal");
    private static final Slider speed = new Slider("Speed", 0, 10, 20, false);

    public FreeCam() {
        super("FreeCam", "Out-of-body experience", Category.MISC);
    }

    private EntityPlayerCamera other;
    private Entity renderViewOg = null;

    @Override
    public void onDisable() {
        if(nullCheck()) return;
        if(other == null) return;
        mc.world.removeEntity(other);
        setRender(mc.player);
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if (nullCheck()) {
            this.setEnabled(false);
            return;
        }
        if(mc.currentScreen != null) return;
        if(other == null) return;

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

        float playerSpeed = (float) (speed.getValue() / 10);
        float moveForward = getMoveForward();
        float moveStrafe = getMoveStrafe();
        float rotationYaw = other.rotationYaw;

        double x = ((moveForward * playerSpeed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))) + (moveStrafe * playerSpeed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))));
        double z = ((moveForward * playerSpeed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))) - (moveStrafe * playerSpeed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))));

        other.noClip = true;
        other.move(MoverType.SELF, x, getY(), z);
    }

    @Override
    public void onEnable() {
        if(nullCheck()) return;

        this.other = new EntityPlayerCamera(mc.world, mc.player.gameProfile);
        other.copyLocationAndAnglesFrom(mc.player);
        other.noClip = true;
        EntityPlayer player = mc.player;

        if (player != null)
        {
            other.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            other.setRotationYawHead(player.rotationYaw);
        }
        mc.world.addEntityToWorld(1234, other);
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

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        RenderManager manager = mc.getRenderManager();
        if (event.getEntityPlayer().isUser())
        {
            this.renderViewOg = other;
            manager.renderViewEntity = mc.player;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        if(other == null) return;
        mc.getRenderManager().renderEntityStatic(this.other, event.getPartialTicks(), false);
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<EntityPlayer> event)
    {
        if (this.renderViewOg != null)
        {
            mc.getRenderManager().renderViewEntity = this.renderViewOg;
            this.renderViewOg = null;
        }
    }

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
