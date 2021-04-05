package me.leon.trinity.config.rewrite;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.Frame;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.*;
import me.leon.trinity.setting.settings.Boolean;
import me.leon.trinity.setting.settings.sub.*;
import me.leon.trinity.utils.misc.FileUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.PrintWriter;

public class SaveConfig extends Thread {
    public final static File mainDir = new File("Trinity");

    @Override
    public void run() {
        runStatic();
    }

    public static void runStatic() {
        try {
            savePreset();
            saveModules();
            saveHUD();
            saveGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveModules() {
        for(Module mod : ModuleManager.modules) {
            try {
                PrintWriter writer = FileUtils.writer(Trinity.currentPreset.getModsFile(), mod.getName() + ".json");
                JSONObject object = new JSONObject();
                object.put("enabled", mod.isEnabled());
                object.put("visible", mod.visible);
                object.put("bind", mod.getKey());

                JSONObject settings = new JSONObject();

                for(Setting set : Trinity.settingManager.getSettingsByMod(mod.getClass())) {
                    if(set instanceof Slider) {
                        settings.put(set.name, ((Slider) set).getValue());
                    }
                    if(set instanceof Mode) {
                        settings.put(set.name, ((Mode) set).getValue());
                    }
                    if(set instanceof StringInput) {
                        settings.put(set.name, ((StringInput) set).val);
                    }
                    if(set instanceof KeyBinding) {
                        settings.put(set.name, ((KeyBinding) set).Char);
                    }
                    if(set instanceof Color) {
                        JSONObject colorSets = new JSONObject();
                        colorSets.put("red", ((Color) set).r);
                        colorSets.put("green", ((Color) set).g);
                        colorSets.put("blue", ((Color) set).b);
                        colorSets.put("alpha", ((Color) set).a);
                        colorSets.put("rainbow", ((Color) set).rainbow);
                        colorSets.put("speed", ((Color) set).speed);
                        colorSets.put("sync", ((Color) set).sync);

                        settings.put(set.name, colorSets);
                    }
                    if(set instanceof Boolean) {
                        settings.put(set.name, ((Boolean) set).getValue());
                    }

                    if(set instanceof SettingParent) {
                        JSONObject sParentSets = new JSONObject();
                        JSONObject subSets = new JSONObject();
                        sParentSets.put("enabled", ((SettingParent) set).getValue());
                        for(SubSetting set0 : Trinity.settingManager.getSubSettingsBySetting((SettingParent) set)) {
                            if(set0 instanceof SubSlider) {
                                subSets.put(set0.name, ((SubSlider) set0).getValue());
                            }
                            if(set0 instanceof SubMode) {
                                subSets.put(set0.name, ((SubMode) set0).getValue());
                            }
                            if(set0 instanceof SubColor) {
                                JSONObject subColor = new JSONObject();
                                subColor.put("red", ((SubColor) set0).r);
                                subColor.put("green", ((SubColor) set0).g);
                                subColor.put("blue", ((SubColor) set0).b);
                                subColor.put("alpha", ((SubColor) set0).a);
                                subColor.put("rainbow", ((SubColor) set0).rainbow);
                                subColor.put("speed", ((SubColor) set0).speed);
                                subColor.put("sync", ((SubColor) set0).sync);
                                subSets.put(set0.name, subColor);
                            }
                            if(set0 instanceof SubBoolean) {
                                subSets.put(set0.name, ((SubBoolean) set0).getValue());
                            }
                            if(set0 instanceof SubKeyBinding) {
                                subSets.put(set0.name, ((SubKeyBinding) set0).Char);
                            }
                        }
                        sParentSets.put("settings", subSets);
                        settings.put(set.name, sParentSets);
                    }
                }

                object.put("settings", settings);
                writer.write(FileUtils.prettyPrint(object.toString()));
                writer.close();
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public static void saveGUI() {
        for(Frame c : ClickGui.frames) {
            try {
                PrintWriter writer = FileUtils.writer(Trinity.currentPreset.getGuiFile(), c.c.name() + ".json");
                JSONObject object = new JSONObject();
                object.put("x", c.x);
                object.put("y", c.y);
                object.put("open", c.open);
                writer.write(FileUtils.prettyPrint(object.toJSONString()));
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveHUD() {
        for(me.leon.trinity.hud.Component c : Trinity.hudManager.comps) {
            try {
                PrintWriter writer = FileUtils.writer(Trinity.currentPreset.getHudFile(), c.name + ".json");
                JSONObject object = new JSONObject();
                object.put("x", c.x);
                object.put("y", c.y);
                object.put("anchor", c.anchorPoint == null ? null : c.anchorPoint.name());
                object.put("visible", c.visible);
                writer.write(FileUtils.prettyPrint(object.toJSONString()));
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void savePreset() {
        try {
            PrintWriter writer = FileUtils.writer("Trinity", "Main.txt");
            writer.write(Trinity.currentPreset.name + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
