package me.leon.trinity.managers;

import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.SubSetting;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SettingManager {
    public ArrayList<Setting> sets;

    public SettingManager() {
        this.sets = new ArrayList<>();
    }

    public void addSets(Setting set) {
        this.sets.add(set);
    }

    public ArrayList<Setting> getSettingsByMod(String name) {
        return this.sets.stream().filter(set -> set.parent.getName().equalsIgnoreCase(name)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Setting> getSettingsByMod(Class<? extends Module> clazz) {
        return this.sets.stream().filter(set -> set.parent.getClass() == clazz).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<SubSetting> getSubSettingsBySetting(String name) {
        return this.sets.stream().filter(set -> set instanceof SubSetting && ((SubSetting) set).getParent().name.equalsIgnoreCase(name)).map(set -> (SubSetting) set).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<SubSetting> getSubSettingsBySetting(SettingParent set) {
        return this.sets.stream().filter(set0 -> set0 instanceof SubSetting && ((SubSetting) set0).getParent() == set).map(set0 -> (SubSetting) set0).collect(Collectors.toCollection(ArrayList::new));
    }
}
