package me.leon.trinity.utils.client;

import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.Setting;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "unchecked"}) // lololol begone warnings
public class SettingUtil {
    public static ArrayList list;

    public static <T> void addMod(T mod) {
        try {
            for (Field field : mod.getClass().getDeclaredFields()) {
                if (Setting.class.isAssignableFrom(field.getType())) {
                    if (!field.isAccessible()) field.setAccessible(true);

                    final Setting val = (Setting) field.get(mod);
                    val.setSuperParent(mod);

                    if(val.getParent() == null) {
                        val.setParent(mod);

                    } else {
                        ((Setting) val.getParent()).addSetting(val);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        list.add(mod);
    }

    public static <T> void addMod(T mod, ArrayList<T> arrayList) {
        try {
            for (Field field : mod.getClass().getDeclaredFields()) {
                if (Setting.class.isAssignableFrom(field.getType())) {
                    if (!field.isAccessible()) field.setAccessible(true);

                    final Setting val = (Setting) field.get(mod);
                    val.setSuperParent(mod);

                    if(val.getParent() == null) {
                        val.setParent(mod);

                    } else {
                        ((Setting) val.getParent()).addSetting(val);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        arrayList.add(mod);
    }
}
