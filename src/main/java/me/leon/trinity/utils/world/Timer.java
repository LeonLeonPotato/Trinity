package me.leon.trinity.utils.world;

import me.leon.trinity.utils.Util;
import org.lwjgl.Sys;

public class Timer implements Util {
    private long time;

    public Timer(long time) {
        this.time = time;
    }

    public Timer() {
        this.time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void reset() {
        this.time = System.currentTimeMillis();
    }

    public boolean hasPassed(int millis) {
        return (System.currentTimeMillis() - this.time) >= millis;
    }

    public boolean passAndReset(int millis) {
        if(System.currentTimeMillis() - this.time >= millis) {
            reset();
            return true;
        } else return false;
    }
}
