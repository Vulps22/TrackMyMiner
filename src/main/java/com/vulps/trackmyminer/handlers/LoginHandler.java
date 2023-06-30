package com.vulps.trackmyminer.handlers;

import com.vulps.trackmyminer.SpyOrigin;
import com.vulps.trackmyminer.TrackMyMiner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginHandler implements Listener {

    TrackMyMiner plugin;
    public LoginHandler(TrackMyMiner plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event){
        Player player = event.getPlayer();

        //remove player from SpyOrigin if they exist and reset them
        plugin.cleanSpyOrigin(player);

        plugin.removeMiningRecord(player);

    }

}
