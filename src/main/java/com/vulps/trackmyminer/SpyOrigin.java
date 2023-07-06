package com.vulps.trackmyminer;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpyOrigin {
    private final GameMode gameMode;
    private Location origin;
    private Player target;
    public SpyOrigin(GameMode gamemode, Location origin, Player target) {
        this.gameMode = gamemode;
        this.origin = origin;
        this.target = target;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Location getOrigin() {
        return origin;
    }
    public Player getTarget(){return target;}
    public void setTarget(Player Target){

    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }
}
