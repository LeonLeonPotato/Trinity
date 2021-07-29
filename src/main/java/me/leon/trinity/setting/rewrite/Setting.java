package me.leon.trinity.setting.rewrite;

import me.leon.trinity.hacks.Module;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public abstract class Setting {
    private Object parent;
    private Object superParent;
    private final String name;
    private final ArrayList<Setting> settings;

    public abstract Object getJsonString();
    public abstract void parseJson(JSONObject main, String key, Object val);

    public Setting(String name) {
        this.name = name;
        this.parent = null;
        this.settings = new ArrayList<>();
    }

    public Setting(String name, Object parent) {
        this.name = name;
        this.parent = parent;
        this.settings = new ArrayList<>();
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public Object getSuperParent() {
        return superParent;
    }

    public void setSuperParent(Object superParent) {
        this.superParent = superParent;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Setting> getSubSettings() {
        return settings;
    }

    public void addSetting(Setting setting) {
        settings.add(setting);
    }
}
