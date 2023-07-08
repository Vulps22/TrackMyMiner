package com.vulps.trackmyminer.commands;

import com.vulps.trackmyminer.TrackMyMiner;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.sound.midi.Track;

public class CommandSee implements CommandExecutor {

    private final TrackMyMiner plugin;

    public CommandSee(TrackMyMiner plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player executor = (Player) sender;

        if (!sender.hasPermission("miner.see")) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) { // If there isn't an argument (player name).
            TrackMyMiner.log(plugin.isSpying(executor.getPlayer()).toString());

            if (!plugin.isSpying(executor.getPlayer())) { // If sender is not spying on the target.
                sender.sendMessage("Usage: /mSee <player>");
                return true;
            }

            Player target = plugin.getSpyOrigin(executor).getTarget(); // View inventory of target.
            executor.openInventory(target.getInventory());

            return true;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            sender.sendMessage("Player " + targetPlayerName + " is not online.");
            return true;
        }

        executor.openInventory(targetPlayer.getInventory());

        return true;
    }
}