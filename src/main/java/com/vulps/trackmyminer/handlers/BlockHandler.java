package com.vulps.trackmyminer.handlers;

import com.vulps.trackmyminer.TrackMyMiner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class BlockHandler implements Listener {
    List<String> monitoredBlocks;
    int monitoredLevel;
    TrackMyMiner plugin;
    public BlockHandler(TrackMyMiner plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        monitoredBlocks = plugin.getConfig().getStringList("blocks");
        monitoredLevel = plugin.getConfig().getInt("level");
    }

    @EventHandler
    public void onBlockPlace(BlockBreakEvent event){
        Block block = event.getBlock();

        int level = block.getLocation().getBlockY();
        if(level > monitoredLevel) return; //if the block was broken above the "monitored level" do not track

        for (String monitoredBlock : monitoredBlocks) {
            if (block.getType() == Material.getMaterial(monitoredBlock)) {
                Player player = event.getPlayer();
                Location location = player.getLocation();
                plugin.setMined(player, location, block);
            }
        }
    }
}
