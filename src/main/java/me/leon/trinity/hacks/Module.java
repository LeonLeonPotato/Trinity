package me.leon.trinity.hacks;

import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.utils.misc.MessageBus;
import me.zero.alpine.fork.listener.Listenable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

public abstract class Module implements Listenable {
	protected static volatile Minecraft mc = Minecraft.getMinecraft();

	private ArrayList<Setting> settings;
	private boolean visible;
	private String name;
	private String description;
	private Category cat;
	private int key;
	private boolean enabled;

	public Module(String name, String description, Category cat) {
		this.name = name;
		this.description = description;
		this.cat = cat;
		this.enabled = false;
		this.visible = true;

		this.settings = new ArrayList<>();
	}

	public Module(String name, String description, Category cat, boolean visible) {
		this.name = name;
		this.description = description;
		this.cat = cat;
		this.enabled = false;
		this.visible = visible;

		this.settings = new ArrayList<>();
	}

	public void onEnable() {

	}

	public void onDisable() {

	}

	public void onUpdate() {

	}

	public void onEnable0() {
		MinecraftForge.EVENT_BUS.register(this);
		Trinity.dispatcher.subscribe(this);
	}

	public void onDisable0() {
		MinecraftForge.EVENT_BUS.unregister(this);
		Trinity.dispatcher.unsubscribe(this);
	}

	@SubscribeEvent
	public void onUpdate0(TickEvent.ClientTickEvent event) {
		onUpdate();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return cat;
	}

	public void setCategory(Category cat) {
		this.cat = cat;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (enabled != this.enabled) {
			this.enabled = enabled;
			if (this.enabled) {
				onEnable0();
				onEnable();
			} else {
				onDisable0();
				onDisable();
			}
		} else {
			if (this.enabled) {
				onEnable0();
			} else {
				onDisable0();
			}
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public Module setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void toggle() {
		this.setEnabled(!this.isEnabled());
	}

	public boolean shouldSave() {
		return true;
	}

	public String getHudInfo() {
		return null;
	}

	public void addSetting(Setting set){
		settings.add(set);
	}

	public ArrayList<Setting> getSettings() {
		return settings;
	}

	public Setting getSetting(String set) {
		return settings.stream().filter(e -> e.getName().equals(set)).findFirst().get();
	}

	protected boolean pCheck() {
		return mc.player == null;
	}

	protected boolean wCheck() {
		return mc.world == null;
	}

	protected boolean nullCheck() {
		return (mc.world == null || mc.player == null);
	}

	protected void toggleWithMessage(String message) {
		this.setEnabled(false);
		MessageBus.sendClientMessage(message, true);
	}
}
