package com.kiminouso.kimivanish.commands;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.commands.subcommands.HideCommand;
import com.kiminouso.kimivanish.commands.subcommands.HideOtherCommand;
import com.kiminouso.kimivanish.commands.subcommands.ListCommand;
import com.kiminouso.kimivanish.commands.subcommands.SettingsCommand;
import me.tippie.tippieutils.commands.TippieCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends TippieCommand {
    public VanishCommand(){
        super.prefix = ConfigUtils.getMessage("prefix", null);
        super.name = "vanish";
        super.getSubCommands().add(new HideCommand());
        super.getSubCommands().add(new HideOtherCommand());
        super.getSubCommands().add(new SettingsCommand());
        super.getSubCommands().add(new ListCommand());
    }

    @Override
    protected void sendHelpMessage(CommandSender sender, String label, String prefix) {
        sender.sendMessage(prefix + "§e Commands");

        if (!(sender instanceof Player player))
            return;

        getSubCommands().forEach(cmd -> {
            if (!player.hasPermission(cmd.getPermission()))
                return;

            TextComponent helpMessage = new TextComponent(ConfigUtils.getMessage("messages.vanish.help", false));
            helpMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to execute command.")));
            helpMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " " + cmd.getName()));
            player.spigot().sendMessage(helpMessage);
        });
    }
}
