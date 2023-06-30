package com.vulps.trackmyminer;

import com.vulps.trackmyminer.commands.CommandBack;
import com.vulps.trackmyminer.commands.CommandSpy;
import com.vulps.trackmyminer.handlers.BlockHandler;
import com.vulps.trackmyminer.handlers.LoginHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class TrackMyMiner extends JavaPlugin {
    private final HashMap<Player, PlayerMiningRecord> lastMinedRecord = new HashMap<>();
    private final Map<Player, SpyOrigin> spyOrigin = new HashMap<>();


    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        List<String> blocks = getConfig().getStringList("blocks");
        if(!blocks.isEmpty()){
            Bukkit.getLogger().info("[TrackMyMiner] Adding Blocks to Monitor");
            for(String block : blocks){
                Bukkit.getLogger().info("[TrackMyMiner] " + block);
            }
        }

        int notifyLevel = getConfig().getInt("level");
        Bukkit.getLogger().info("[TrackMyMiner] Monitoring blocks below level " + notifyLevel);

        new BlockHandler(this);
        new LoginHandler(this);

        this.getCommand("mspy").setExecutor( new CommandSpy(this));
        this.getCommand("mBack").setExecutor(new CommandBack(this));
    }

    @Override
    public void onDisable() {
        // reset all player positions if they are spying
        Set<Player> players = spyOrigin.keySet();
        for (Player player : players) {
            cleanSpyOrigin(player);
        }
    }

    public boolean sendMessage(String message){
        if(message == "") return false;
        Bukkit.broadcastMessage("[TrackMyMiner] " + message);
        return true;
    }
    public boolean sendMessage(String message, String permission){
        if(message == "") return false;
        Bukkit.broadcast("[TrackMyMiner] " + message, permission);

        return true;
    }

    /**
     * Send a message to all players who are either OP or have the specified permission
     * @param message
     * @param permission
     * @return
     */
    public boolean sendNotifyMessage(String message, String permission){
        if(message == "") return false;
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.isOp() || player.hasPermission(permission)){
                player.sendMessage("[TrackMyMiner] " + message);
            }
        }
        return true;
    }

    public void sendNotifyMessage(BaseComponent[] message, String permission){
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.isOp() || player.hasPermission(permission)){
                player.spigot().sendMessage(message);
            }
        }
    }

    public void setMined(Player player, Location location, Block block) {

        if(lastMinedRecord.containsKey(player)) {
            PlayerMiningRecord miningRecord = lastMinedRecord.get(player);
            miningRecord.recordMined(block, location);
        }else{
            PlayerMiningRecord miningRecord = new PlayerMiningRecord(player, block, location, this);
            lastMinedRecord.put(player, miningRecord);
        }
    }
    public void removeMiningRecord(Player player){
        lastMinedRecord.remove(player);
    }

    public void setSpyOrigin(Player player) {
        Location location = player.getLocation();

        // Check if the player already exists in the spyOrigin map
        if (spyOrigin.containsKey(player)) {
            SpyOrigin origin = spyOrigin.get(player);
            origin.setOrigin(location);
        } else {
            SpyOrigin origin = new SpyOrigin(player.getGameMode(), location);
            spyOrigin.put(player, origin);
        }
    }

    public SpyOrigin getSpyOrigin(Player player){
        return spyOrigin.getOrDefault(player, null);
    }

    public void cleanSpyOrigin(Player player){
        SpyOrigin origin = getSpyOrigin(player);
        if(origin == null) return;
        player.teleport(origin.getOrigin());
        player.setGameMode(origin.getGameMode());
        removeOrigin(player);
    }

    public void removeOrigin(Player player){
        spyOrigin.remove(player);
    }


}
