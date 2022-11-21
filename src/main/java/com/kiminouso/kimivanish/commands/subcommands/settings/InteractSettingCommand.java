package com.kiminouso.kimivanish.commands.subcommands.settings;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.Storage;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class InteractSettingCommand extends TippieCommand implements Listener {
    public InteractSettingCommand() {
        super.subLevel = 2;
        super.name = "interact";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "Toggle physical interactions for vanished players";
        super.permission = "kimivanish.settings.interact";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        Storage storage = KimiVanish.getPlugin(KimiVanish.class).getStorage();

        storage.findVanishUser(player.getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).interactSetting()) {
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.interact.off", player));
                storage.setInteractSetting(player.getUniqueId(), false);
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().interactPlayers.remove(player.getUniqueId());
            } else {
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.interact.on", player));
                storage.setInteractSetting(player.getUniqueId(), true);
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().interactPlayers.add(player.getUniqueId());
            }
        });
    }

    @EventHandler
    private void onPhysicalInteraction(PlayerInteractEvent event) {
        if (canInteract(event.getPlayer()))
            return;

        if (KimiVanish.getPlugin(KimiVanish.class).getVanishManager().isVanished(event.getPlayer())) {
            if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock() != null) {
                event.setCancelled(true);
            }
        }
    }

    private boolean canInteract(Player player) {
        return KimiVanish.getPlugin(KimiVanish.class).getVanishManager().interactPlayers.contains(player.getUniqueId());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getStorage().findVanishUser(event.getPlayer().getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).interactSetting()) {
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().interactPlayers.add(event.getPlayer().getUniqueId());
            }
        });
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().interactPlayers.remove(event.getPlayer().getUniqueId());
    }
}
