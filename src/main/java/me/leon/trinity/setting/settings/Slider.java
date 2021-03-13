package me.leon.trinity.setting.settings;

import me.leon.trinity.setting.Setting;

public class Slider extends Setting {
    private double min;
    private double val;
    private double max;
    private boolean onlyInt;

    public Slider(String name, double min, double val, double max, boolean onlyInt) {
        super(name);
        this.min = min;
        this.val = val;
        this.max = max;
        this.onlyInt = onlyInt;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getValue() {
        return val;
    }

    public void setValue(double val) {
        this.val = val;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public boolean isOnlyInt() {
        return onlyInt;
    }

    public void setOnlyInt(boolean onlyInt) {
        this.onlyInt = onlyInt;
    }
}
