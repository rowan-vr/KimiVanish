package com.kiminouso.kimivanish;

import com.kiminouso.kimivanish.commands.HideManager;
import com.kiminouso.kimivanish.commands.VanishCommand;
import com.kiminouso.kimivanish.commands.subcommands.settings.InteractSettingCommand;
import com.kiminouso.kimivanish.commands.subcommands.settings.ItemSettingCommand;
import com.kiminouso.kimivanish.commands.subcommands.settings.NotifySettingCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class KimiVanish extends JavaPlugin {
    @Getter private Storage storage;
    @Getter private VanishedPlayer vanishedPlayer;
    @Getter private HideManager hideManager;

    private VanishCommand vanishCommand;
    private ItemSettingCommand itemSettingCommand;
    private NotifySettingCommand notifySettingCommand;
    private InteractSettingCommand interactSettingCommand;


    @Override
    public void onEnable() {
        vanishedPlayer = new VanishedPlayer();
        vanishCommand = new VanishCommand();
        hideManager = new HideManager();
        itemSettingCommand = new ItemSettingCommand();
        notifySettingCommand = new NotifySettingCommand();
        interactSettingCommand = new InteractSettingCommand();
        storage = new Storage(this);

        Bukkit.getPluginCommand("vanish").setExecutor(this.vanishCommand);
        Bukkit.getPluginCommand("vanish").setTabCompleter(this.vanishCommand);
        Bukkit.getServer().getPluginManager().registerEvents(this.itemSettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(this.notifySettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(this.interactSettingCommand, this);

        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getServer().getLogger().log(Level.INFO, "PlaceholderAPI has been found! Enabling placeholder expansion...");
            new VanishPlaceholder(this).register();
        } else {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not find PlaceholderAPI! Placeholder expansion is disabled.");
        }

        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null) {
            Bukkit.getServer().getLogger().log(Level.INFO, "Essentials has been found! Enabling Essentials vanish hook...");
        } else {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not find Essentials! Essentials hook is disabled.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // TODO: Add boss bar task
}
