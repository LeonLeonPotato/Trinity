package me.leon.trinity.managers;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.*;
import me.leon.trinity.hacks.combat.*;
import me.leon.trinity.hacks.exploits.*;
import me.leon.trinity.hacks.misc.*;
import me.leon.trinity.hacks.movement.*;
import me.leon.trinity.hacks.player.*;
import me.leon.trinity.hacks.preset.PresetManager;
import me.leon.trinity.hacks.render.*;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.hacks.render.NoRender;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ModuleManager {
    public static ArrayList<Module> modules;

    public ModuleManager() {
        modules = new ArrayList<>();

        // Preset
        addMod(new PresetManager());

        // Client
        addMod(new Font());
        addMod(new HUD());
        addMod(new HUDeditor());
        addMod(new ClickGUI());
        addMod(new ClientColor());

        // Combat
        addMod(new KillAura());
        addMod(new AutoCrystal());
        addMod(new Surround());
        addMod(new AutoTrap());
        addMod(new InstantBurrow());
        addMod(new Offhand());

        // Misc
        addMod(new NoRotate());
        addMod(new DiscordRPC());
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
        addMod(new NoSlow());
        addMod(new Parkour());
        addMod(new ReverseStep());
        addMod(new Step());
        addMod(new Speed());
        addMod(new Yaw());

        // Render
        addMod(new FullBright());
        addMod(new FreeLook());
        addMod(new NoRender());
        addMod(new HoleESP());

        // Exploits
        addMod(new PacketFly());
        addMod(new FastUse());
        addMod(new Reach());
        addMod(new EntityMine());

        // Player
        addMod(new InventoryMove());
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

        modules.add(mod);
    }

    public static Module getMod(String name) {
        return modules.stream().filter(mod0 -> mod0.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static Module getMod(String name, Category cat) {
        return modules.stream().filter(mod0 -> mod0.getName().equalsIgnoreCase(name) && mod0.getCategory() == cat).findFirst().orElse(null);
    }

    public static Module getMod(Class<? extends Module> clazz) {
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
