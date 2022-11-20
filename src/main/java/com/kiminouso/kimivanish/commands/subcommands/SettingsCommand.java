package com.kiminouso.kimivanish.commands.subcommands;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.commands.subcommands.settings.InteractSettingCommand;
import com.kiminouso.kimivanish.commands.subcommands.settings.ItemSettingCommand;
import com.kiminouso.kimivanish.commands.subcommands.settings.NotifySettingCommand;
import me.tippie.tippieutils.commands.TippieCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand extends TippieCommand {
    public SettingsCommand() {
        super.subLevel = 1;
        super.name = "settings";
        super.prefix = ConfigUtils.getMessage("prefix", null);
        super.description = "Edit your vanish settings";
        super.permission = "kimivanish.settings";

        super.getSubCommands().add(new InteractSettingCommand());
        super.getSubCommands().add(new NotifySettingCommand());
        super.getSubCommands().add(new ItemSettingCommand());
    }

    protected void sendHelpMessage(CommandSender sender, String label, String prefix) {
        sender.sendMessage(prefix + "§e Setting Subcommands");

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
