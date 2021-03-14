package me.leon.trinity.setting.settings.sub;

import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.SubSetting;

import java.util.ArrayList;
import java.util.Arrays;

public class SubMode extends SubSetting {
    private String val;
    private ArrayList<String> values;

    public SubMode(String name, Setting parent, String val, String... values) {
        super(name, parent);
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
