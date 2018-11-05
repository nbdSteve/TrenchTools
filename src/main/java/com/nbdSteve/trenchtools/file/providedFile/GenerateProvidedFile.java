package com.nbdsteve.trenchtools.file.providedfile;

import com.nbdsteve.trenchtools.TrenchTools;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class to generate a file given its name, the file must already be in the resources section.
 * This class loads files from there.
 */
public class GenerateProvidedFile {
    //Register the main class
    private Plugin pl = TrenchTools.getPlugin(TrenchTools.class);
    //YAML configuration for the file
    private YamlConfiguration yamlFile;
    //Store the file name for later use
    private String fileName;
    //File to be created
    private File file;

    /**
     * Generates the provided yml file, the filename must be that of a file in the resources folder.
     *
     * @param fileName the name of the file being generated
     */
    public GenerateProvidedFile(String fileName) {
        file = new File(pl.getDataFolder(), fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            pl.saveResource(fileName, false);
            pl.getLogger().info("The supplied file " + fileName + " was not found, creating it now.");
        }
        yamlFile = new YamlConfiguration();
        try {
            yamlFile.load(file);
        } catch (InvalidConfigurationException e) {
            pl.getLogger().severe("The supplied file " + fileName +
                    " is not in the correct format, please contact the developer. Disabling the plugin");
            pl.getServer().getPluginManager().disablePlugin(pl);
        } catch (FileNotFoundException e) {
            pl.getLogger().severe("The supplied file " + fileName +
                    " was not found, please contact the developer. Disabling the plugin.");
            pl.getServer().getPluginManager().disablePlugin(pl);
        } catch (IOException e) {
            pl.getLogger().severe("The supplied file " + fileName +
                    " could not be loaded, please contact the developer. Disabling the plugin.");
            pl.getServer().getPluginManager().disablePlugin(pl);
        }
        //Instance variables
        this.fileName = fileName;
    }

    /**
     * reload the file, after the yml has been edited
     */
    public void reload() {
        try {
            yamlFile.load(file);
        } catch (InvalidConfigurationException e) {
            pl.getLogger().severe("The supplied file " + fileName +
                    " is not in the correct format, please contact the developer. Disabling the plugin");
            pl.getServer().getPluginManager().disablePlugin(pl);
        } catch (FileNotFoundException e) {
            pl.getLogger().severe("The supplied file " + fileName +
                    " was not found, please contact the developer. Disabling the plugin.");
            pl.getServer().getPluginManager().disablePlugin(pl);
        } catch (IOException e) {
            pl.getLogger().severe("The supplied file " + fileName +
                    " could not be loaded, please contact the developer. Disabling the plugin.");
            pl.getServer().getPluginManager().disablePlugin(pl);
        }
    }

    /**
     * get the yaml configuration for the file
     *
     * @return yaml configuration
     */
    public FileConfiguration get() {
        return yamlFile;
    }
}