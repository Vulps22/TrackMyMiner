package com.vulps.trackmyminer;

import org.bukkit.GameMode;
import org.bukkit.Location;

public class SpyOrigin {
    private final GameMode gameMode;
    private Location origin;
    public SpyOrigin(GameMode gamemode, Location origin) {
        this.gameMode = gamemode;
        this.origin = origin;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }
}
