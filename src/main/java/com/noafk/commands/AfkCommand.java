package com.noafk.commands;

import com.noafk.NoAFKPlugin;
import com.noafk.afk.AfkManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import static com.noafk.util.ChatUtil.color;

public class AfkCommand implements CommandExecutor, TabCompleter {
    private final NoAFKPlugin plugin;

    public AfkCommand(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(color(plugin.getConfig().getString("messages.only_player", "Only players can use this command.")));
                return true;
            }
            if (!sender.hasPermission("noafk.afk.self")) {
                sender.sendMessage(color(plugin.getConfig().getString("messages.no_permission", "You don't have permission.")));
                return true;
            }
            Player player = (Player) sender;
            AfkManager manager = plugin.getAfkManager();
            if (manager.isAfk(player)) {
                String alreadySelf = plugin.getConfig().getString("messages.afk_already_self", "You are already AFK.");
                sender.sendMessage(color(alreadySelf));
                return true;
            }
            manager.setAfk(player, true, AfkManager.AfkReason.MANUAL);
            String selfMsg = plugin.getConfig().getString("messages.afk_set_self", "You are now AFK.");
            player.sendMessage(color(selfMsg));
            return true;
        } else {
            if (!sender.hasPermission("noafk.afk.others")) {
                sender.sendMessage(color(plugin.getConfig().getString("messages.no_permission", "You don't have permission.")));
                return true;
            }
            String targetName = args[0];
            Player target = Bukkit.getPlayerExact(targetName);
            if (target == null) {
                String lower = targetName.toLowerCase();
                target = Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.getName().toLowerCase().startsWith(lower))
                        .findFirst().orElse(null);
            }
            if (target == null) {
                sender.sendMessage(color(plugin.getConfig().getString("messages.player_not_found", "Player not found.")));
                return true;
            }
            AfkManager manager = plugin.getAfkManager();
            if (manager.isAfk(target)) {
                String alreadyOther = plugin.getConfig().getString("messages.afk_already_other", "%target% is already AFK.");
                sender.sendMessage(color(alreadyOther.replace("%target%", target.getName())));
                return true;
            }
            manager.setAfk(target, true, AfkManager.AfkReason.MANUAL);
            String msg = plugin.getConfig().getString("messages.afk_set_other", "%target% has been set AFK.");
            sender.sendMessage(color(msg.replace("%target%", target.getName())));
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String partial = (args.length > 0 ? args[0] : "").toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(partial))
                .collect(Collectors.toList());
    }

    // color() centralized in ChatUtil
}