package me.leon.trinity.setting.rewrite;

import com.google.gson.JsonObject;
import org.json.simple.JSONObject;

import java.util.Set;

public class TextBoxSetting extends Setting {
    private String value;

    public TextBoxSetting(String name, String value) {
        this(name, null, value);
    }

    public TextBoxSetting(String name, Object parent, String value) {
        super(name, parent);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
