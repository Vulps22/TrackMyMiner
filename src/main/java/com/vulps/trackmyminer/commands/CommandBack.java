package com.vulps.trackmyminer.commands;

import com.vulps.trackmyminer.SpyOrigin;
import com.vulps.trackmyminer.TrackMyMiner;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBack implements CommandExecutor {
    TrackMyMiner plugin;
    public CommandBack(TrackMyMiner plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(!player.isOp() || !player.hasPermission("miner.spy")) return false;
            SpyOrigin origin = plugin.getSpyOrigin(player);
            if(origin == null){
                player.sendMessage("You are not spying");
                return false;

            }
            player.teleport(origin.getOrigin());
            player.setGameMode(origin.getGameMode());
            plugin.removeOrigin(player);
            return true;
        }
        return false;
    }
}
