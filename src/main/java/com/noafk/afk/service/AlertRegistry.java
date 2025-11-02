package com.noafk.afk.service;

import com.noafk.NoAFKPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

import static com.noafk.util.ChatUtil.color;

public class AlertRegistry {
    private final NoAFKPlugin plugin;
    private final Set<UUID> receivers = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public AlertRegistry(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    public void toggle(Player player) {
        UUID id = player.getUniqueId();
        if (receivers.contains(id)) {
            receivers.remove(id);
            player.sendMessage(color(plugin.getConfig().getString("messages.alerts_disabled", "AFK alerts disabled.")));
        } else {
            receivers.add(id);
            player.sendMessage(color(plugin.getConfig().getString("messages.alerts_enabled", "AFK alerts enabled.")));
        }
    }

    public void broadcastEnterAfk(Player p) {
        String msg = plugin.getConfig().getString("messages.alert_afk", "%player% is now AFK.");
        if (msg == null || msg.isEmpty()) return;
        msg = color(msg.replace("%player%", p.getName()));
        for (UUID id : receivers) {
            Player receiver = Bukkit.getPlayer(id);
            if (receiver != null && receiver.isOnline()) {
                receiver.sendMessage(msg);
            }
        }
    }
}