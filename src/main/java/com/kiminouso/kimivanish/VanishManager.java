package com.kiminouso.kimivanish;

import org.bukkit.entity.Player;

import java.util.*;

public class VanishManager {
    public final TreeMap<Integer, List<Player>> vanishLevels = new TreeMap<>();
    public final Set<UUID> currentlyVanished = new HashSet<>();
    public final Set<UUID> canVanish = new HashSet<>();
    public final Set<UUID> interactPlayers = new HashSet<>();
    public final Set<UUID> notifyPlayers = new HashSet<>();
    public final Set<UUID> itemPlayers = new HashSet<>();

    public void addPlayer(Player player, int level) {
        vanishLevels.compute(level,(key, value) -> {
            if (value == null) {
                value = new ArrayList<>();
            }
            value.add(player);
            return value;
        });

        vanishLevels.headMap(level,true).values().forEach(sublist -> sublist.forEach(p -> player.showPlayer(KimiVanish.getPlugin(KimiVanish.class), p)));
    }

    public void removePlayer(Player player) {
        vanishLevels.values().forEach(sublist -> sublist.remove(player));
    }

    public boolean isVanished(Player player) {
        return currentlyVanished.contains(player.getUniqueId());
    }
}

