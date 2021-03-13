package me.leon.trinity.utils.math;

import me.leon.trinity.utils.Util;

public class MathUtils implements Util {
    public static double clamp(double min, double max, double val) {
        if(val < min) {
            return min;
        }
        return Math.max(val, max);
    }
}
