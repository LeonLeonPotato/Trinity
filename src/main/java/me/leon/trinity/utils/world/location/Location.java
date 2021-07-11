package me.leon.trinity.utils.world.location;

import me.leon.trinity.utils.Util;
import me.leon.trinity.utils.world.Priority;

public class Location implements Util {
    private double posX;
    private double posY;
    private double posZ;
    private boolean onGround;
    private final Priority priority;

    public Location(double posX, double posY, double posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.onGround = mc.player.onGround;
        this.priority = Priority.Normal;
    }

    public Location(double posX, double posY, double posZ, boolean onGround) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.onGround = onGround;
        this.priority = Priority.Normal;
    }

    public Location(double posX, double posY, double posZ, Priority priority) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.onGround = mc.player.onGround;
        this.priority = priority;
    }

    public Location(double posX, double posY, double posZ, boolean onGround, Priority priority) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.onGround = onGround;
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }
}
