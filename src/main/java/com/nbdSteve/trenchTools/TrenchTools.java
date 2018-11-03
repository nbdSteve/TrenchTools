package com.nbdSteve.trenchTools;

import com.nbdSteve.trenchTools.command.TrenchCommand;
import com.nbdSteve.trenchTools.event.BlockBreak;
import com.nbdSteve.trenchTools.event.gui.GuiClick;
import com.nbdSteve.trenchTools.file.LoadProvidedFiles;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TrenchTools extends JavaPlugin {
    //Economy variable for the plugin
    private static Economy econ;
    //New LoadProvidedFiles instance
    private LoadProvidedFiles lpf;

    @Override
    public void onEnable() {
        getLogger().info("Thanks for using TrenchTools - nbdSteve");
        if (!setupEconomy()) {
            getLogger().severe("Vault.jar not found, disabling economy features.");
        }
        //Generate all of the provided files for the plugin
        this.lpf = new LoadProvidedFiles();
        //Register the commands for the plugin
        getCommand("trench").setExecutor(new TrenchCommand(this));
        //Register the events for the plugin
        getServer().getPluginManager().registerEvents(new GuiClick(), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Thanks for using TrenchTools - nbdSteve");
    }

    public LoadProvidedFiles getFiles() {
        return lpf;
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
