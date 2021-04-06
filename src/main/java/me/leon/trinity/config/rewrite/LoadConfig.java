package me.leon.trinity.config.rewrite;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.components.Frame;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hud.AnchorPoint;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.*;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.sub.*;
import me.leon.trinity.utils.misc.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

public class LoadConfig {
    public static void load() {
        try {
            loadHUD();
            loadGUI();
            loadModules();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadModules() {
        FileUtils.makeIfDoesntExist(Trinity.currentPreset.getModsFile());
        for(File file : Trinity.currentPreset.getModsFile().listFiles()) {
            try {
                JSONParser parser = new JSONParser();
                FileReader reader = new FileReader(file);
                JSONObject obj = (JSONObject) parser.parse(reader);
                Module mod = ModuleManager.getMod(file.getName().replaceAll(".json", ""));
                ArrayList<Setting> settings = Trinity.settingManager.getSettingsByMod(mod.getClass());
                JSONObject setObj = (JSONObject) obj.get("settings");
                mod.setKey(((Long) obj.get("bind")).intValue()); // returns long for some reason
                mod.visible = ((java.lang.Boolean) obj.get("visible"));
                mod.setEnabled((java.lang.Boolean) obj.get("enabled"));
                for(Setting set : settings) {
                    for(Object o : setObj.keySet()) {
                        if(set.name.equals(o)) {
                            if(set instanceof Slider) {
                                ((Slider) set).setValue(((Double) setObj.get(o))); // all the other ones work fine but this returns double???????
                            }
                            if(set instanceof Mode) {
                                ((Mode) set).setValue((String) setObj.get(o));
                            }
                            if(set instanceof StringInput) {
                                ((StringInput) set).val = (String) setObj.get(o);
                            }
                            if(set instanceof Boolean) {
                                ((Boolean) set).setEnabled((java.lang.Boolean) setObj.get(o));
                            }
                            if(set instanceof KeyBinding) {
                                ((KeyBinding) set).Char = ((Long) setObj.get(o)).intValue();
                            }
                            if(set instanceof Color) {
                                JSONObject colorObj = (JSONObject) setObj.get(o);
                                ((Color) set).r = (((Long) colorObj.get("red")).intValue());
                                ((Color) set).g = (((Long) colorObj.get("green")).intValue());
                                ((Color) set).b = (((Long) colorObj.get("blue")).intValue());
                                ((Color) set).a = (((Long) colorObj.get("alpha")).intValue());
                                ((Color) set).sync = ((java.lang.Boolean) colorObj.get("sync"));
                                ((Color) set).speed = (((Long) colorObj.get("speed")).intValue());
                                ((Color) set).rainbow = ((java.lang.Boolean) colorObj.get("rainbow"));
                            }

                            if(set instanceof SettingParent) {
                                JSONObject sParentObj = (JSONObject) setObj.get(o);
                                JSONObject subSets = (JSONObject) sParentObj.get("settings");
                                ((SettingParent) set).setEnabled(((java.lang.Boolean) sParentObj.get("enabled")));
                                for(SubSetting set0 : Trinity.settingManager.getSubSettingsBySetting((SettingParent) set)) {
                                    for(Object i : subSets.keySet()) {
                                        if(set0.name.equals(i)) {
                                            if(set0 instanceof SubSlider) {
                                                ((SubSlider) set0).setValue((Double) subSets.get(i));
                                            }
                                            if(set0 instanceof SubBoolean) {
                                                ((SubBoolean) set0).setEnabled((java.lang.Boolean) subSets.get(i));
                                            }
                                            if(set0 instanceof SubColor) {
                                                JSONObject colorObj = (JSONObject) subSets.get(i);
                                                final SubColor subSet = (SubColor) set0;
                                                subSet.r = ((Long) colorObj.get("red")).intValue();
                                                subSet.g = ((Long) colorObj.get("green")).intValue();
                                                subSet.b = ((Long) colorObj.get("blue")).intValue();
                                                subSet.a = ((Long) colorObj.get("alpha")).intValue();
                                                subSet.rainbow = ((java.lang.Boolean) colorObj.get("rainbow"));
                                                subSet.speed = ((Long) colorObj.get("speed")).intValue();
                                                subSet.sync = ((java.lang.Boolean) colorObj.get("sync"));
                                            }
                                            if(set0 instanceof SubSlider) {
                                                ((SubSlider) set0).setValue((Double) subSets.get(i));
                                            }
                                            if(set0 instanceof SubMode) {
                                                ((SubMode) set0).setValue((String) subSets.get(i));
                                            }
                                            if(set0 instanceof SubKeyBinding) {
                                                ((SubKeyBinding) set0).Char = ((Long) subSets.get(i)).intValue();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadGUI() {
        FileUtils.makeIfDoesntExist(Trinity.currentPreset.getGuiFile());
        for(File file : Trinity.currentPreset.getGuiFile().listFiles()) {
            try {
                Frame c = ClickGui.getFrameFromCategory(file.getName().replaceAll(".json", ""));
                JSONParser parser = new JSONParser();
                FileReader reader = new FileReader(file);
                JSONObject obj = (JSONObject) parser.parse(reader);
                c.x = ((Long) obj.get("x")).intValue();
                c.y = ((Long) obj.get("y")).intValue();
                c.open = (boolean) obj.get("open");
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadHUD() {
        FileUtils.makeIfDoesntExist(Trinity.currentPreset.getHudFile());
        for(File file : Trinity.currentPreset.getHudFile().listFiles()) {
            try {
                me.leon.trinity.hud.Component c = Trinity.hudManager.getComponentByName(file.getName().replaceAll(".json", ""));
                JSONParser parser = new JSONParser();
                FileReader reader = new FileReader(file);
                JSONObject obj = (JSONObject) parser.parse(reader);
                c.x = ((Double) obj.get("x")).floatValue();
                c.y = ((Double) obj.get("y")).floatValue();
                c.visible = (boolean) obj.get("visible");
                String anchor = (String) obj.get("anchor");
                c.anchorPoint = anchor == null ? null : AnchorPoint.valueOf(anchor);
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadPreset() {
        FileUtils.makeIfDoesntExist("Trinity/");
        final File main = new File("Trinity/Main.txt");
        if(!main.exists()) {
            PrintWriter writer = FileUtils.writer("Trinity", "Main.txt");
            writer.write("None");
            writer.close();
        }
        try {
            Scanner scanner = new Scanner(new File("Trinity/Main.txt"));
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                final PresetObj obj = PresetManager.find(line);
                if(obj == null) {
                    PresetObj obj1 = new PresetObj("Default");
                    Trinity.currentPreset = obj1;
                    PresetManager.presets.add(obj1);
                    ClickGui.getFrameFromCategory("PRESETS").refresh();
                    SaveConfig.runStatic();
                } else {
                    Trinity.currentPreset = obj;
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
