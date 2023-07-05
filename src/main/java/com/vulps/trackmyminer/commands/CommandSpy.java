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
                if(string.equals("")) return false;
            }



            Player target = Bukkit.getPlayer(strings[0]);
            if(target == null){
                player.sendMessage(target.getName() + " could not be located.");
                return false;
            }
            Location targetLocation = target.getLocation();
            plugin.setSpyOrigin(player, target);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(targetLocation);
            return true;
        }
        return false;
    }
}
