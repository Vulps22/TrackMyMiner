package com.vulps.trackmyminer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerMiningRecord {
    private TrackMyMiner plugin;
    public Player player;
    public Long lastMined;
    public Location lastLocation;//player location
    public int veins;
    public Material lastBlock;

    public PlayerMiningRecord(Player player, Block block, Location location, TrackMyMiner plugin){
        this.plugin = plugin;
        this.player = player;
        this.veins = 1;
        this.lastMined = System.currentTimeMillis();
        this.lastLocation = location;//player location
        this.lastBlock = block.getType();
    }

    public Player getPlayer() {
        return player;
    }
    public void recordMined(Block block, Location location){

        lastLocation = location;

        Long currentTime = System.currentTimeMillis();
        long passedTime = currentTime - lastMined;

        if(passedTime < 600000) { //less than 10 minutes has passed
            if (passedTime > 10000){
                Bukkit.getLogger().info("10 seconds has passed - New Vein");
                lastBlock = block.getType();
                this.lastMined = System.currentTimeMillis();
                this.lastLocation = location;
                newVein(true); //more than 10 seconds has passed, this is a new vein
            } else if(lastBlock != block.getType()){
                Bukkit.getLogger().info("different block - New Vein");
                Bukkit.getLogger().info(block.getType().name());
                Bukkit.getLogger().info(lastBlock.name());
                lastBlock = block.getType();
                this.lastMined = System.currentTimeMillis();
                this.lastLocation = location;
                newVein(true); //a different block means a different vein
            }else { //not a new vein but still keeping track
                Bukkit.getLogger().info("Not A New Vein");

                this.lastBlock = block.getType();
                this.lastMined = System.currentTimeMillis();
                this.lastLocation = location;
            }
            
        } else{ //10 minutes has passed. Reset the record
            Bukkit.getLogger().info("10 minutes has passed - RESET");
            this.veins = 1;
            this.lastMined = System.currentTimeMillis();
            this.lastLocation = location;
            this.lastBlock = block.getType();
        }

    }

    private void newVein(Boolean shouldNotify){
        veins++;
        if(shouldNotify && veins > 2) sendNotify(); // more than 2 veins in less than 10 minutes? sounds suss
    }

    private void sendNotify(){
        // Notify staff
        BaseComponent[] component = new ComponentBuilder("[TrackMyMiner] ").color(ChatColor.RED)
                .append(lastBlock.name() + " has been mined by "
                        + player.getName() + " at "
                        + lastLocation.getBlockX()
                        + ", " + lastLocation.getBlockY()
                        + ", " + lastLocation.getBlockZ()
                        + ", ").color(ChatColor.WHITE)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mspy " + player.getName())).create();
        plugin.sendNotifyMessage(component, "miner.notify");
    }
}
