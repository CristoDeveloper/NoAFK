package com.noafk.listener;

import com.noafk.NoAFKPlugin;
import com.noafk.afk.AfkManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListener implements Listener {
    private final NoAFKPlugin plugin;

    public MovementListener(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getAfkManager().isMovementEnabled()) return;

        // If not actually moving, ignore
        if (event.getFrom().distanceSquared(event.getTo()) < 0.0001) return;

        Player player = event.getPlayer();
        AfkManager manager = plugin.getAfkManager();

        if (manager.isAfk(player)) {
            // Exit AFK on movement if exceeds block threshold
            double dist = manager.distanceFromAfkStart(player);
            if (dist >= manager.getMovementBlocks()) {
                manager.setAfk(player, false, AfkManager.AfkReason.MOVEMENT_EXIT);
            }
        } else {
            // Update activity to prevent AFK entry
            manager.markActivity(player);
        }
    }
}