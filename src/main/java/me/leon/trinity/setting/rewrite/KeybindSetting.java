package me.leon.trinity.setting.rewrite;

import net.minecraft.client.Minecraft;
import org.json.simple.JSONObject;
import org.lwjgl.input.Keyboard;

import java.util.Set;
import java.util.function.Predicate;

public class KeybindSetting extends Setting {
    private int keycode;

    public KeybindSetting(String name, int keycode) {
        this(name, null, keycode, s -> true);
    }

    public KeybindSetting(String name, Object parent, int keycode) {
        this(name, parent, keycode, s -> true);
    }

    public KeybindSetting(String name, int keycode, Predicate<Setting> pre) {
        this(name, null, keycode, pre);
    }

    public KeybindSetting(String name, Object parent, int keycode, Predicate<Setting> pre) {
        super(name, parent, pre);
        this.keycode = keycode;
    }

    public int getKeycode() {
        return keycode;
    }

    public void setKeycode(int keycode) {
        this.keycode = keycode;
    }

    public char getChar() {
        return (char) keycode;
    }

    public boolean down() {
        return down(true);
    }

    public boolean down(boolean guis) {
        if(guis && Minecraft.getMinecraft().currentScreen != null) return false;
        return Keyboard.isKeyDown(keycode);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getJsonString() {
        if(getSubSettings().isEmpty()) {
            return keycode;
        } else {
            JSONObject s = new JSONObject();
            s.put("value", keycode);
            JSONObject a = new JSONObject();
            for(Setting l : getSubSettings()) {
                a.put(l.getName(), l.getJsonString());
            }
            s.put("settings", a);
            return s;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void parseJson(JSONObject main, String key, Object val) {
        if(val instanceof JSONObject) { // jesus christ this took me time idk why
            final JSONObject j = (JSONObject) val;

            this.keycode = ((Long) j.get("value")).intValue();
            JSONObject sets = (JSONObject) j.get("settings");

            for(String k : (Set<String>) sets.keySet()) {
                final Object v = sets.get(k);
                getSubSettings().forEach(e -> {
                    if(e.getName().equals(k)) {
                        e.parseJson(main, k, v);
                    }
                });
            }
        } else {
            this.keycode = ((Long) val).intValue();
        }
    }
}
