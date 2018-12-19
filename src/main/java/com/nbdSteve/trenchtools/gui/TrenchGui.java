package com.nbdSteve.trenchtools.gui;

import com.nbdSteve.trenchtools.TrenchTools;
import com.nbdSteve.trenchtools.file.LoadProvidedFiles;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Merchant Gui class, this creates and adds the harvester tools to the Merchant Gui.
 */
public class TrenchGui {
    //Register the main class
    private Plugin pl = TrenchTools.getPlugin(TrenchTools.class);
    //Register LoadProvideFiles class
    private LoadProvidedFiles lpf = ((TrenchTools) pl).getFiles();

    /**
     * Method to create the gui whenever the player runs the /trench command
     *
     * @param p player, cannot be null
     */
    public void gui(Player p) {
        // Creating the inventory with the name & size from the config.yml.
        Inventory i = pl.getServer().createInventory(null, lpf.getConfig().getInt("gui.size"),
                ChatColor.translateAlternateColorCodes('&', lpf.getConfig().getString("gui.name")));
        String[] parts = lpf.getConfig().getString("gui.fill-item").split("-");
        int b = Integer.parseInt(parts[1]);
        // Creating the item that will fill all of the free slots in the GUI.
        ItemStack f1 = new ItemStack(Material.valueOf(parts[0].toUpperCase()),
                lpf.getConfig().getInt("gui.fill-item-amount"), (byte) b);
        ItemMeta f1M = f1.getItemMeta();
        f1M.setDisplayName(" ");
        // Setting if the fill item will be glowing or not.
        if (lpf.getConfig().getBoolean("gui.fill-item-glowing")) {
            f1M.addEnchant(Enchantment.LURE, 1, true);
            f1M.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        f1.setItemMeta(f1M);
        //Fill the rest of the GUI with the filler item
        for (int z = 0; z < lpf.getConfig().getInt("gui.size"); z++) {
            i.setItem(z, f1);
        }
        //Add all of the tools to the inventory
        for (int x = 1; x < 10; x++) {
            String tool = "trench-tool-" + String.valueOf(x) + "-gui";
            if (lpf.getTrench().getBoolean(tool + ".enabled")) {
                //Create the tool
                ItemStack ttool = new ItemStack(
                        Material.valueOf(lpf.getTrench().getString(tool + ".gui-item").toUpperCase()), 1);
                ItemMeta ttoolMeta = ttool.getItemMeta();
                List<String> ttoolLore = new ArrayList<String>();

                ttoolMeta.setDisplayName(
                        ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString(tool + ".name")));
                for (String lore : lpf.getTrench().getStringList(tool + ".lore")) {
                    ttoolLore.add(ChatColor.translateAlternateColorCodes('&', lore).replace("%blocksMined%", "0"));
                }
                for (String ench : lpf.getTrench().getStringList(tool + ".enchantments")) {
                    String[] enchs = ench.split("-");
                    ttoolMeta.addEnchant(Enchantment.getByName(enchs[0]), Integer.parseInt(enchs[1]), true);
                }
                ttoolMeta.setLore(ttoolLore);
                ttool.setItemMeta(ttoolMeta);
                //Add it to the gui
                i.setItem(lpf.getTrench().getInt(tool + ".gui-slot"), ttool);
            } else {

            }
        }
        p.openInventory(i);
    }
}