package me.leon.trinity.managers;

import baritone.api.BaritoneAPI;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.Setting;
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
}
