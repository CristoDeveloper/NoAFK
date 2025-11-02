package com.noafk.listener;

import com.noafk.NoAFKPlugin;
import com.noafk.afk.AfkManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final NoAFKPlugin plugin;

    public ChatListener(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!plugin.getAfkManager().isChatEnabled()) return;
        Player player = event.getPlayer();
        AfkManager manager = plugin.getAfkManager();

        if (manager.isAfk(player)) {
            // exit AFK on chat
            manager.setAfk(player, false, AfkManager.AfkReason.CHAT_EXIT);
        } else {
            manager.markActivity(player);
        }
    }
}