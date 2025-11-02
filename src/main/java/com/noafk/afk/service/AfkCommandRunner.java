package com.noafk.afk.service;

import com.noafk.NoAFKPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class AfkCommandRunner {
    private final NoAFKPlugin plugin;

    public AfkCommandRunner(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    public void run(Player p) {
        boolean enabled = plugin.getConfig().getBoolean("afk.commands_enabled", true);
        if (!enabled) return;
        List<String> commands = plugin.getConfig().getStringList("afk.commands");
        for (String cmd : commands) {
            if (cmd == null || cmd.trim().isEmpty()) continue;
            String toRun = cmd.replace("%player%", p.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), toRun);
        }
    }
}