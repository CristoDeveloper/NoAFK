package com.noafk.afk;

import com.noafk.NoAFKPlugin;
import com.noafk.afk.service.AfkCommandRunner;
import com.noafk.afk.service.AlertRegistry;
import com.noafk.afk.service.KickService;
import com.noafk.afk.service.RedirectService;
import com.noafk.afk.service.TeleportService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import static com.noafk.util.ChatUtil.color;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AfkManager {
    public enum AfkReason { MANUAL, MOVEMENT_EXIT, CHAT_EXIT, TIME }

    private final NoAFKPlugin plugin;
    private final Map<UUID, AfkData> dataMap = new ConcurrentHashMap<>();

    private volatile int afkTimeSeconds;
    private volatile int movementBlocks;
    private volatile boolean movementEnabled;
    private volatile boolean chatEnabled;

    // Servizi dedicati
    private final AlertRegistry alerts;
    private final KickService kickService;
    private final RedirectService redirectService;
    private final TeleportService teleportService;
    private final AfkCommandRunner commandRunner;

    public AfkManager(NoAFKPlugin plugin) {
        this.plugin = plugin;
        this.alerts = new AlertRegistry(plugin);
        this.kickService = new KickService(plugin);
        this.redirectService = new RedirectService(plugin);
        this.teleportService = new TeleportService(plugin);
        this.commandRunner = new AfkCommandRunner(plugin);
    }

    public void setConfig(int afkTimeSeconds, int movementBlocks, boolean movementEnabled, boolean chatEnabled) {
        this.afkTimeSeconds = afkTimeSeconds;
        this.movementBlocks = movementBlocks;
        this.movementEnabled = movementEnabled;
        this.chatEnabled = chatEnabled;
    }

    public void toggleAlerts(Player player) { alerts.toggle(player); }

    private AfkData getOrCreate(Player p) {
        return dataMap.computeIfAbsent(p.getUniqueId(), k -> new AfkData());
    }

    public void markActivity(Player p) {
        AfkData d = getOrCreate(p);
        if (!d.isAfk()) {
            d.setLastActiveMillis(System.currentTimeMillis());
        }
    }

    public boolean isAfk(Player p) {
        return getOrCreate(p).isAfk();
    }

    public long getCurrentAfkSeconds(Player p) {
        AfkData d = getOrCreate(p);
        if (!d.isAfk()) return 0L;
        return Math.max(0L, (System.currentTimeMillis() - d.getAfkStartMillis()) / 1000L);
    }

    public long getTotalAfkSeconds(Player p) {
        AfkData d = getOrCreate(p);
        long total = d.getTotalAfkMillis();
        if (d.isAfk()) {
            total += Math.max(0, System.currentTimeMillis() - d.getAfkStartMillis());
        }
        return total / 1000L;
    }

    public List<Player> getAfkPlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(this::isAfk)
                .collect(Collectors.toList());
    }

    public void setAfk(Player p, boolean afk, AfkReason reason) {
        if (afk) {
            enterAfk(p, reason);
        } else {
            exitAfk(p, reason);
        }
    }

    private void enterAfk(Player p, AfkReason reason) {
        int delayTicks = (Math.abs(p.getUniqueId().hashCode()) & 3) + 1; // 1-4 ticks to avoid burst
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            AfkData d = getOrCreate(p);
            if (d.isAfk()) return;
            d.setAfk(true);
            d.setAfkStartMillis(System.currentTimeMillis());

            // Optional teleport when entering AFK
            teleportService.teleportIfEnabled(p);

            // Set AFK start position after possible teleport
            d.setAfkStartLocation(p.getLocation());

            // Send alerts to receivers
            alerts.broadcastEnterAfk(p);

            // Optional actions: kick or Bungee redirect
            if (kickService.kickIfEnabled(p)) return;
            redirectService.redirectIfEnabled(p);

            // Log
            if (plugin.getConfig().getBoolean("log", true)) {
                plugin.getLogger().info("AFK: " + p.getName() + " (reason=" + reason + ")");
            }

            // Message to player
            String msg = plugin.getConfig().getString("messages.afk_enter", "You are now AFK.");
            if (msg != null && !msg.isEmpty()) {
                p.sendMessage(color(msg.replace("%player%", p.getName())));
            }

            // Commands to execute
            commandRunner.run(p);
        }, delayTicks);
    }

    public void exitAfk(Player p, AfkReason reason) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            AfkData d = getOrCreate(p);
            if (!d.isAfk()) return;
            long now = System.currentTimeMillis();
            long session = Math.max(0, now - d.getAfkStartMillis());
            d.addToTotalAfkMillis(session);
            d.setAfk(false);
            d.setAfkStartMillis(0L);
            d.setAfkStartLocation(null);
            d.setLastActiveMillis(now);

            // Log
            if (plugin.getConfig().getBoolean("log", true)) {
                plugin.getLogger().info("AFK END: " + p.getName() + " (reason=" + reason + ")");
            }

            String key;
            if (reason == AfkReason.CHAT_EXIT) key = "messages.afk_exit_chat";
            else if (reason == AfkReason.MOVEMENT_EXIT) key = "messages.afk_exit_movement";
            else key = "messages.afk_exit";

            String msg = plugin.getConfig().getString(key, "You are no longer AFK.");
            if (msg != null && !msg.isEmpty()) {
                p.sendMessage(color(msg.replace("%player%", p.getName())));
            }
        });
    }

    public void tickAfkCheck() {
        long now = System.currentTimeMillis();
        for (Player p : Bukkit.getOnlinePlayers()) {
            AfkData d = getOrCreate(p);
            if (!d.isAfk()) {
                long idle = now - d.getLastActiveMillis();
                if (idle >= afkTimeSeconds * 1000L) {
                    // Enter AFK due to time
                    setAfk(p, true, AfkReason.TIME);
                }
            }
        }
    }

    public void shutdown() {
        // nothing specific for now
    }

    public int getMovementBlocks() { return movementBlocks; }
    public boolean isMovementEnabled() { return movementEnabled; }
    public boolean isChatEnabled() { return chatEnabled; }

    public double distanceFromAfkStart(Player p) {
        AfkData d = getOrCreate(p);
        Location start = d.getAfkStartLocation();
        if (start == null) return 0.0;
        try {
            Location current = p.getLocation();
            if (!Objects.equals(start.getWorld(), current.getWorld())) return Double.MAX_VALUE;
            return start.distance(current);
        } catch (Exception ignored) {
            return 0.0;
        }
    }

    // color() centralized in ChatUtil
}