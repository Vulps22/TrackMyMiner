package com.vulps.trackmyminer;

import com.vulps.trackmyminer.commands.CommandBack;
import com.vulps.trackmyminer.commands.CommandSee;
import com.vulps.trackmyminer.commands.CommandSpy;
import com.vulps.trackmyminer.handlers.BlockHandler;
import com.vulps.trackmyminer.handlers.LoginHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public final class TrackMyMiner extends JavaPlugin {

    private final HashMap<Player, PlayerMiningRecord> lastMinedRecord = new HashMap<>();
    private final Map<Player, SpyOrigin> spyOrigin = new HashMap<>();
    public static String version;

    @Override
    public void onLoad() {
        new Metrics(this, 18987);
    }

    @Override
    public void onEnable() {
        log("===================== [TrackMyMiner] =====================");

        updateConfig();
        checkForUpdates();
        registerCommands();
        registerHandlers();
        logConfig();

        log("==========================================================");
    }

    private boolean checkForUpdates() {
        if (!getConfig().getBoolean("checkForUpdates")) return false;
        //check for updates
        try {
            version = Updater.getVersion();
            boolean shouldUpdate = Updater.shouldUpdate();
            if (!shouldUpdate) {
                log("I am up-to-date (v" + version + ")");
                return false;
            }
            log("A new update is available");
        } catch (Exception exception) {
            warn("UPDATE CHECKER FAILED: Unable to retrieve latest version");
            warn(exception.getMessage());
            return false;
        }
        return true;
    }

    private void updateConfig() {
        FileConfiguration config = getConfig();
        boolean updated = false;

        // Check and update each config key
        if (!config.isSet("checkForUpdates")) {
            // Key not found, add default value from config.yml
            config.set("checkForUpdates", true);
            updated = true;
        }

        if (!config.isSet("level")) {
            // Key not found, add default value from config.yml
            config.set("level", 32);
            updated = true;
        }

        if (!config.isSet("blocks")) {
            // Key not found, add default value from config.yml
            config.set("blocks", getDefaultBlockList());
            updated = true;
        }

        // Save the updated config if any changes were made
        if (updated) {
            saveConfigWithComments();
            log("config.yml has been updated");
        }
    }

    private List<String> getDefaultBlockList() {
        List<String> defaultBlocks = new ArrayList<>();
        defaultBlocks.add("DIAMOND_ORE");
        defaultBlocks.add("IRON_ORE");
        defaultBlocks.add("GOLD_ORE");

        return defaultBlocks;
    }

    private void saveConfigWithComments() {
        File configFile = new File(getDataFolder(), "config.yml");
        FileConfiguration config = getConfig();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write("# Run the update checker when the server starts and when players with the miner.update permission login");
            writer.newLine();
            writer.write("checkForUpdates: " + config.get("checkForUpdates"));
            writer.newLine();
            writer.newLine();
            writer.write("# Set a level for monitoring to start. Players above this level will not trigger a notification");
            writer.newLine();
            writer.write("# Set this to 400 to monitor at all levels");
            writer.newLine();
            writer.write("level: " + config.get("level"));
            writer.newLine();
            writer.newLine();
            writer.write("# List any block types in (block caps) that you want to monitor");
            writer.newLine();
            writer.write("blocks:");
            writer.newLine();
            List<String> blocks = config.getStringList("blocks");
            for (String block : blocks) {
                writer.write("  - " + block);
                writer.newLine();
            }
            writer.flush(); //write to file
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("mspy")).setExecutor(new CommandSpy(this));
        Objects.requireNonNull(this.getCommand("mback")).setExecutor(new CommandBack(this));
        Objects.requireNonNull(this.getCommand("msee")).setExecutor(new CommandSee(this));
    }

    private void registerHandlers() {
        new BlockHandler(this);
        new LoginHandler(this);
    }

    private void logConfig() {
        List<String> blocks = getConfig().getStringList("blocks");
        if (!blocks.isEmpty()) {
            log("Adding Blocks to Monitor");
            for (String block : blocks) {
                log(block);
            }
        }
        int notifyLevel = getConfig().getInt("level");
        log("Monitoring blocks below level " + notifyLevel);
    }

    @Override
    public void onDisable() {
        // reset all player positions if they are spying
        Set<Player> players = spyOrigin.keySet();
        for (Player player : players) {
            cleanSpyOrigin(player);
        }
    }

    public static void log(String message) {
        if (message.isEmpty()) return;
        Bukkit.getLogger().info("[TrackMyMiner] " + message);
    }

    public static void warn(String message) {
        if (message.isEmpty()) return;
        Bukkit.getLogger().warning("[TrackMyMiner] " + message);
    }

    public boolean sendMessage(String message) {
        if (message.isEmpty()) return false;
        Bukkit.broadcastMessage("[TrackMyMiner] " + message);
        return true;
    }

    public boolean sendMessage(String message, String permission) {
        if (message.isEmpty()) return false;
        Bukkit.broadcast("[TrackMyMiner] " + message, permission);
        return true;
    }

    /**
     * Send a message to all players who are either OP or have the specified permission
     *
     * @param message The message which is sent to the Player.
     * @param permission The permission the player needs to have the message sent.
     * @return If function is successful.
     */
    public boolean sendNotifyMessage(String message, String permission) {
        if (message.isEmpty()) return false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp() || player.hasPermission(permission)) {
                player.sendMessage("[TrackMyMiner] " + message);
            }
        }
        return true;
    }

    public void sendNotifyMessage(BaseComponent[] message, String permission) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(permission)) continue;
            player.spigot().sendMessage(message);
        }
    }

    public void setMined(Player player, Location location, Block block) {
        if (!lastMinedRecord.containsKey(player)) {
            PlayerMiningRecord miningRecord = new PlayerMiningRecord(player, block, location, this);
            lastMinedRecord.put(player, miningRecord);
            return;
        }
        PlayerMiningRecord miningRecord = lastMinedRecord.get(player);
        miningRecord.recordMined(block, location);
    }

    public void removeMiningRecord(Player player) {
        lastMinedRecord.remove(player);
    }

    public void setSpyOrigin(Player player, Player target) {
        Location location = player.getLocation();
        // Check if the player already exists in the spyOrigin map
        if (!spyOrigin.containsKey(player)) {
            SpyOrigin origin = new SpyOrigin(player.getGameMode(), location, target);
            spyOrigin.put(player, origin);
            return;
        }
        SpyOrigin origin = spyOrigin.get(player);
        origin.setOrigin(location);
        origin.setTarget(target);
    }

    public SpyOrigin getSpyOrigin(Player player) {
        return spyOrigin.getOrDefault(player, null);
    }

    public void cleanSpyOrigin(Player player) {
        SpyOrigin origin = getSpyOrigin(player);
        if (origin == null) return;
        player.teleport(origin.getOrigin());
        player.setGameMode(origin.getGameMode());
        removeOrigin(player);
    }

    public void removeOrigin(Player player) {
        spyOrigin.remove(player);
    }

    public Boolean isSpying(Player player) {
        return spyOrigin.containsKey(player);
    }

}
