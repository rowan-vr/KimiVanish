package com.kiminouso.kimivanish.commands.subcommands.settings;

import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.Storage;
import com.kiminouso.kimivanish.listeners.VanishStatusUpdateEvent;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class NotifySettingCommand extends TippieCommand implements Listener {
    public NotifySettingCommand() {
        super.subLevel = 2;
        super.name = "notify";
        super.prefix = "§6[§3KimiVanish§6]§r";
        super.description = "Toggle notifications for vanished players";
        super.permission = "kimivanish.settings.notify";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        Storage storage = KimiVanish.getPlugin(KimiVanish.class).getStorage();

        storage.findVanishUser(player.getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).notifySetting()) {
                player.sendMessage("Toggled notify for vanished players OFF");
                storage.setNotifySetting(player.getUniqueId(), false);
                KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().notifyPlayers.remove(player.getUniqueId());
            } else {
                player.sendMessage("Toggled notify for vanished players ON");
                storage.setNotifySetting(player.getUniqueId(), true);
                KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().notifyPlayers.add(player.getUniqueId());
            }
        });
    }

    @EventHandler
    private void onVanishStatusChange(VanishStatusUpdateEvent event) {
        if (!shouldNotify(event.getPlayer()))
            return;

        event.getPlayer().sendMessage(event.getPlayer().getName() + " vanished at level " + event.getVanishLevel());
    }

    private boolean shouldNotify(Player player) {
        return KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().notifyPlayers.contains(player.getUniqueId());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getStorage().findVanishUser(event.getPlayer().getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).notifySetting()) {
                KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().notifyPlayers.add(event.getPlayer().getUniqueId());
            }
        });
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().notifyPlayers.remove(event.getPlayer().getUniqueId());
    }
}
