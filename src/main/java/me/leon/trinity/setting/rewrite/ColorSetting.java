package me.leon.trinity.setting.rewrite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.leon.trinity.hacks.client.ClientColor;
import me.leon.trinity.utils.rendering.Rainbow;
import net.minecraft.client.Minecraft;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.Set;

public class ColorSetting extends Setting {
    private int r, g, b, a;
    public double speed;
    public boolean sync;
    public boolean rainbow;
    public float br, s;

    public ColorSetting(String name, int r, int g, int b, int a, boolean rainbow) {
        this(name, null, r, g, b, a, rainbow);
    }

    public ColorSetting(String name, Object parent, int r, int g, int b, int a, boolean rainbow) {
        super(name, parent);
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.br = Color.RGBtoHSB(r, g, b, new float[3])[2];
        this.s = Color.RGBtoHSB(r, g, b, new float[3])[1];
        this.speed = 3;
        this.sync = false;
        this.rainbow = rainbow;
    }

    public int getR() {
        return r;
    }

    public ColorSetting setR(int r) {
        this.r = r;
        refreshHSB();
        return this;
    }

    public int getG() {
        return g;
    }

    public ColorSetting setG(int g) {
        this.g = g;
        refreshHSB();
        return this;
    }

    public int getB() {
        return b;
    }

    public ColorSetting setB(int b) {
        this.b = b;
        refreshHSB();
        return this;
    }

    public int getA() {
        return a;
    }

    public ColorSetting setA(int a) {
        this.a = a;
        return this;
    }

    private void refreshHSB() {
        this.br = Color.RGBtoHSB(r, g, b, new float[3])[2];
        this.s = Color.RGBtoHSB(r, g, b, new float[3])[1];
    }

    public Color getValue() {
        if (sync) {
            return ClientColor.color.getValue();
        } else if (rainbow) {
            return Rainbow.getColorStatic(0.0f, (float) speed, s, br, a);
        }
        return new java.awt.Color(r, g, b, a);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getJsonString() {
        JSONObject s = new JSONObject();
        if(getSubSettings().isEmpty()) {
            putValues(s);
        } else {
            JSONObject val = new JSONObject();
            putValues(val);
            s.put("value", val);
            JSONObject a = new JSONObject();
            for(Setting l : getSubSettings()) {
                a.put(l.getName(), l.getJsonString());
            }
            s.put("settings", a);
        }
        return s;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void parseJson(JSONObject main, String key, Object val) {
        if(val instanceof JSONObject) {
            final JSONObject j = (JSONObject) val;

            JSONObject sets = (JSONObject) j.get("settings");

            if(sets != null) {
                readValues((JSONObject) j.get("value"));

                for(String k : (Set<String>) sets.keySet()) {
                    final Object v = sets.get(k);
                    getSubSettings().forEach(e -> {
                        if(e.getName().equals(k)) {
                            e.parseJson(main, k, v);
                        }
                    });
                }
            } else {
                readValues((JSONObject) val);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void putValues(JSONObject s) {
        s.put("r", r);
        s.put("g", g);
        s.put("b", b);
        s.put("a", a);
        s.put("s", speed);
        s.put("sy", sync);
        s.put("ra", rainbow);
        s.put("br", br);
        s.put("sa", this.s);
    }

    private void readValues(JSONObject s) {
        this.r = ((Long) s.get("r")).intValue();
        this.g = ((Long) s.get("g")).intValue();
        this.b = ((Long) s.get("b")).intValue();
        this.a = ((Long) s.get("a")).intValue();
        this.speed = (Double) s.get("s");
        this.sync = (Boolean) s.get("sy");
        this.rainbow = (Boolean) s.get("ra");
        this.br = ((Double) s.get("br")).floatValue();
        this.s = ((Double) s.get("sa")).floatValue();
    }
}
