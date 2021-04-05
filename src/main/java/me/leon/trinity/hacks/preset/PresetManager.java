package me.leon.trinity.hacks.preset;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.config.rewrite.LoadConfig;
import me.leon.trinity.config.rewrite.PresetObj;
import me.leon.trinity.config.rewrite.SaveConfig;
import me.leon.trinity.events.settings.EventBooleanToggle;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.StringInput;
import me.leon.trinity.setting.settings.sub.SubBoolean;
import me.leon.trinity.utils.misc.MessageBus;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class PresetManager extends Module {
    public static StringInput name = new StringInput("Name", "Default");
    public static Boolean create = new Boolean("Create", false);
    public static Boolean delete = new Boolean("Delete", false);
    public static StringInput to = new StringInput("To", "Default");
    public static Boolean rename = new Boolean("Rename", false);

    public PresetManager() {
        super("PresetManager", "Manage presets", Category.PRESETS);
    }

    @EventHandler
    private final Listener<EventBooleanToggle> toggleListener = new Listener<>(event -> {
        if(event.getSet() == create) {
            event.cancel();
            if(me.leon.trinity.config.rewrite.PresetManager.find(name.val) != null) return;
            PresetObj obj = new PresetObj(name.val);

            SaveConfig.runStatic();
            Trinity.currentPreset = obj;
            me.leon.trinity.config.rewrite.PresetManager.presets.add(obj);
            ClickGui.getFrameFromCategory("PRESETS").refresh();
            SaveConfig.runStatic();
            LoadConfig.load();
        }
        if(event.getSet() == delete) {
            event.cancel();
            final PresetObj obj = me.leon.trinity.config.rewrite.PresetManager.find(name.val);
            if(obj == null) return;

            if(me.leon.trinity.config.rewrite.PresetManager.find("Default") == null) {
                PresetObj def = new PresetObj("Default");
                Trinity.currentPreset = def;
                me.leon.trinity.config.rewrite.PresetManager.presets.add(def);
                ClickGui.getFrameFromCategory("PRESETS").refresh();
            } else {
                Trinity.currentPreset = me.leon.trinity.config.rewrite.PresetManager.find("Default");
            }
            try {
                FileUtils.forceDelete(new File("Trinity/" + name.val + "/"));
                me.leon.trinity.config.rewrite.PresetManager.presets.remove(obj);
                ClickGui.getFrameFromCategory("PRESETS").refresh();
            } catch (IOException e) {
                e.printStackTrace();
                MessageBus.sendClientMessage("Unable to delete preset " + name.val, true);
            }
            LoadConfig.load();
        }
        if(event.getSet() == rename) {
            event.cancel();
            final PresetObj obj = me.leon.trinity.config.rewrite.PresetManager.find(name.val);
            if(obj == null) return;
            if(to.val.equals("")) return;
            if(to.val.equals(name.val)) return;
            if(me.leon.trinity.config.rewrite.PresetManager.find(to.val) != null) return;

            try {
                FileUtils.moveDirectory(new File("Trinity/" + name.val + "/"), new File("Trinity/" + to.val + "/"));
                obj.name = to.val;
            } catch (IOException e) {
                e.printStackTrace();
                MessageBus.sendClientMessage("Unable to rename preset " + name.val, true);
            }
        }
    });

    @Override
    public void onEnable() {
        this.setEnabled(false);
    }
}
