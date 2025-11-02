package com.noafk.commands;

import com.noafk.NoAFKPlugin;
import static com.noafk.util.ChatUtil.color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AfkReloadCommand implements CommandExecutor {
    private final NoAFKPlugin plugin;

    public AfkReloadCommand(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("noafk.reload")) {
            sender.sendMessage(color(plugin.getConfig().getString("messages.no_permission", "You don't have permission.")));
            return true;
        }
        plugin.reloadNoAfkConfig();
        sender.sendMessage(color(plugin.getConfig().getString("messages.reload_done", "Configuration reloaded.")));
        return true;
    }
}