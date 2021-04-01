package me.leon.trinity.managers;

import me.leon.trinity.hacks.Module;
import me.leon.trinity.hud.Component;
import me.leon.trinity.hud.components.FPSComponent;
import me.leon.trinity.hud.components.RagDollComponent;
import me.leon.trinity.setting.Setting;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class HudManager {
    public ArrayList<Component> comps;

    public HudManager() {
        this.comps = new ArrayList<>();

        comps.add(new RagDollComponent());
        comps.add(new FPSComponent());
    }
}
