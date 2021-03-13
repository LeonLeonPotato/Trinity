package me.leon.trinity.hacks;

import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.Setting;
import me.zero.alpine.fork.listener.Listenable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public abstract class Module implements Listenable {
    private String name;
    private String description;
    private Category cat;
    private int key;
    private boolean enabled;

    public Module(String name, String description, Category cat) {
        this.name = name;
        this.description = description;
        this.cat = cat;
        this.enabled = true;
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

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(this.enabled) {
            onEnable0();
            onEnable();
        } else {
            onDisable0();
            onDisable();
        }
    }

    public void addSetting(Setting set) {
        Trinity.settingManager.addSets(set);
    }
}
