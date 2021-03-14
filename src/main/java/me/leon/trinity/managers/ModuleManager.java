package me.leon.trinity.managers;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.hacks.client.Font;
import me.leon.trinity.setting.Setting;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ModuleManager {
    public ArrayList<Module> modules;

    public ModuleManager() {
        this.modules = new ArrayList<>();

        // Client
        addMod(new Font());
        addMod(new ClickGUI());
    }

    private void addMod(Module mod) {
        try {
            for(Field field : mod.getClass().getDeclaredFields()) {
                if (Setting.class.isAssignableFrom(field.getType()))
                {
                    if (!field.isAccessible())
                    {
                        field.setAccessible(true);
                    }
                    final Setting val = (Setting) field.get(mod);
                    val.parent = mod;
                    mod.addSetting(val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.modules.add(mod);
    }

    public Module getMod(String name) {
        for(Module mod0 : modules) {
            if(mod0.getName().equalsIgnoreCase(name)) {
                return mod0;
            }
        }
        return null;
    }

    public Module getMod(String name, Category cat) {
        for(Module mod0 : modules) {
            if(mod0.getName().equalsIgnoreCase(name) && mod0.getCategory() == cat) {
                return mod0;
            }
        }
        return null;
    }

    public Module getMod(Class<? extends Module> clazz) {
        for(Module mod0 : modules) {
            if(mod0.getClass() == clazz) {
                return mod0;
            }
        }
        return null;
    }

    public ArrayList<Module> getModulesByCategory(Category cat) {
        ArrayList<Module> mods = new ArrayList<>();
        for(Module mod : modules) {
            if(mod.getCategory() == cat) {
                mods.add(mod);
            }
        }
        return mods;
    }
}
