package com.vulps.trackmyminer.handlers;

import com.vulps.trackmyminer.TrackMyMiner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

public class BlockHandler implements Listener {
    List<String> monitoredBlocks;
    public BlockHandler(TrackMyMiner plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);

        monitoredBlocks = plugin.getConfig().getStringList("blocks");
    }

    @EventHandler
    public void onBlockPlace(BlockBreakEvent event){
        Block block = event.getBlock();

        for (String monitoredBlock : monitoredBlocks) {
            if(block.getType() == Material.getMaterial(monitoredBlock)){
                Bukkit.getLogger().info(monitoredBlock + " was broken!");
            }
        }
    }
}
