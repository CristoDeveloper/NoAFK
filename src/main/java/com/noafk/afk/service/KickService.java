package com.noafk.afk.service;

import com.noafk.NoAFKPlugin;
import org.bukkit.entity.Player;

import static com.noafk.util.ChatUtil.color;

public class KickService {
    private final NoAFKPlugin plugin;

    public KickService(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Kicks the player if enabled in config.
     * @return true if the player was kicked (and therefore subsequent actions should be interrupted)
     */
    public boolean kickIfEnabled(Player p) {
        if (!plugin.getConfig().getBoolean("afk.kick_enabled", false)) return false;
        String reasonText = plugin.getConfig().getString("afk.kick_reason", "You have been kicked for being AFK.");
        p.kickPlayer(color(reasonText.replace("%player%", p.getName())));
        return true;
    }
}