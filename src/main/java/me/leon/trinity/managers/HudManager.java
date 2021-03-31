package me.leon.trinity.managers;

import me.leon.trinity.hud.Component;
import me.leon.trinity.hud.components.RagDollComponent;

import java.util.ArrayList;

public class HudManager {
    public ArrayList<Component> comps;

    public HudManager() {
        this.comps = new ArrayList<>();

        comps.add(new RagDollComponent());
    }
}
