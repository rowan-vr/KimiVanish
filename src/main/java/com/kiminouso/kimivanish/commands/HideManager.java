package com.kiminouso.kimivanish.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.kiminouso.kimivanish.KimiVanish;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HideManager implements Listener {
    private final BossBar vanishedBossBar = Bukkit.createBossBar("§c§lYOU ARE IN VANISH", BarColor.WHITE, BarStyle.SOLID);

    public void VanishPlayer(Player player) {
        Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(KimiVanish.getPlugin(KimiVanish.class), player));
        vanishedBossBar.addPlayer(player);

        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().vanishLevels.tailMap(checkLevel(player),true).values().forEach(sublist -> sublist.forEach(p -> player.showPlayer(KimiVanish.getPlugin(KimiVanish.class), p)));
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().currentlyVanished.add(player.getUniqueId());

        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null) {
            Essentials essentials = Essentials.getPlugin(Essentials.class);
            User essentialsUser = essentials.getUser(player.getUniqueId());
            essentialsUser.setHidden(true);
        }
    }

    public void RemoveVanishStatus(Player player) {
        Bukkit.getOnlinePlayers().stream().filter(viewer -> viewer != player).forEach(viewer -> viewer.showPlayer(KimiVanish.getPlugin(KimiVanish.class), player));
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().removePlayer(player);
        vanishedBossBar.removePlayer(player);

        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().currentlyVanished.remove(player.getUniqueId());

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
}
