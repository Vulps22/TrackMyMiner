package com.vulps.trackmyminer;

import com.vulps.trackmyminer.handlers.BlockHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class TrackMyMiner extends JavaPlugin {

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

        new BlockHandler(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
