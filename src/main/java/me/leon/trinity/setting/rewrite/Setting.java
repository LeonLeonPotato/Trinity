package me.leon.trinity.setting.rewrite;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.function.Predicate;

public abstract class Setting {
    private Object parent;
    private Object superParent;
    private final String name;
    private final ArrayList<Setting> settings;

    protected final Predicate<Setting> DEFAULT_PREDICATE = s -> true;
    protected final Predicate<Setting> predicate;

    public abstract Object getJsonString();
    public abstract void parseJson(JSONObject main, String key, Object val);

    public Setting(String name) {
        this.name = name;
        this.parent = null;
        this.settings = new ArrayList<>();
        this.predicate = DEFAULT_PREDICATE;
    }

    public Setting(String name, Object parent) {
        this.name = name;
        this.parent = parent;
        this.settings = new ArrayList<>();
        this.predicate = DEFAULT_PREDICATE;
    }

    public Setting(String name, Object parent, Predicate<Setting> predicate) {
        this.name = name;
        this.parent = parent;
        this.settings = new ArrayList<>();
        this.predicate = predicate;
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

    public Predicate<Setting> getPredicate() {
        return predicate;
    }

    public boolean test() {
        return predicate.test(this);
    }
}
