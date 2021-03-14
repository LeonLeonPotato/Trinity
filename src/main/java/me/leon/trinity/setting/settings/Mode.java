package me.leon.trinity.setting.settings;

import me.leon.trinity.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;

public class Mode extends Setting {
    private String val;
    private ArrayList<String> values;

    public Mode(String name, String val, String... values) {
        super(name);
        this.val = val;
        this.values = new ArrayList<>(Arrays.asList(values));
    }

    public String getValue() {
        return val;
    }

    public void setValue(String val) {
        this.val = val;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }
}
