package me.leon.trinity.setting.rewrite;

import org.json.simple.JSONObject;

import java.sql.Types;
import java.util.Set;

public class BooleanSetting extends Setting {
    private final boolean canEnable;
    private boolean value;

    public BooleanSetting(String name, boolean value, boolean canEnable) {
        this(name, null, value, canEnable);
    }

    public BooleanSetting(String name, boolean value) {
        this(name, null, value);
    }

    public BooleanSetting(String name, Object parent, boolean value) {
        this(name, parent, value, true);
    }

    public BooleanSetting(String name, Object parent, boolean value, boolean canEnable) {
        super(name, parent);
        this.value = value;
        this.canEnable = canEnable;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        if(canEnable)
            this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getJsonString() {
        if(getParent() == getSuperParent()) {
            return value;
        } else {
            JSONObject s = new JSONObject();
            s.put("value", value);
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
    public void parseJson(JSONObject main, String key, Object val) { // i really dont know
        if(val instanceof JSONObject) { // jesus christ this took me time idk why
            final JSONObject j = (JSONObject) val;

            this.value = (Boolean) j.get("value");
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
            this.value = (Boolean) val;
        }
    }
}
