package me.leon.trinity.setting.settings;

import me.leon.trinity.setting.Setting;

public class Mode extends Setting {
    private String val;
    private String[] values;

    public Mode(String name, String val, String... values) {
        super(name);
        this.val = val;
        this.values = values;
    }

    public String getValue() {
        return val;
    }

    public void setValue(String val) {
        this.val = val;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }
}
