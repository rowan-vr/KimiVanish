package com.kiminouso.kimivanish.listeners;

import com.kiminouso.kimivanish.KimiVanish;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().vanishedPlayers.keySet().forEach(p -> event.getPlayer().hidePlayer(KimiVanish.getPlugin(KimiVanish.class), Bukkit.getPlayer(p)));
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().vanishedPlayers.keySet().forEach(p -> event.getPlayer().showPlayer(KimiVanish.getPlugin(KimiVanish.class), Bukkit.getPlayer(p)));
    }
}
