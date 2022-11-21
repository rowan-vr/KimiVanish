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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class NightvisionSettingCommand extends TippieCommand implements Listener {
    public NightvisionSettingCommand() {
        super.subLevel = 2;
        super.name = "nightvision";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "Toggle nightvision for vanish";
        super.permission = "kimivanish.settings.nightvision";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        Storage storage = KimiVanish.getPlugin(KimiVanish.class).getStorage();

        storage.findVanishUser(player.getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).nightVisionSetting()) {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                storage.setNightvisionSetting(player.getUniqueId(), false);
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.nightvision.off", player));
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true,false,false));
                storage.setNightvisionSetting(player.getUniqueId(), true);
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.nightvision.on", player));
            }
        });
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getStorage().findVanishUser(event.getPlayer().getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).nightVisionSetting()) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true,false,false));
            }
        });
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        if (event.getPlayer().hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            event.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

}
