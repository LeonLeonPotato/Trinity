package me.leon.trinity.main;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import me.leon.trinity.config.rewrite.LoadConfig;
import me.leon.trinity.config.rewrite.PresetManager;
import me.leon.trinity.config.rewrite.PresetObj;
import me.leon.trinity.config.rewrite.SaveConfig;
import me.leon.trinity.gui.ClickGui;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.managers.*;
import me.leon.trinity.utils.misc.FontUtil;
import me.zero.alpine.fork.bus.EventBus;
import me.zero.alpine.fork.bus.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.net.Proxy;

/**
 * my second mod, please work!
 */
@Mod(
		modid = Trinity.MODID,
		name = Trinity.CLIENTNAME,
		version = Trinity.VERSION,
		acceptedMinecraftVersions = "[1.12.2]"
)
public class Trinity {
	public final static String MODID = "trinity";
	public final static String CLIENTNAME = "Trinity";
	public final static String VERSION = "0.2";
	public static Logger LOGGER;
	public static EventBus dispatcher;
	public static EventBus settingsDispatcher;
	public static ClickGui clickGui;
	public static CapeManager capeManager;
	public static ModuleManager moduleManager;
	public static PresetManager presetManager;
	public static FriendManager friendManager;
	public static SpoofingManager spoofingManager;
	public static TickrateManager tickrateManager;
	public static HudManager hudManager;
	public static PresetObj currentPreset;
	public static NotificationManager notificationManager;
	public static TotempopManager totempopManager;

	private static boolean obfEnv;

	public static boolean finishLoading = false;

	@Mod.Instance
	private static Trinity INSTANCE;

	public Trinity() {
		INSTANCE = this;
	}

	public static boolean isObfEnv() {
		return obfEnv;
	}

	@Mod.EventHandler
	public void init(FMLPostInitializationEvent event) {
		finishLoading = true;
	}

	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this); // subscribe to event bus
		LOGGER = LogManager.getLogger("Trinity"); // init logger
		obfEnv = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"); // are we running in a obfuscated environment?

		LOGGER.info("Trinity Pre Initialization: ----------------------");

		// init dispatchers
		dispatcher = new EventManager();
		settingsDispatcher = new EventManager();

		// init managers
		moduleManager = new ModuleManager();
		capeManager = new CapeManager();
		friendManager = new FriendManager();
		spoofingManager = new SpoofingManager();
		tickrateManager = new TickrateManager();
		hudManager = new HudManager();
		presetManager = new PresetManager();
		notificationManager = new NotificationManager();
		totempopManager = new TotempopManager();

		// init gui(s)
		clickGui = new ClickGui();

		Runtime.getRuntime().addShutdownHook(new SaveConfig());
		Runtime.getRuntime().addShutdownHook(new Thread(RPCHandler::shutdown));

		LoadConfig.loadPreset();
		LoadConfig.load();

		FontUtil.load();

		if (ModuleManager.getMod(ClickGUI.class).getKey() == 0) {
			ModuleManager.getMod(ClickGUI.class).setKey(Keyboard.KEY_RSHIFT);
		}

		ModuleManager.modules.forEach(m -> Trinity.settingsDispatcher.subscribe(m));

		LOGGER.info("Trinity Post Initialization: ----------------------");
	}

	@SubscribeEvent
	public void toggleKeys(InputEvent.KeyInputEvent event) {
		if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null) {
			if (Keyboard.isCreated()) {
				if (Keyboard.getEventKeyState()) {
					if(Keyboard.getEventKey() != 0) {
						for (final Module mod : ModuleManager.modules) {
							if (mod.getKey() == Keyboard.getEventKey()) {
								mod.toggle();
							}
						}
					}
				}
			}
		}
	}
}
