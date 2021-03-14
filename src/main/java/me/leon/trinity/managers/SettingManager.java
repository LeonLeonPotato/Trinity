package me.leon.trinity.managers;

import baritone.api.BaritoneAPI;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.SubSetting;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class SettingManager {
    public ArrayList<Setting> sets;

    public SettingManager() {
        this.sets = new ArrayList<>();
    }

    public void addSets(Setting set) {
        this.sets.add(set);
    }

    public ArrayList<Setting> getSettingsByMod(String name) {
        ArrayList<Setting> sets = new ArrayList<>();
        for(Setting set : this.sets) {
            if(set.parent.getName().equalsIgnoreCase(name)) {
                sets.add(set);
            }
        }
        return sets;
    }

    public ArrayList<Setting> getSettingsByMod(Class<? extends Module> clazz) {
        ArrayList<Setting> sets = new ArrayList<>();
        for(Setting set : this.sets) {
            if(set.parent.getClass() == clazz) {
                sets.add(set);
            }
        }
        return sets;
    }

    public ArrayList<SubSetting> getSubSettingsBySetting(String name) {
        ArrayList<SubSetting> sets = new ArrayList<>();
        for(Setting set : this.sets) {
            if(set instanceof SubSetting && ((SubSetting) set).getParent().name.equalsIgnoreCase(name)) {
                sets.add((SubSetting) set);
            }
        }
        return sets;
    }

    public ArrayList<SubSetting> getSubSettingsBySetting(SettingParent set) {
        ArrayList<SubSetting> sets = new ArrayList<>();
        for(Setting set0 : this.sets) {
            if(set0 instanceof SubSetting && ((SubSetting) set0).getParent() == set) {
                sets.add((SubSetting) set0);
            }
        }
        return sets;
    }
}
