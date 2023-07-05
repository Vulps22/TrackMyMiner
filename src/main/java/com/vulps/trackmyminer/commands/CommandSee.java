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

    TrackMyMiner plugin;

    public CommandSee(TrackMyMiner plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
       if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player executor = (Player) sender;


        if (!sender.isOp() && !sender.hasPermission("miner.see")) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            TrackMyMiner.log(plugin.isSpying(executor.getPlayer()).toString());
            if(plugin.isSpying(executor.getPlayer())){
                Player target = plugin.getSpyOrigin(executor).getTarget();
                openInventoryGUI(executor, target);
            }else {
                sender.sendMessage("Usage: /mSee <player>");
            }
            return true;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            sender.sendMessage("Player " + targetPlayerName + " is not online.");
            return true;
        }


        openInventoryGUI(executor, targetPlayer);

        return true;
    }

    private void openInventoryGUI(Player player, Player targetPlayer) {
        TrackMyMiner.log("Getting player inventory of: " + targetPlayer.getName());
        Inventory targetInventory = targetPlayer.getInventory();
        Inventory guiInventory = Bukkit.createInventory(null, 54, "Target Inventory: " + targetPlayer.getName());

        // Copy the items from the target player's inventory to the GUI inventory
        for (int slot = 0; slot < targetInventory.getSize(); slot++) {
            ItemStack item = targetInventory.getItem(slot);
            if (item != null) {
                ItemStack clonedItem = item.clone();
                ItemMeta itemMeta = clonedItem.getItemMeta();
                // Modify item meta or perform any additional changes if needed
                guiInventory.setItem(slot, clonedItem);
            }
        }

        // Open the GUI inventory for the player
        player.openInventory(guiInventory);
    }

}
