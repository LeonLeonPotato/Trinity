package me.leon.trinity.managers;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.*;
import me.leon.trinity.hacks.combat.*;
import me.leon.trinity.hacks.misc.*;
import me.leon.trinity.hacks.exploits.*;
import me.leon.trinity.hacks.movement.*;
import me.leon.trinity.hacks.render.*;
import me.leon.trinity.hacks.player.*;
import me.leon.trinity.utils.client.SettingUtil;

import static me.leon.trinity.utils.client.SettingUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
	public static ArrayList<Module> modules;

	public ModuleManager() {
		modules = new ArrayList<>();

		list = modules;
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
		addMod(new Burrow());
		addMod(new Offhand());
		addMod(new HoleFill());
		addMod(new AutoMend());
		addMod(new AutoArmor());
		addMod(new Quiver());

		// Misc
		addMod(new NoRotate());
		addMod(new DiscordRPC());
		addMod(new FreeCam());
		addMod(new ChatSuffix());
		addMod(new FakePlayer());
		addMod(new Timer());
		addMod(new TotempopCounter());

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
		addMod(new Chams());
		addMod(new ESP());
		addMod(new Tracers());

		// Exploits
		addMod(new PacketFly());
		addMod(new Reach());
		addMod(new EntityMine());
		addMod(new PacketLogger());

		// Player
		addMod(new InventoryMove());
		addMod(new FastUse());
		addMod(new NoPush());
		addMod(new SpeedMine());

		list = null;
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

	public static List<Module> getMods(Category c) {
		return modules.stream().filter(mod -> mod.getCategory() == c).collect(Collectors.toList());
	}
}
