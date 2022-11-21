package com.kiminouso.kimivanish.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.listeners.VanishStatusUpdateEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class HideManager implements Listener {
    private final BossBar vanishedBossBar = Bukkit.createBossBar(ConfigUtils.getMessage("messages.vanish.bossbar", false), BarColor.WHITE, BarStyle.SOLID);

    public void VanishPlayer(Player player) {
        Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(KimiVanish.getPlugin(KimiVanish.class), player));
        AddToBossBar(player, true);

        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().vanishLevels.tailMap(checkLevel(player), true).values().forEach(sublist -> sublist.forEach(p -> player.showPlayer(KimiVanish.getPlugin(KimiVanish.class), p)));
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().currentlyVanished.add(player.getUniqueId());

        VanishStatusUpdateEvent event = new VanishStatusUpdateEvent(player, checkLevel(player), true);
        Bukkit.getPluginManager().callEvent(event);

        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null) {
            Essentials essentials = Essentials.getPlugin(Essentials.class);
            User essentialsUser = essentials.getUser(player.getUniqueId());
            essentialsUser.setHidden(true);
        }

        KimiVanish.getPlugin(KimiVanish.class).getStorage().findVanishUser(player.getUniqueId()).thenAccept(entry -> {
            if (!entry.isEmpty())
                return;

            KimiVanish.getPlugin(KimiVanish.class).getStorage().registerVanishUser(player.getUniqueId(), false, false, false, false, false);
        });
    }

    private void AddToBossBar(Player player, boolean shouldAdd) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.bossbar"))
            return;

        if (shouldAdd) {
            vanishedBossBar.addPlayer(player);
        } else {
            vanishedBossBar.removePlayer(player);
        }
    }

    public void RemoveVanishStatus(Player player) {
        Bukkit.getOnlinePlayers().stream().filter(viewer -> viewer != player).forEach(viewer -> viewer.showPlayer(KimiVanish.getPlugin(KimiVanish.class), player));
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().removePlayer(player);
        AddToBossBar(player, false);

        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().currentlyVanished.remove(player.getUniqueId());
        VanishStatusUpdateEvent event = new VanishStatusUpdateEvent(player, checkLevel(player), false);
        Bukkit.getPluginManager().callEvent(event);

        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null) {
            Essentials essentials = Essentials.getPlugin(Essentials.class);
            User essentialsUser = essentials.getUser(player.getUniqueId());
            essentialsUser.setHidden(false);
        }
    }

    public int checkLevel(Player player) {
        return player.getEffectivePermissions().stream()
                .filter(perm -> perm.getPermission().startsWith("kimivanish.level."))
                .map(perm -> perm.getPermission().replace("kimivanish.level.", ""))
                .mapToInt(permInt -> Integer.parseInt(permInt))
                .max().orElse(0);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().addPlayer(event.getPlayer(), checkLevel(event.getPlayer()));
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().currentlyVanished.forEach(uuid -> {
            event.getPlayer().hidePlayer(KimiVanish.getPlugin(KimiVanish.class), Bukkit.getPlayer(uuid));
        });

        if (event.getPlayer().hasPermission("kimivanish.hide")) {
            KimiVanish.getPlugin(KimiVanish.class).getVanishManager().canVanish.add(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().removePlayer(event.getPlayer());
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().canVanish.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onVanish(VanishStatusUpdateEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getStorage().findVanishUser(event.getPlayer().getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty() || !entry.get(0).notifySetting())
                return;

            KimiVanish.getPlugin(KimiVanish.class).getVanishManager().notifyPlayers.forEach(p -> {
                Player player = Bukkit.getPlayer(p);
                if (player == null)
                    return;

                if (event.isVanished()) {
                    player.sendMessage(ConfigUtils.getMessage("messages.vanish.notify.player-unvanished", player, player.getName()));
                } else {
                    player.sendMessage(ConfigUtils.getMessage("messages.vanish.notify.player-vanished", player, player.getName(), String.valueOf(checkLevel(player))));
                }
            });
        });
    }

    public void unhideAll() {
        for (UUID uuid : KimiVanish.getPlugin(KimiVanish.class).getVanishManager().currentlyVanished) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null)
                return;

            RemoveVanishStatus(player);
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.unhide-all", false));
        }
    }

    private final Runnable actionBarTask = () -> KimiVanish.getPlugin(KimiVanish.class).getVanishManager().currentlyVanished.forEach(uuid -> {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            return;

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ConfigUtils.getMessage("messages.vanish.bossbar", false)));
    });

    private BukkitTask activeTask = null;

    public void start() {
        if (activeTask != null)
            activeTask.cancel();

        activeTask = Bukkit.getScheduler().runTaskTimer(KimiVanish.getPlugin(KimiVanish.class), actionBarTask, 0, 20L);
    }

    public void end() {
        if (activeTask != null) {
            activeTask.cancel();
            activeTask = null;
        }
    }

    public boolean isActive() {
        return activeTask != null;
    }
}
