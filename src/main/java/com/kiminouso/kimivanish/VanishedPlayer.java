package com.kiminouso.kimivanish;

import org.bukkit.entity.Player;

import java.util.*;

public class VanishedPlayer {
    public final Map<UUID, Integer> vanishedPlayers = new HashMap<>();
    public final Set<UUID> interactPlayers = new HashSet<>();
    public final Set<UUID> notifyPlayers = new HashSet<>();
    public final Set<UUID> itemPlayers = new HashSet<>();

    public void addPlayer(Player player, int level) {
        vanishedPlayers.put(player.getUniqueId(), level);
    }

    public void removePlayer(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.containsKey(player.getUniqueId());
    }

    public boolean isHiddenFrom(Player player , Player other) {
        // TODO: Add logic
        return false;
    }
}

