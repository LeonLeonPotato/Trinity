package me.leon.trinity.utils.math;

import me.leon.trinity.utils.Util;

public class MathUtils implements Util {
    public static double clamp(double min, double max, double val) {
        if(val > max || min > max) {
            return max;
        }
        return Math.max(val, min);
    }
}
