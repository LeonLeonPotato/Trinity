package me.leon.trinity.main;

import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.Font;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.managers.SettingManager;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.utils.misc.FontUtil;
import me.zero.alpine.fork.bus.EventBus;
import me.zero.alpine.fork.bus.EventManager;
import me.zero.alpine.fork.listener.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * my second mod, please work!
 */
@Mod (
        modid = Trinity.MODID,
        name = Trinity.CLIENTNAME,
        version = Trinity.VERSION,
        acceptedMinecraftVersions = "[1.12.2]"
)
public class Trinity {
    public final static String MODID = "trinity";
    public final static String CLIENTNAME = "Trinity";
    public final static String VERSION = "0.1";
    private static boolean obfEnv;
    public static Logger LOGGER;
    public static EventBus dispatcher;
    public static EventBus settingsDispatcher;

    public static ModuleManager moduleManager;
    public static SettingManager settingManager;
    public static FontUtil fontManager;

    @Mod.Instance
    private static Trinity INSTANCE;

    public Trinity() {
        INSTANCE = this;
    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this); // subscribe to event bus
        LOGGER = LogManager.getLogger("Trinity"); // get logger
        obfEnv = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"); // are we running in a obfuscated environment?

        // init dispatchers
        dispatcher = new EventManager();
        settingsDispatcher = new EventManager();

        // init managers
        settingManager = new SettingManager();
        moduleManager = new ModuleManager();
        moduleManager.getMod(Font.class).setEnabled(true);
        fontManager = new FontUtil();
        fontManager.load();
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            FontUtil.drawString("aaaa", 300, 300, new Color(0x2A2A8B).getRGB());
        }
    }

    @SubscribeEvent
    public void toggleKeys(InputEvent.KeyInputEvent event) {
        if(Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null) {
            if(Keyboard.isCreated()) {
                if(Keyboard.getEventKeyState()) {
                    for(Module mod : moduleManager.modules) {
                        if(mod.getKey() == Keyboard.getEventKey()) {
                            mod.setEnabled(true);
                        }
                    }
                }
            }
        }
    }

    public static boolean isObfEnv() {
        return obfEnv;
    }
}
