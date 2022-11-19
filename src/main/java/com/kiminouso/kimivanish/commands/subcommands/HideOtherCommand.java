package com.kiminouso.kimivanish.commands.subcommands;

import com.kiminouso.kimivanish.KimiVanish;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HideOtherCommand extends TippieCommand {
    public HideOtherCommand() {
        super.subLevel = 1;
        super.name = "hideother";
        super.prefix = "§6[§3KimiVanish§6]§r";
        super.description = "Hide someone else from other players";
        super.permission = "kimivanish.hide.others";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage("Player couldn't be found");
            return;
        }

        if (KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().vanishedPlayers.containsKey(player.getUniqueId())) {
            sender.sendMessage("Unvanished " + player.getName());
            KimiVanish.getPlugin(KimiVanish.class).getHideManager().RemoveVanishStatus(player);
        } else {
            sender.sendMessage("Vanished " + player.getName());
            KimiVanish.getPlugin(KimiVanish.class).getHideManager().VanishPlayer(player);
        }
    }
}
