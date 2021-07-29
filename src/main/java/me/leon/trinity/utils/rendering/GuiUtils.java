package me.leon.trinity.utils.rendering;

import me.leon.trinity.gui.ClickGui;
import me.leon.trinity.gui.frame.FrameComponent;
import me.leon.trinity.gui.IComponent;
import me.leon.trinity.gui.frame.IFrame;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.utils.Util;

import java.awt.*;
import java.util.ArrayList;

public class GuiUtils implements Util {
    public static ArrayList<IComponent> findSubComps(IComponent comp) {
        ArrayList<IComponent> toReturn = new ArrayList<>();
        for(IComponent c : ClickGui.getTotalComponents()) {
            if(c.parent() == comp) {
                toReturn.add(c);
            }
        }
        return toReturn;
    }

    public static IFrame findFrame(Category c) {
        for(IFrame a : ClickGui.getFrames()) {
            if(a.getCategory() == c)
                return a;
        }
        return null;
    }

    public static IFrame findFrame(String name) {
        for(IFrame a : ClickGui.getFrames()) {
            if(a.getCategory().name().equalsIgnoreCase(name))
                return a;
        }
        return null;
    }

    public static boolean onButton(float x, float y, float x1, float y1, Point point) {
        return point.x >= x && point.x <= x1 && point.y >= y && point.y <= y1;
    }
}
