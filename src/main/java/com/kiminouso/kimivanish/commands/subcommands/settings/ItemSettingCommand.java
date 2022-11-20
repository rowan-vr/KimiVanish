package com.kiminouso.kimivanish.commands.subcommands.settings;

import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.Storage;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

public class ItemSettingCommand extends TippieCommand implements Listener {
    public ItemSettingCommand() {
        super.subLevel = 2;
        super.name = "items";
        super.prefix = "§6[§3KimiVanish§6]§r";
        super.description = "Toggle item pick-up and drop for vanished players";
        super.permission = "kimivanish.settings.item";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        Storage storage = KimiVanish.getPlugin(KimiVanish.class).getStorage();

        storage.findVanishUser(player.getUniqueId()).thenAccept((entry) -> {
           if (entry.isEmpty())
               return;

           if (entry.get(0).itemSetting()) {
               player.sendMessage("Toggled item pick up and drop OFF");
               storage.setItemSetting(player.getUniqueId(), false);
               KimiVanish.getPlugin(KimiVanish.class).getVanishManager().itemPlayers.remove(player.getUniqueId());
           } else {
               player.sendMessage("Toggled item pick up and drop ON");
               storage.setItemSetting(player.getUniqueId(), true);
               KimiVanish.getPlugin(KimiVanish.class).getVanishManager().itemPlayers.add(player.getUniqueId());
           }
        });
    }

    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event) {
        if (canDropItem(event.getPlayer()))
            return;

        if (KimiVanish.getPlugin(KimiVanish.class).getVanishManager().isVanished(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    private void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        if (canDropItem(player))
            return;

        if (KimiVanish.getPlugin(KimiVanish.class).getVanishManager().isVanished(player))
            event.setCancelled(true);
    }

    private boolean canDropItem(Player player) {
        return KimiVanish.getPlugin(KimiVanish.class).getVanishManager().itemPlayers.contains(player.getUniqueId());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getStorage().findVanishUser(event.getPlayer().getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).itemSetting()) {
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().itemPlayers.add(event.getPlayer().getUniqueId());
            }
        });
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().itemPlayers.remove(event.getPlayer().getUniqueId());
    }
}
