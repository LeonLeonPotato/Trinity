package me.leon.trinity.hacks.client;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventLoadConfig;
import me.leon.trinity.events.settings.EventModeChange;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.util.ResourceLocation;

public class ClickGUI extends Module {
	public static BooleanSetting main = new BooleanSetting("Main", true, false);
	public static ModeSetting background = new ModeSetting("Background", main, "Blur", "None", "Blur", "Darken", "Both");
	public static BooleanSetting scroll = new BooleanSetting("Scroll", main, true);
	public static SliderSetting scrollSpeed = new SliderSetting("Speed", scroll, 0, 10, 50, true);
	public static ModeSetting scrollAnimations = new ModeSetting("Animation", scroll, "Bezier", "Bezier", "Half", "None");
	public static BooleanSetting pause = new BooleanSetting("Pause Game", main, false);

	public static BooleanSetting frame = new BooleanSetting("Frame", true, false);
	public static ColorSetting frameColor = new ColorSetting("Frame Color", frame, 60, 63, 65, 255, false);
	public static ColorSetting nameColorFrame = new ColorSetting("Frame Name", frame, 169, 183, 198, 255, false);
	public static ModeSetting frameSeparator = new ModeSetting("Separator", frame, "Rainbow", "Normal", "Rolling", "None");
	public static ColorSetting separatorColor = new ColorSetting("Separator Color", frame, 255, 255, 255, 255, true);
	public static SliderSetting width = new SliderSetting("Width", frame, 100, 125, 200, true);

	public static BooleanSetting button = new BooleanSetting("Button", true, false);
	public static ColorSetting nameColorButton = new ColorSetting("Button Name", button, 169, 183, 198, 255, false);
	public static ColorSetting disabledColor = new ColorSetting("Disabled Color", button, 43, 43, 43, 200, false);
	public static ColorSetting enabledColor = new ColorSetting("Enabled Color", button, 97, 97, 97, 200, true);

	public static BooleanSetting settings = new BooleanSetting("Settings", true, false);
	public static ColorSetting backgroundColor = new ColorSetting("Background Color", settings, 43, 43, 43, 200, false);
	public static ColorSetting nameColorSetting = new ColorSetting("Setting Name", settings, 169, 183, 198, 255, false);
	public static ColorSetting disabledBooleanColor = new ColorSetting("Disabled Color", settings, 43, 43, 43, 200, false);
	public static ColorSetting enabledBooleanColor = new ColorSetting("Enabled Color", settings, 97, 97, 97, 200, true);
	public static ColorSetting sliderColor = new ColorSetting("Slider Color", settings, 97, 97, 97, 200, false);
	public static ModeSetting barMode = new ModeSetting("Bar Mode", settings, "Rainbow", "Rainbow", "Static", "None");
	public static ColorSetting barColor = new ColorSetting("Bar Color", settings, 217, 217, 217, 200, true);
	
	@EventHandler
	private final Listener<EventModeChange> toggleListener = new Listener<>(event -> {
		updateShader(event);
	});
	
	@EventHandler
	private final Listener<EventLoadConfig> loadPresetListener = new Listener<>(event -> {
		if (event.getStage() == EventStage.PRE)
			stopShader();
		else if (event.getStage() == EventStage.POST) {
			loadShader();
		}
	});

	public ClickGUI() {
		super("ClickGUI", "The ClickGUI of the client", Category.CLIENT);
	}

	public static void loadShader() {
		if (background.getValue().equalsIgnoreCase("Blur") || background.getValue().equalsIgnoreCase("Both")) {
			mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
		}
	}

	public static void stopShader() {
		if (background.getValue().equalsIgnoreCase("Blur") || background.getValue().equalsIgnoreCase("Both")) {
			mc.entityRenderer.stopUseShader();
		}
	}

	public static void updateShader(EventModeChange event) {
		if (event.getStage() != EventStage.POST) return;
		if (event.getSet() == background) {
			if (!background.getValue().equalsIgnoreCase("Blur") && !background.getValue().equalsIgnoreCase("Both")) {
				mc.entityRenderer.getShaderGroup().deleteShaderGroup();
			} else if (background.getValue().equalsIgnoreCase("Blur") || background.getValue().equalsIgnoreCase("Both")) {
				mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
			}
		}
	}

	@Override
	public void onEnable() {
		mc.displayGuiScreen(Trinity.clickGui);
		loadShader();
		this.setEnabled(false);
	}

	@Override
	public void onUpdate() {
		if (mc.world == null) this.setEnabled(false);
	}

	@Override
	public boolean shouldSave() {
		return false;
	}
}
