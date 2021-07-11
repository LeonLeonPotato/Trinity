package me.leon.trinity.managers;

import me.leon.trinity.hacks.Module;
import me.leon.trinity.hud.Component;
import me.leon.trinity.setting.rewrite.Setting;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SettingManager {
	public static ArrayList<Setting> sets;
	public static ArrayList<Setting> hudSets;

	public SettingManager() {
		sets = new ArrayList<>();
		hudSets = new ArrayList<>();
	}

	public static void addSets(Setting set) {
		hudSets.add(set);
	}

	public static Setting getSetting(String name) {
		return sets.stream().filter(set -> set.getName().equals(name)).findFirst().orElse(null);
	}

	public static Setting getSetting(Class<?> clazz) {
		return sets.stream().filter(set -> set.getClass() == clazz).findFirst().orElse(null);
	}
	
	public static Setting getSetting(Object set) {
		return sets.stream().filter(s -> s == set).findFirst().orElse(null);
	}

	public static ArrayList<Setting> getSettings(String name) {
		if(getSetting(name).getParent() instanceof Module) {
			return (ArrayList<Setting>) sets.stream().filter(set -> ((Module) set.getParent()).getName().equals(name)).collect(Collectors.toList());
		} else
		if(getSetting(name).getParent() instanceof Setting) {
			return (ArrayList<Setting>) sets.stream().filter(set -> ((Setting) set.getParent()).getName().equals(name)).collect(Collectors.toList());
		}
		return null;
	}

	public static ArrayList<Setting> getSettings(Class<?> clazz) {
		return (ArrayList<Setting>) sets.stream().filter(set -> set.getParent().getClass() == clazz).collect(Collectors.toList());
	}

	public static ArrayList<Setting> getSettings(Object obj) {
		return (ArrayList<Setting>) sets.stream().filter(s -> s.getParent() == obj).collect(Collectors.toList());
	}

	public static ArrayList<Setting> getAllSuperSettings(Class<?> clazz) {
		return (ArrayList<Setting>) sets.stream().filter(set -> set.getSuperParent().getClass() == clazz).collect(Collectors.toList());
	}

	public static ArrayList<Setting> getAllSuperSettings(Object obj) {
		return (ArrayList<Setting>) sets.stream().filter(s -> s.getSuperParent() == obj).collect(Collectors.toList());
	}

	public static ArrayList<Setting> getAllSuperSettings(String name) {
		if(getSetting(name).getParent() instanceof Module) {
			return (ArrayList<Setting>) sets.stream().filter(set -> ((Module) set.getSuperParent()).getName().equals(name)).collect(Collectors.toList());
		} else
		if(getSetting(name).getParent() instanceof Setting) {
			return (ArrayList<Setting>) sets.stream().filter(set -> ((Setting) set.getSuperParent()).getName().equals(name)).collect(Collectors.toList());
		}
		return null;
	}


	// hud settings below

	public static void addSetsHud(Setting set) {
		hudSets.add(set);
	}

	public static Setting getHudSetting(String name) {
		return hudSets.stream().filter(set -> set.getName().equals(name)).findFirst().orElse(null);
	}

	public static Setting getHudSetting(Class<? extends Setting> clazz) {
		return hudSets.stream().filter(set -> set.getClass() == clazz).findFirst().orElse(null);
	}

	public static Setting getHudSetting(Setting set) {
		return hudSets.stream().filter(s -> s == set).findFirst().orElse(null);
	}
}
