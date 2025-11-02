package com.noafk.afk.service;

import com.noafk.NoAFKPlugin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;

import java.util.Locale;

public class RedirectService {
    private final NoAFKPlugin plugin;

    public RedirectService(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    public void redirectIfEnabled(Player p) {
        if (!plugin.getConfig().getBoolean("afk.bungee_redirect_enabled", false)) return;

        String server = plugin.getConfig().getString("afk.bungee_server", "lobby");
        String mode = plugin.getConfig().getString("afk.redirect_mode", "bungee");
        mode = mode == null ? "bungee" : mode.toLowerCase(Locale.ROOT);

        boolean channel = plugin.getServer().getMessenger().isOutgoingChannelRegistered(plugin, "BungeeCord");
        if (!channel) {
            plugin.getLogger().warning("BungeeCord channel not registered: unable to redirect " + p.getName() + " to '" + server + "'."
                    + " Enable channel registration in onEnable.");
            return;
        }

        try {
            if ("velocity".equals(mode)) {
                plugin.getLogger().info("Redirect mode 'velocity': using BungeeCord channel for compatibility. Make sure the proxy enables the compatibility channel.");
            }
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            plugin.getLogger().info("Redirect attempt (" + mode + ") for " + p.getName() + " -> server '" + server + "'.");
        } catch (Exception ex) {
            plugin.getLogger().warning("Error sending plugin message for redirect: " + ex.getMessage());
        }
    }
}