package me.leon.trinity.config;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.components.Frame;
import me.leon.trinity.hud.Component;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.*;
import me.leon.trinity.setting.settings.sub.SubBoolean;
import me.leon.trinity.setting.settings.sub.SubColor;
import me.leon.trinity.setting.settings.sub.SubMode;
import me.leon.trinity.setting.settings.sub.SubSlider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class saveConfig extends Thread {
    public static File confDir = new File("Trinity");
    public static File defaultDir = new File("Trinity/Default");
    public static File presetDir = new File("Trinity/essentials");

    @Override
    public void run() {
        if (!confDir.exists()) confDir.mkdirs();
        savePreset();
        saveModules();
        saveBinds();
        saveSettings();
        saveSearch();
        saveHud();
        saveFriends();
        saveGui();
    }

    public static void saveModules() {
        //if(panic.paniked) return;
        ArrayList<String> enabledMods = new ArrayList<>();

        ModuleManager.modules.forEach(mod -> {
            if(mod.isEnabled() && mod.shouldSave()) {
                enabledMods.add(mod.getName());
            }
        });

        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(Trinity.curPreset.enabledModules, false), true);
            for(String m : enabledMods) {
                writer.write(m + "\n");
            }
            writer.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void saveBinds() {
        ArrayList<String> binds = new ArrayList<>();

        ModuleManager.modules.forEach(mod -> {
           binds.add(mod.getName() + ":" + mod.getKey());
        });

        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(Trinity.curPreset.binds, false), true);
            for(String b: binds) {
                writer.write(b + "\n");
            }
            writer.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void saveSettings() {
        ArrayList<String> settings = new ArrayList<>();
        ModuleManager.modules.forEach(mod -> {
            Trinity.settingManager.sets.forEach(set -> {
                if(set.parent == mod) {
                    if(set instanceof Mode) {
                        settings.add(mod.getName() + ":" + set.name + ":" + ((Mode) set).getValue());
                    }
                    if(set instanceof Boolean) {
                        settings.add(mod.getName() + ":" + set.name + ":" + ((Boolean) set).getValue());
                    }
                    if(set instanceof Slider) {
                        settings.add(mod.getName() + ":" + set.name + ":" + ((Slider) set).getValue());
                    }
                    if(set instanceof StringInput) {
                        settings.add(mod.getName() + ":" + set.name + ":" + ((StringInput) set).val);
                    }
                    if(set instanceof Color) {
                        settings.add(mod.getName() + ":" + set.name + ":" + ((Color) set).r + ":" + ((Color) set).g + ":" + ((Color) set).b + ":" + ((Color) set).a + ":" + ((Color) set).rainbow);
                    }

                    // SubSettings
                    if(set instanceof SettingParent) {
                        settings.add(mod.getName() + ":" + set.name + ":" + ((SettingParent) set).getValue());
                    }
                    if(set instanceof SubMode) {
                        settings.add(mod.getName() + ":" + ((SubMode) set).getParent().name + ":" + set.name + ":" + ((SubMode) set).getValue());
                    }
                    if(set instanceof SubBoolean) {
                        settings.add(mod.getName() + ":" + ((SubBoolean) set).getParent().name + ":" + set.name + ":" + ((SubBoolean) set).getValue());
                    }
                    if(set instanceof SubSlider) {
                        settings.add(mod.getName() + ":" + ((SubSlider) set).getParent().name+ ":" + set.name + ":" + ((SubSlider) set).getValue());
                    }
                    if(set instanceof SubColor) {
                        SubColor set0 = ((SubColor) set);
                        settings.add(mod.getName() + ":" + ((SubColor) set).getParent().name + ":" + set.name + ":" + set0.r  + ":" + set0.g + ":" + set0.b + ":" + set0.a + ":" + set0.rainbow);
                    }

                }
            });
        });

        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(Trinity.curPreset.settings, false), true);
            for (String s: settings) {
                writer.write(s + "\n");
            }
            writer.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void saveSearch() {
        /*try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(Trinity.curPreset.search, false), true);
            for (Block s : Search.blocks) {
                writer.write(s.getRegistryName() + "\n");
            }
            writer.close();
        } catch (Exception e) { Trinity.LOGGER.warn("could not save Xray settings! " + e); }

         */
    }

    public static void saveHud() {
        try {
            ArrayList<Component> comps = new ArrayList<>(Trinity.hudManager.comps);
            PrintWriter writer = new PrintWriter(new FileOutputStream(Trinity.curPreset.HUD, false), true);
            for(Component c : comps) {
                writer.write(c.name + ":" + c.x + ":" + c.y + ":" + c.visible + ":" + c.anchorPoint.name() + "\n");
            }
            writer.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void saveFriends() {
        /*try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(Trinity.curPreset.Friends, false), true);
            for(String s : ModuleManager.friends.friends) {
                writer.write(s + "\n");
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

         */
    }

    public static void saveGui() {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(Trinity.curPreset.GUI, false), true);
            for(Frame c : ClickGui.frames) {
                writer.write(c.c.toString() + ":" + c.x + ":" + c.y + ":" + c.open + "\n");
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void savePreset() {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(presetDir, false), true);
            writer.write(Trinity.curPreset.name);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}