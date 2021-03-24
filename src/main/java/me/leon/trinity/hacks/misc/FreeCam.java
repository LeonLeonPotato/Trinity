package me.leon.trinity.hacks.misc;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import com.mojang.authlib.GameProfile;
import me.leon.trinity.events.main.EventPacketSend;
import me.leon.trinity.events.main.MoveEvent;
import me.leon.trinity.events.settings.EventBooleanToggle;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.Mode;
import me.leon.trinity.setting.settings.Slider;
import me.leon.trinity.utils.entity.PlayerUtils;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

public class FreeCam extends Module {
    public static final Mode mode = new Mode("Mode", "Camera", "Camera", "Normal");
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
        if(!mode.getValue().equalsIgnoreCase("Camera")) return;

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
        other.move(MoverType.PLAYER, x, getY(), z);
    }

    @EventHandler
    private final Listener<EventPacketSend> packetListener = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketPlayer && mode.getValue().equalsIgnoreCase("Normal")) {
            event.cancel();
        }
    });

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        event.setCanceled(true);
    }

    @Override
    public void onEnable() {
        if(nullCheck()) return;
        mc.player.motionX = mc.player.motionZ = 0;

        this.other = new EntityPlayerCamera(mc.world, mc.player.gameProfile);
        other.copyLocationAndAnglesFrom(mc.player);
        other.noClip = true;
        EntityPlayer player = mc.player;

        if (player != null)
        {
            if(mode.getValue().equalsIgnoreCase("Normal")) other.inventory.copyInventory(mc.player.inventory);
            other.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            other.setRotationYawHead(player.rotationYaw);
        }
        mc.world.addEntityToWorld(-1234, other);
        if(mode.getValue().equalsIgnoreCase("Camera"))
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

    @EventHandler
    private final Listener<MoveEvent> moveEventListener = new Listener<>(event -> {
        if(mode.getValue().equalsIgnoreCase("Normal")) {
            float playerSpeed = (float) (speed.getValue() / 2);
            float moveForward = mc.player.movementInput.moveForward;
            float moveStrafe = mc.player.movementInput.moveStrafe;
            float rotationYaw = mc.player.rotationYaw;

            double x = ((moveForward * playerSpeed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))) + (moveStrafe * playerSpeed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))));
            double z = ((moveForward * playerSpeed) * Math.sin(Math.toRadians((rotationYaw + 90.0f))) - (moveStrafe * playerSpeed) * Math.cos(Math.toRadians((rotationYaw + 90.0f))));

            event.x = x;
            event.z = z;
            event.y = (getY());
            event.cancel();
        }
    });

    @EventHandler
    private final Listener<EventBooleanToggle> toggleListener = new Listener<>(event -> {
        if(event.getSet() == mode) {
            this.toggle(); this.toggle();
        }
    });
    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        if(!mode.getValue().equalsIgnoreCase("Camera")) return;
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
        if(!mode.getValue().equalsIgnoreCase("Camera")) return;
        if(other == null) return;
        mc.getRenderManager().renderEntityStatic(this.other, event.getPartialTicks(), false);
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<EntityPlayer> event)
    {
        if(!mode.getValue().equalsIgnoreCase("Camera")) return;
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
        public boolean isInvisibleToPlayer(@NotNull EntityPlayer player)
        {
            return player != mc.player || !mode.getValue().equalsIgnoreCase("Camera");
        }

        @Override
        public boolean isSpectator()
        {
            return false;
        }
    }
}
