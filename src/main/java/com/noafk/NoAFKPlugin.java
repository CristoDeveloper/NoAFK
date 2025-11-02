package com.noafk;

import com.noafk.afk.AfkManager;
import com.noafk.listener.ChatListener;
import com.noafk.listener.MovementListener;
import com.noafk.placeholders.NoAfkPlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

public class NoAFKPlugin extends JavaPlugin {

    private AfkManager afkManager;
    private boolean movementEnabled;
    private boolean chatEnabled;
    private int afkTimeSeconds;
    private int movementBlocks;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings(getConfig());
        // Update config by adding any missing keys from defaults
        updateConfigKeys();

        this.afkManager = new AfkManager(this);
        this.afkManager.setConfig(afkTimeSeconds, movementBlocks, movementEnabled, chatEnabled);

        // Register listeners based on types
        PluginManager pm = getServer().getPluginManager();
        if (movementEnabled) {
            pm.registerEvents(new MovementListener(this), this);
            getLogger().info("Movement detection active.");
        }
        if (chatEnabled) {
            pm.registerEvents(new ChatListener(this), this);
            getLogger().info("Chat detection active.");
        }

        // Register commands
        if (getCommand("afk") != null) {
            getCommand("afk").setExecutor(new com.noafk.commands.AfkCommand(this));
            getCommand("afk").setTabCompleter(new com.noafk.commands.AfkCommand(this));
        }
        if (getCommand("afkstatus") != null) {
            getCommand("afkstatus").setExecutor(new com.noafk.commands.AfkStatusCommand(this));
        }
        if (getCommand("afkreload") != null) {
            getCommand("afkreload").setExecutor(new com.noafk.commands.AfkReloadCommand(this));
        }
        if (getCommand("noafk") != null) {
            getCommand("noafk").setExecutor(new com.noafk.commands.AfkAlertsCommand(this));
            getCommand("noafk").setTabCompleter(new com.noafk.commands.AfkAlertsCommand(this));
        }

        // PlaceholderAPI integration
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new NoAfkPlaceholderExpansion(this).register();
            getLogger().info("PlaceholderAPI detected: placeholders registered.");
        } else {
            getLogger().warning("PlaceholderAPI not found: placeholders not available.");
        }

        // Start AFK scheduler (async computations, sync actions when needed)
        startAfkScheduler();

        // Plugin messaging channel for BungeeCord (redirect)
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getLogger().info("NoAFK enabled.");
    }

    @Override
    public void onDisable() {
        if (afkManager != null) {
            afkManager.shutdown();
        }
        getLogger().info("NoAFK disabled.");
    }

    public AfkManager getAfkManager() {
        return afkManager;
    }

    public int getAfkTimeSeconds() {
        return afkTimeSeconds;
    }

    public int getMovementBlocks() {
        return movementBlocks;
    }

    public boolean isMovementEnabled() {
        return movementEnabled;
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void reloadNoAfkConfig() {
        reloadConfig();
        loadSettings(getConfig());
        // Update missing keys also after reload
        updateConfigKeys();
        if (afkManager != null) {
            afkManager.setConfig(afkTimeSeconds, movementBlocks, movementEnabled, chatEnabled);
        }
        getLogger().log(Level.INFO, "Configuration reloaded.");
    }

    /**
     * Adds missing keys from the default config to the current config
     * without overwriting existing values.
     * Returns the list of added keys and logs the operations.
     * Note: saving might not preserve all YAML comments.
     */
    public List<String> updateConfigKeys() {
        List<String> added = new java.util.ArrayList<>();
        try {
            FileConfiguration current = getConfig();
            if (getResource("config.yml") == null) {
                getLogger().warning("Default config.yml resource not found in JAR.");
                return added;
            }
            InputStreamReader reader = new InputStreamReader(getResource("config.yml"), StandardCharsets.UTF_8);
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
            current.setDefaults(defaults);
            current.options().copyDefaults(true);
            for (String key : defaults.getKeys(true)) {
                if (!current.contains(key)) {
                    current.set(key, defaults.get(key));
                    added.add(key);
                }
            }
            // Additional keys recently introduced not present in JAR defaults
            if (!current.contains("afk.redirect_mode")) {
                current.set("afk.redirect_mode", "bungee");
                added.add("afk.redirect_mode");
            }
            if (!added.isEmpty()) {
                saveConfig();
                getLogger().info("Keys added to config: " + String.join(", ", added));
            } else {
                getLogger().info("No new keys to add to config.");
            }
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, "Error updating config: " + ex.getMessage(), ex);
        }
        return added;
    }

    private void loadSettings(FileConfiguration cfg) {
        List<String> types = cfg.getStringList("types");
        this.movementEnabled = types.stream().anyMatch(s -> s.equalsIgnoreCase("movement"));
        this.chatEnabled = types.stream().anyMatch(s -> s.equalsIgnoreCase("chat"));
        this.afkTimeSeconds = cfg.getInt("afk.time", 300);
        this.movementBlocks = cfg.getInt("movement.blocks", 5);
    }

    private void startAfkScheduler() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                afkManager.tickAfkCheck();
            } catch (Exception ex) {
                getLogger().log(Level.WARNING, "Error in AFK check: " + ex.getMessage(), ex);
            }
        }, 40L, 40L); // every 2 seconds
    }
}