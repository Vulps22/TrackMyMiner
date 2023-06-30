package com.vulps.trackmyminer.handlers;

import com.vulps.trackmyminer.TrackMyMiner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
    TrackMyMiner plugin;
    public BlockHandler(TrackMyMiner plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        monitoredBlocks = plugin.getConfig().getStringList("blocks");
    }

    @EventHandler
    public void onBlockPlace(BlockBreakEvent event){
        Block block = event.getBlock();

        for (String monitoredBlock : monitoredBlocks) {
            if (block.getType() == Material.getMaterial(monitoredBlock)) {
                Player player = event.getPlayer();
                Location location = player.getLocation();

                plugin.setMined(player, location, block);
            }
        }
    }
}
