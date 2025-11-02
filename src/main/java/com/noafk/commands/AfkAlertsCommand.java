package com.noafk.commands;

import com.noafk.NoAFKPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static com.noafk.util.ChatUtil.color;

public class AfkAlertsCommand implements CommandExecutor, TabCompleter {
    private final NoAFKPlugin plugin;

    public AfkAlertsCommand(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(color("&eUsage: &7/noafk alerts &8| &7/noafk updateconfig"));
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "alerts": {
                if (!sender.hasPermission("noafk.alerts")) {
                    sender.sendMessage(color(plugin.getConfig().getString("messages.no_permission", "You don't have permission.")));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(color(plugin.getConfig().getString("messages.only_player", "Only players can use this command.")));
                    return true;
                }
                Player p = (Player) sender;
                plugin.getAfkManager().toggleAlerts(p);
                return true;
            }
            case "updateconfig": {
                if (!sender.hasPermission("noafk.updateconfig")) {
                    sender.sendMessage(color(plugin.getConfig().getString("messages.no_permission", "You don't have permission.")));
                    return true;
                }
                List<String> added = plugin.updateConfigKeys();
                if (added.isEmpty()) {
                    sender.sendMessage(color("&aNo new keys to add to &7config.yml&a."));
                } else {
                    sender.sendMessage(color("&aAdded &e" + added.size() + " &akeys to &7config.yml&a:"));
                    sender.sendMessage(color("&7- " + added.stream().collect(Collectors.joining(", "))));
                }
                return true;
            }
            default:
                sender.sendMessage(color("&eUsage: &7/noafk alerts &8| &7/noafk updateconfig"));
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return java.util.Arrays.asList("alerts", "updateconfig");
        }
        return Collections.emptyList();
    }
}