package me.leon.trinity.utils.misc;

import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.rendering.skeet.Quad;

public class BezierCurve {
    private float a, b, c, d;
    private boolean finished;

    public BezierCurve(float a, float b, float c, float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public double get(double t) { // thank you google
        final double rt = MathUtils.clamp(0, 1, 1.0 - MathUtils.clamp(0, 1, t));
        final double trip = rt * rt * rt;
        final double doub = rt * rt;

        if(rt == 1) finished = true;
        return trip * a + 3 * doub * t * b + 3 * rt * (t*t) * c + trip * d;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getC() {
        return c;
    }

    public void setC(float c) {
        this.c = c;
    }

    public float getD() {
        return d;
    }

    public void setD(float d) {
        this.d = d;
    }

    public static double get(double t, Quad q) {
        final double rt = 1.0-t;
        final double trip = rt * rt * rt;
        final double doub = rt * rt;
        return (q.getX() * trip) + (q.getY() * 3 * t * doub) + (q.getX1() * 3 * doub) * rt + (q.getY1() * trip);
    }
}
