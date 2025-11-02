package com.noafk.placeholders;

import com.noafk.NoAFKPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NoAfkPlaceholderExpansion extends PlaceholderExpansion {

    private final NoAFKPlugin plugin;

    public NoAfkPlaceholderExpansion(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "noafk";
    }

    @Override
    public @NotNull String getAuthor() {
        return "NoAFK";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";
        Player online = player.getPlayer();
        if (online == null) return "";
        switch (params.toLowerCase()) {
            case "afk":
                return String.valueOf(plugin.getAfkManager().isAfk(online));
            case "time":
                return String.valueOf(plugin.getAfkManager().getCurrentAfkSeconds(online));
            case "total":
                return String.valueOf(plugin.getAfkManager().getTotalAfkSeconds(online));
            default:
                return "";
        }
    }
}