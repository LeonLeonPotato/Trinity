package me.leon.trinity.setting.rewrite;

import org.json.simple.JSONObject;

import java.util.Set;
import java.util.function.Predicate;

public class SliderSetting extends Setting {
    private double value;
    private final double min, max;
    private final boolean onlyInt;

    public SliderSetting(String name, double min, double value, double max, boolean onlyInt) {
        this(name, null, min, value, max, onlyInt, s -> true);
    }

    public SliderSetting(String name, Object parent, double min, double value, double max, boolean onlyInt) {
        this(name, parent, min, value, max, onlyInt, s -> true);
    }

    public SliderSetting(String name, double min, double value, double max, boolean onlyInt, Predicate<Setting> pre) {
        this(name, null, min, value, max, onlyInt, pre);
    }

    public SliderSetting(String name, Object parent, double min, double value, double max, boolean onlyInt, Predicate<Setting> pre) {
        super(name, parent, pre);
        this.value = value;
        this.min = min;
        this.max = max;
        this.onlyInt = onlyInt;
    }

    public double getValue() {
        return value;
    }

    public float floatValue() {
        return (float) value;
    }

    public int intValue() {
        return (int) value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public boolean isOnlyInt() {
        return onlyInt;
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

            this.value = ((Long) j.get("value")).intValue();
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
            this.value = ((Double) val).intValue();
        }
    }
}
