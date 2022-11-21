package com.kiminouso.kimivanish;

import com.kiminouso.kimivanish.listeners.VanishStatusUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishListeners implements Listener {
    private final Set<UUID> recentlyUnhidden = new HashSet<>(); // Keeps recently unvanished players for 5 seconds.

    @EventHandler
    private void onVanish(VanishStatusUpdateEvent event) {
        if (KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.protect-recently-unvanished")) {
            if (recentlyUnhidden.contains(event.getPlayer().getUniqueId()))
                return;

            if (event.isVanished())
                return;

            recentlyUnhidden.add(event.getPlayer().getUniqueId());
            Bukkit.getScheduler().runTaskLater(KimiVanish.getPlugin(KimiVanish.class), () -> recentlyUnhidden.remove(event.getPlayer().getUniqueId()), 5 * 20L);
        }
    }

    @EventHandler
    private void onVanishPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("kimivanish.hide-onjoin")) {
            KimiVanish.getPlugin(KimiVanish.class).getHideManager().VanishPlayer(event.getPlayer());
        }
    }

    @EventHandler
    private void onVanishPlayerDamage(EntityDamageEvent event) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.protect-players"))
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        if (KimiVanish.getPlugin(KimiVanish.class).getVanishManager().currentlyVanished.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onRecentlyUnvanishedPlayerDamage(EntityDamageEvent event) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.protect-recently-unvanished"))
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        if (recentlyUnhidden.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onJoinMessage(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("kimivanish.hide")
        && KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.hide-connection-message")) {
            event.setJoinMessage("");
        }
    }

    @EventHandler
    private void onLeaveMessage(PlayerQuitEvent event) {
        if (event.getPlayer().hasPermission("kimivanish.hide")
                && KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.hide-connection-message")) {
            event.setQuitMessage("");
        }
    }

    @EventHandler
    private void onKickMessage(PlayerKickEvent event) {
        if (event.getPlayer().hasPermission("kimivanish.hide")
                && KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.hide-connection-message")) {
            event.setLeaveMessage("");
        }
    }

    @EventHandler
    private void onVanishHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        if (KimiVanish.getPlugin(KimiVanish.class).getVanishManager().currentlyVanished.contains(player.getUniqueId())
                && KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.prevent-hunger-loss")) {
            event.setCancelled(true);
        }
    }
}
