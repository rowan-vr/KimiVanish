package com.kiminouso.kimivanish.listeners;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class VanishStatusUpdateEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    @Getter private final int vanishLevel;

    public VanishStatusUpdateEvent(Player player, int level) {
        super(player);
        this.player = player;
        this.vanishLevel = level;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
