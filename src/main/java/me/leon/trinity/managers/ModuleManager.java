package me.leon.trinity.managers;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.*;
import me.leon.trinity.hacks.combat.*;
import me.leon.trinity.hacks.exploits.*;
import me.leon.trinity.hacks.misc.*;
import me.leon.trinity.hacks.movement.*;
import me.leon.trinity.hacks.render.*;
import me.leon.trinity.setting.Setting;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ModuleManager {
    public ArrayList<Module> modules;

    public ModuleManager() {
        this.modules = new ArrayList<>();

        // Client
        addMod(new Font());
        addMod(new ClickGUI());
        addMod(new ClientColor());

        // Combat
        addMod(new KillAura());
        addMod(new AutoCrystal());

        // Misc
        addMod(new NoRotate());
        addMod(new FreeCam());
        addMod(new ChatSuffix());
        addMod(new FakePlayer());
        addMod(new Timer());
        // Movement
        addMod(new Velocity());
        addMod(new AutoSprint());
        addMod(new AutoWalk());
        addMod(new IceSpeed());
        addMod(new NoVoid());
        addMod(new Parkour());
        addMod(new ReverseStep());
        addMod(new Yaw());

        // Render
        addMod(new FullBright());
        addMod(new FreeLook());
        // Exploits
        addMod(new FastEXP());
        addMod(new Reach());
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
        return modules.stream().filter(mod0 -> mod0.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Module getMod(String name, Category cat) {
        return modules.stream().filter(mod0 -> mod0.getName().equalsIgnoreCase(name) && mod0.getCategory() == cat).findFirst().orElse(null);
    }

    public Module getMod(Class<? extends Module> clazz) {
        return modules.stream().filter(mod0 -> mod0.getClass() == clazz).findFirst().orElse(null);
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
