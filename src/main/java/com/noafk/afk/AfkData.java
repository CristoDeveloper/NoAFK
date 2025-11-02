package com.noafk.afk;

import org.bukkit.Location;

public class AfkData {
    private boolean afk;
    private long lastActiveMillis;
    private long afkStartMillis;
    private long totalAfkMillis;
    private Location afkStartLocation;

    public AfkData() {
        this.afk = false;
        this.lastActiveMillis = System.currentTimeMillis();
        this.afkStartMillis = 0L;
        this.totalAfkMillis = 0L;
        this.afkStartLocation = null;
    }

    public boolean isAfk() {
        return afk;
    }

    public void setAfk(boolean afk) {
        this.afk = afk;
    }

    public long getLastActiveMillis() {
        return lastActiveMillis;
    }

    public void setLastActiveMillis(long lastActiveMillis) {
        this.lastActiveMillis = lastActiveMillis;
    }

    public long getAfkStartMillis() {
        return afkStartMillis;
    }

    public void setAfkStartMillis(long afkStartMillis) {
        this.afkStartMillis = afkStartMillis;
    }

    public long getTotalAfkMillis() {
        return totalAfkMillis;
    }

    public void addToTotalAfkMillis(long millis) {
        this.totalAfkMillis += Math.max(0, millis);
    }

    public Location getAfkStartLocation() {
        return afkStartLocation;
    }

    public void setAfkStartLocation(Location afkStartLocation) {
        this.afkStartLocation = afkStartLocation;
    }
}