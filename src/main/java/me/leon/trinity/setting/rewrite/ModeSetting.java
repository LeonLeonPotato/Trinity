package me.leon.trinity.setting.rewrite;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class ModeSetting extends Setting {
    private ArrayList<String> values;
    private String value;

    public ModeSetting(String name, String value, String... values) {
        this(name, null, value, values);
    }

    public ModeSetting(String name, Object parent, String value, String... values) {
        super(name, parent);
        this.value = value;
        Collections.addAll((this.values = new ArrayList<>()), values);
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean is(String... strings) {
        for(String s : strings) {
            if(value.equals(s)) return true;
        }
        return false;
    }

    public boolean equalsIgnoreCase(String... strings) {
        for(String s : strings) {
            if(value.equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getJsonString() {
        if(getSubSettings().isEmpty()) {
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

            this.value = (String) j.get("value");
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
            this.value = (String) val;
        }
    }
}
