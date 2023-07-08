package com.vulps.trackmyminer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerMiningRecord {
    private final TrackMyMiner plugin;
    public Player player;
    public Long lastMined;
    public Location lastLocation;//player location
    public int veins;
    public Material lastBlock; //we use Material because Block changes when the referenced block updates

    public PlayerMiningRecord(Player player, Block block, Location location, TrackMyMiner plugin){
        this.plugin = plugin;
        this.player = player;
        this.veins = 1;
        this.lastMined = System.currentTimeMillis();
        this.lastLocation = location; //player location
        this.lastBlock = block.getType();
    }

    public void recordMined(Block block, Location location){

        lastLocation = location;

        long currentTime = System.currentTimeMillis();
        long passedTime = currentTime - lastMined;

        if (passedTime < 600000) { //less than 10 minutes has passed
            if (passedTime > 10000){
                lastBlock = block.getType();
                this.lastMined = System.currentTimeMillis();
                this.lastLocation = location;
                newVein(); //more than 10 seconds has passed, this is a new vein
            } else if (lastBlock != block.getType()){
                lastBlock = block.getType();
                this.lastMined = System.currentTimeMillis();
                this.lastLocation = location;
                newVein(); //a different block means a different vein
            } else { //not a new vein but still keeping track
                this.lastBlock = block.getType();
                this.lastMined = System.currentTimeMillis();
                this.lastLocation = location;
            }
            
        } else { //10 minutes has passed. Reset the record
            this.veins = 1;
            this.lastMined = System.currentTimeMillis();
            this.lastLocation = location;
            this.lastBlock = block.getType();
        }

    }

    private void newVein() {
        veins++;
        if (veins > 2) sendNotify(); // more than 2 veins in less than 10 minutes? sounds suss
    }

    private void sendNotify() {
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
