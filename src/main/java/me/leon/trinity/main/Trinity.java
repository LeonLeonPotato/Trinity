package me.leon.trinity.main;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.config.Preset;
import me.leon.trinity.config.PresetManager;
import me.leon.trinity.config.loadConfig;
import me.leon.trinity.config.saveConfig;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.hud.HUDeditor;
import me.leon.trinity.managers.*;
import me.leon.trinity.utils.misc.FontUtil;
import me.zero.alpine.fork.bus.EventBus;
import me.zero.alpine.fork.bus.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

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

    public static ClickGui clickGui;
    public static HUDeditor hudEditor;

    public static CapeManager capeManager;
    public static ModuleManager moduleManager;
    public static PresetManager presetManager;
    public static SettingManager settingManager;
    public static FontUtil fontManager;
    public static FriendManager friendManager;
    public static RotationManager rotationManager;
    public static TickrateManager tickrateManager;
    public static HudManager hudManager;

    public static Preset curPreset;

    @Mod.Instance
    private static Trinity INSTANCE;

    public Trinity() {
        INSTANCE = this;
    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this); // subscribe to event bus
        LOGGER = LogManager.getLogger("Trinity"); // init logger, we don't want to use System.out.println();
        obfEnv = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"); // are we running in a obfuscated environment?

        // init dispatchers
        dispatcher = new EventManager();
        settingsDispatcher = new EventManager();

        // init managers
        settingManager = new SettingManager();
        moduleManager = new ModuleManager();
        doPresetThing(); // im a lazy boi
        fontManager = new FontUtil();
        fontManager.load();
        capeManager = new CapeManager();
        friendManager = new FriendManager();
        rotationManager = new RotationManager();
        tickrateManager = new TickrateManager();
        hudManager = new HudManager();

        // init gui(s)
        clickGui = new ClickGui();
        hudEditor = new HUDeditor();

        Runtime.getRuntime().addShutdownHook(new saveConfig());
        loadConfig.LoadConfig.loadBinds();
        loadConfig.LoadConfig.loadFriends();
        loadConfig.LoadConfig.loadGUI();
        loadConfig.LoadConfig.loadHud();
        loadConfig.LoadConfig.loadModules(false);
        loadConfig.LoadConfig.loadSearch();
        loadConfig.LoadConfig.loadSettings();

        if(ModuleManager.getMod(ClickGUI.class).getKey() == 0) {
            ModuleManager.getMod(ClickGUI.class).setKey(Keyboard.KEY_RSHIFT);
        }

        ModuleManager.modules.forEach(m -> Trinity.settingsDispatcher.subscribe(m));
    }

    @SubscribeEvent
    public void toggleKeys(InputEvent.KeyInputEvent event) {
        if(Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null) {
            if(Keyboard.isCreated()) {
                if(Keyboard.getEventKeyState()) {
                    for(Module mod : moduleManager.modules) {
                        if(mod.getKey() == Keyboard.getEventKey()) {
                            mod.toggle();
                        }
                    }
                }
            }
        }
    }

    public static boolean isObfEnv() {
        return obfEnv;
    }

    private void doPresetThing() {
        //see if essentials exists, if it doesnt, make it, and write default in it
        if(!saveConfig.presetDir.exists()) {
            try {
                if(!saveConfig.confDir.exists()) saveConfig.confDir.mkdirs();
                PrintWriter writer = new PrintWriter(new FileOutputStream(saveConfig.presetDir), true);
                writer.write("Default");
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(!saveConfig.confDir.exists()) {
            saveConfig.confDir.mkdirs();
        }

        presetManager = new PresetManager();
        LOGGER.info("PresetManager initiated!");

        //curPreset must be loaded first
        loadConfig.LoadConfig.loadPreset();

        try {
            if(!curPreset.file.exists()) {
                curPreset.file.mkdirs();
                curPreset.GUI.createNewFile();
                curPreset.settings.createNewFile();
                curPreset.search.createNewFile();
                curPreset.Friends.createNewFile();
                curPreset.enabledModules.createNewFile();
                curPreset.binds.createNewFile();
                curPreset.HUD.createNewFile();
            }
        } catch (Exception ignored) {}
    }
}
