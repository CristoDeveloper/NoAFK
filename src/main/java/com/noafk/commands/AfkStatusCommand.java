package com.noafk.commands;

import com.noafk.NoAFKPlugin;
import static com.noafk.util.ChatUtil.color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AfkStatusCommand implements CommandExecutor {
    private final NoAFKPlugin plugin;

    public AfkStatusCommand(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("noafk.afkstatus")) {
            sender.sendMessage(color(plugin.getConfig().getString("messages.no_permission", "You don't have permission.")));
            return true;
        }
        List<Player> afk = plugin.getAfkManager().getAfkPlayers();
        if (afk.isEmpty()) {
            sender.sendMessage(color(plugin.getConfig().getString("messages.status_none", "No AFK players.")));
            return true;
        }
        sender.sendMessage(color(plugin.getConfig().getString("messages.status_header", "AFK Players:")));
        String line = plugin.getConfig().getString("messages.status_line", "- %player% (%time%s)");
        for (Player p : afk) {
            long seconds = plugin.getAfkManager().getCurrentAfkSeconds(p);
            sender.sendMessage(color(line.replace("%player%", p.getName()).replace("%time%", String.valueOf(seconds))));
        }
        return true;
    }
}