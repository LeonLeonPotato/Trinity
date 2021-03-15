package me.leon.trinity.config;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.components.Frame;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.settings.*;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.sub.SubBoolean;
import me.leon.trinity.setting.settings.sub.SubColor;
import me.leon.trinity.setting.settings.sub.SubMode;
import me.leon.trinity.setting.settings.sub.SubSlider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class loadConfig {
    public static class LoadConfig {
        public static File presetDir = new File(System.getProperty("user.home") + "/Documents/Trinity/essentials");

        public static void loadModules(boolean preset) {
            try {
                for(Module s : Trinity.moduleManager.modules) {
                    if(s.isEnabled()) if(preset) s.setEnabled(false);
                }

                Scanner scanner = new Scanner(Trinity.curPreset.enabledModules);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if(Trinity.moduleManager.getMod(line) == null) {
                        continue;
                    }
                    Trinity.moduleManager.getMod(line).setEnabled(true);
                }
            } catch (Exception e) {System.out.println("could not find file to load enabled modules from! \n error: \n "); e.printStackTrace();}
        }

        public static void loadBinds() {

            try {
                Scanner scanner = new Scanner(Trinity.curPreset.binds);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] string = line.split(":");
                    if(Trinity.moduleManager.getMod(string[0]) == null) {
                        continue;
                    }
                    Trinity.moduleManager.getMod(string[0]).setKey((Integer.parseInt(string[1])));
                }

            } catch (Exception e) {Trinity.LOGGER.info("could not find file to load module binds from! \n error code: \n"); e.printStackTrace();}
        }

        public static void loadSettings() {
            try {
                Scanner scanner = new Scanner(Trinity.curPreset.settings);
                while(scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] setting = line.split(":");
                    Trinity.settingManager.sets.forEach(set -> {
                        if(set.parent.getName().equals(setting[0])) {
                            if(set instanceof Mode && set.name.equals(setting[1])) {
                                ((Mode) set).setValue(setting[2]);
                            }
                            if(set instanceof Slider && set.name.equals(setting[1])) {
                                ((Slider) set).setValue(Double.parseDouble(setting[2]));
                            }
                            if(set instanceof Boolean && set.name.equals(setting[1])) {
                                ((Boolean) set).setEnabled(java.lang.Boolean.parseBoolean(setting[2]));
                            }
                            if(set instanceof StringInput && set.name.equals(setting[1])) {
                                ((StringInput) set).val = setting[2];
                            }
                            if(set instanceof Color && set.name.equals(setting[1])) {
                                ((Color) set).r = (Integer.parseInt(setting[2]));
                                ((Color) set).g = (Integer.parseInt(setting[3]));
                                ((Color) set).b = (Integer.parseInt(setting[4]));
                                ((Color) set).a = (Integer.parseInt(setting[5]));
                                ((Color) set).rainbow  = java.lang.Boolean.parseBoolean(setting[6]);
                            }

                            // SubSettings
                            if(set instanceof SubMode && set.name.equals(setting[1]) && set.name.equals(setting[2])) {
                                ((SubMode) set).setValue(setting[3]);
                            }
                            if(set instanceof SubSlider && set.name.equals(setting[1]) && set.name.equals(setting[2])) {
                                ((SubSlider) set).setValue(Double.parseDouble(setting[3]));
                            }
                            if(set instanceof SubBoolean && set.name.equals(setting[1]) && set.name.equals(setting[2])) {
                                ((SubBoolean) set).setEnabled(java.lang.Boolean.parseBoolean(setting[3]));
                            }
                            if(set instanceof SubColor && set.name.equals(setting[1]) && set.name.equals(setting[2])) {
                                ((SubColor) set).r = (Integer.parseInt(setting[3]));
                                ((SubColor) set).g = (Integer.parseInt(setting[4]));
                                ((SubColor) set).b = (Integer.parseInt(setting[5]));
                                ((SubColor) set).a = (Integer.parseInt(setting[6]));
                                ((SubColor) set).rainbow  = java.lang.Boolean.parseBoolean(setting[7]);
                            }
                        }
                    });
                }
            } catch (Exception e) {Trinity.LOGGER.info("could not find file to load module settings from! \n" + "error code: \n" + e);}
        }

        public static void loadSearch() {
            /*try {
                Search.blocks.clear();
                Scanner scanner = new Scanner(Trinity.curPreset.search);
                while(scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] setting = line.split(":");

                    Search.blocks.add(Block.getBlockFromName(setting[1]));
                }
            } catch (Exception e) {Trinity.LOGGER.info("could not find file to load Search settings from! \n" + "error code: \n" + e);
             */
        }

        public static void loadHud() {
            /*try {
                Scanner scanner = new Scanner(Trinity.curPreset.HUD);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] hud = line.split(":");

                    component c = componentManager.getCompByName(hud[0]);
                    c.setX(Float.parseFloat(hud[1]));
                    c.setY(Float.parseFloat(hud[2]));
                    c.setEnabled(Boolean.parseBoolean(hud[3]));
                }

            } catch (Exception e) {e.printStackTrace();}

             */
        }

        public static void loadFriends() {
            /*try {
                ModuleManager.friends.friends.clear();
                Scanner scanner = new Scanner(Trinity.curPreset.Friends);
                while (scanner.hasNextLine()) {
                    ModuleManager.friends.friends.add(scanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

             */
        }

        public static void loadGUI() {
            try {
                Scanner scanner = new Scanner(Trinity.curPreset.GUI);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] gui = line.split(":");

                    Frame frame = ClickGui.getFrameFromCategory(Category.valueOf(gui[0]).name());

                    if(frame != null) {
                        frame.x = (Integer.parseInt(gui[1]));
                        frame.y = (Integer.parseInt(gui[2]));
                        frame.open = (java.lang.Boolean.parseBoolean(gui[3]));
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public static void loadPreset() {
            try {
                Scanner scanner = new Scanner(presetDir);
                while(scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    if(Trinity.presetManager.getPreset(line) != null) {
                        Trinity.curPreset = Trinity.presetManager.getPreset(line);
                    } else {
                        Trinity.curPreset = new Preset(new File(System.getProperty("user.home") + "/Documents/Trinity/Default"));
                        Trinity.presetManager.presets.add(Trinity.curPreset);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

