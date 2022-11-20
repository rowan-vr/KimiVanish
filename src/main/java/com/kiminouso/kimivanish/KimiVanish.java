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
    @Getter
    private Storage storage;
    @Getter
    private VanishManager vanishManager;
    @Getter
    private HideManager hideManager;

    private VanishCommand vanishCommand;
    private ItemSettingCommand itemSettingCommand;
    private NotifySettingCommand notifySettingCommand;
    private InteractSettingCommand interactSettingCommand;


    @Override
    public void onEnable() {
        vanishManager = new VanishManager();
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
        Bukkit.getServer().getPluginManager().registerEvents(this.hideManager, this);

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

        Bukkit.getScheduler().runTaskTimer(this, () -> vanishManager.canVanish.forEach(player -> {
            int level = hideManager.checkLevel(Bukkit.getPlayer(player));
            vanishManager.vanishLevels.tailMap(level, true)
                    .values()
                    .forEach(sublist -> sublist.forEach(p -> Bukkit.getPlayer(player).showPlayer(this, p))
                    );
        }), 0L, 1L);
    }

    @Override
    public void onDisable() {
        hideManager.unhideAll("You have been unvanished due to a reload/restart.");
    }
}
