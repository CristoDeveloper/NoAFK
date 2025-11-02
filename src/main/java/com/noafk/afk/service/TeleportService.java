package com.noafk.afk.service;

import com.noafk.NoAFKPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeleportService {
    private final NoAFKPlugin plugin;

    public TeleportService(NoAFKPlugin plugin) {
        this.plugin = plugin;
    }

    public void teleportIfEnabled(Player p) {
        boolean tpEnabled = plugin.getConfig().getBoolean("commands.teleport.enabled", false);
        if (!tpEnabled) return;

        String worldName = plugin.getConfig().getString("commands.teleport.world", p.getWorld().getName());
        double x = plugin.getConfig().getDouble("commands.teleport.x", p.getLocation().getX());
        double y = plugin.getConfig().getDouble("commands.teleport.y", p.getLocation().getY());
        double z = plugin.getConfig().getDouble("commands.teleport.z", p.getLocation().getZ());
        float yaw = (float) plugin.getConfig().getDouble("commands.teleport.yaw", p.getLocation().getYaw());
        float pitch = (float) plugin.getConfig().getDouble("commands.teleport.pitch", p.getLocation().getPitch());
        World w = Bukkit.getWorld(worldName);
        if (w == null) {
            plugin.getLogger().warning("World not found for teleport: " + worldName + ", using player's current world.");
            w = p.getWorld();
        }
        p.teleport(new Location(w, x, y, z, yaw, pitch));
    }
}