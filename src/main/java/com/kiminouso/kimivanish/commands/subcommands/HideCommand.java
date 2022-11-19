package com.kiminouso.kimivanish.commands.subcommands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.kiminouso.kimivanish.KimiVanish;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HideCommand extends TippieCommand {

    public HideCommand() {
        super.subLevel = 1;
        super.name = "hide";
        super.prefix = "§6[§3KimiVanish§6]§r";
        super.description = "Hide yourself from other players";
        super.permission = "kimivanish.hide";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        if (KimiVanish.getPlugin(KimiVanish.class).getVanishedPlayer().vanishedPlayers.containsKey(player.getUniqueId())) {
            sender.sendMessage("Unvanished");
            KimiVanish.getPlugin(KimiVanish.class).getHideManager().RemoveVanishStatus(player);
        } else {
            sender.sendMessage("Vanished");
            KimiVanish.getPlugin(KimiVanish.class).getHideManager().VanishPlayer(player);
        }
    }
}
