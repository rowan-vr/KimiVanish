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
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;
import java.util.logging.Level;

public class HideManager implements Listener {
    private final Set<UUID> vanishLevelZero = new HashSet<>();
    private final Set<UUID> vanishLevelOne = new HashSet<>();
    private final Set<UUID> vanishLevelTwo = new HashSet<>();
    private final Set<UUID> vanishLevelThree = new HashSet<>();
    private final Set<UUID> vanishLevelFour = new HashSet<>();

    private BossBar vanishedBossBar = Bukkit.createBossBar("§c§lYOU ARE IN VANISH", BarColor.WHITE, BarStyle.SOLID);

    public void VanishPlayer(Player player) {
        // Hide player from everyone online and add to a list
        Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(KimiVanish.getPlugin(KimiVanish.class), player));
        KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().addPlayer(player, 1);
        vanishedBossBar.addPlayer(player);
        vanishLevelZero.add(player.getUniqueId());

        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null) {
            Essentials essentials = Essentials.getPlugin(Essentials.class);
            User essentialsUser = essentials.getUser(player.getUniqueId());
            essentialsUser.setHidden(true);
        }

        // Un-hide player from other staff members
        switch (checkLevel(player)) {
            case 1 -> {
                vanishLevelOne.add(player.getUniqueId());
                vanishLevelZero.add(player.getUniqueId());
            }
            case 2 -> {
                vanishLevelOne.add(player.getUniqueId());
                vanishLevelZero.add(player.getUniqueId());
                vanishLevelTwo.add(player.getUniqueId());
            }
            case 3 -> {
                vanishLevelThree.add(player.getUniqueId());
                vanishLevelTwo.add(player.getUniqueId());
                vanishLevelOne.add(player.getUniqueId());
                vanishLevelZero.add(player.getUniqueId());
            }
            case 4 -> {
                vanishLevelFour.add(player.getUniqueId());
                vanishLevelThree.add(player.getUniqueId());
                vanishLevelTwo.add(player.getUniqueId());
                vanishLevelOne.add(player.getUniqueId());
                vanishLevelZero.add(player.getUniqueId());
            }
            default -> vanishLevelZero.add(player.getUniqueId());
        }

        ShowPlayers(player, vanishLevelZero);
        ShowPlayers(vanishLevelZero, vanishLevelOne);
        ShowPlayers(player, vanishLevelOne);
        ShowPlayers(vanishLevelOne, vanishLevelTwo);
        ShowPlayers(player, vanishLevelTwo);
        ShowPlayers(vanishLevelTwo, vanishLevelThree);
        ShowPlayers(player, vanishLevelThree);
        ShowPlayers(vanishLevelThree, vanishLevelFour);
        ShowPlayers(player, vanishLevelFour);
    }

    private void ShowPlayers(Set<UUID> hidden, Set<UUID> viewer) {
        hidden.forEach(level -> viewer.forEach(targetLevel -> Bukkit.getPlayer(level).showPlayer(KimiVanish.getPlugin(KimiVanish.class), Bukkit.getPlayer(targetLevel))));
    }

    private void ShowPlayers(Player self, Set<UUID> others) {
        others.forEach(uuid -> {
            if (Bukkit.getPlayer(uuid) != self) {
                Bukkit.getPlayer(uuid).showPlayer(KimiVanish.getPlugin(KimiVanish.class), self);
            }
        });
    }

    public void RemoveVanishStatus(Player player) {
        Bukkit.getOnlinePlayers().stream().filter(viewer -> viewer != player).forEach(viewer -> viewer.showPlayer(KimiVanish.getPlugin(KimiVanish.class), player));
        KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().removePlayer(player);
        vanishedBossBar.removePlayer(player);
        vanishLevelZero.remove(player.getUniqueId());
        vanishLevelOne.remove(player.getUniqueId());
        vanishLevelTwo.remove(player.getUniqueId());
        vanishLevelThree.remove(player.getUniqueId());
        vanishLevelFour.remove(player.getUniqueId());

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
        KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().vanishedPlayers.keySet().forEach(key -> {
            Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(KimiVanish.getPlugin(KimiVanish.class), Bukkit.getPlayer(key)));
        });

        ShowPlayers(event.getPlayer(), vanishLevelZero);
        ShowPlayers(vanishLevelZero, vanishLevelOne);
        ShowPlayers(event.getPlayer(), vanishLevelOne);
        ShowPlayers(vanishLevelOne, vanishLevelTwo);
        ShowPlayers(event.getPlayer(), vanishLevelTwo);
        ShowPlayers(vanishLevelTwo, vanishLevelThree);
        ShowPlayers(event.getPlayer(), vanishLevelThree);
        ShowPlayers(vanishLevelThree, vanishLevelFour);
        ShowPlayers(event.getPlayer(), vanishLevelFour);
    }
}
