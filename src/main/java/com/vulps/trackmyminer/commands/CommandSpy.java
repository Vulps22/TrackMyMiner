package com.vulps.trackmyminer.commands;

import com.vulps.trackmyminer.TrackMyMiner;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpy implements CommandExecutor {

    TrackMyMiner plugin;

    public CommandSpy(TrackMyMiner plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(!player.isOp()) return false;

            for(String string : strings) {
                if(string == "") return false;
            }

            plugin.setSpyOrigin(player);

            Player target = Bukkit.getPlayer(strings[0]);
            Location targetLocation = target.getLocation();

            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(targetLocation);
            return true;
        }
        return false;
    }
}
