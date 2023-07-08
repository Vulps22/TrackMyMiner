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

    private final TrackMyMiner plugin;

    public CommandSpy(TrackMyMiner plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("miner.spy")) return true;

        for (String arg : args) {
            if (arg.isEmpty()) return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(args[0] + " could not be located.");
            return true;
        }

        Location targetLocation = target.getLocation();

        plugin.setSpyOrigin(player, target);
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(targetLocation);

        return true;
    }
}
