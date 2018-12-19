package com.nbdSteve.trenchtools.event.gui;

import com.nbdSteve.trenchtools.file.LoadProvidedFiles;
import com.nbdSteve.trenchtools.TrenchTools;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Event called when the player clicks inside the Merchant Gui for the plugin.
 */
public class GuiClick implements Listener {
    //Register the main class
    private Plugin pl = TrenchTools.getPlugin(TrenchTools.class);
    //Register LoadProvideFiles class
    private LoadProvidedFiles lpf = ((TrenchTools) pl).getFiles();
    //Get the server economy
    private Economy econ = TrenchTools.getEconomy();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        //Store the player
        Player p = (Player) e.getWhoClicked();
        //Store the inventory
        Inventory inven = e.getClickedInventory();
        //Check that the inventory clicked was this inventory
        if (inven != null) {
            if (inven.getName()
                    .equals(ChatColor.translateAlternateColorCodes('&', lpf.getConfig().getString("gui.name")))) {
                e.setCancelled(true);
                //Store the details about the clicked item
                ItemMeta toolMeta = e.getCurrentItem().getItemMeta();
                List<String> toolLore = toolMeta.getLore();
                String toolType = null;
                String ttool = null;
                String perm = null;
                NumberFormat df = new DecimalFormat("#,###");
                //Check to see if it is a valid tool
                if (!toolMeta.getDisplayName().equalsIgnoreCase(" ")) {
                    for (int i = 1; i < 10; i++) {
                        String tool = "trench-tool-" + String.valueOf(i) + "-gui";
                        String t = "trench-tool-" + String.valueOf(i);
                        try {
                            lpf.getTrench().getString(tool + ".unique");
                            if (toolLore.contains(ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString(tool + ".unique")))) {
                                ttool = t;
                                perm = String.valueOf(i);
                                toolType = tool;
                            }
                        } catch (Exception ex) {
                            //Do nothing, this tool isn't active or doesn't exist
                        }
                    }
                    if (toolType == null) {
                        return;
                    }
                    //Check that the player has permission to buy that tool
                    if (p.hasPermission("trench.gui." + perm)) {
                        if (p.getInventory().firstEmpty() != -1) {
                            double price = lpf.getTrench().getDouble(toolType + ".price");
                            //Check that the player has enough money to buy the tool
                            if (econ.getBalance(p) >= price) {
                                econ.withdrawPlayer(p, price);
                                //Create the item being bought
                                ItemStack item = new ItemStack(
                                        Material.valueOf(lpf.getTrench().getString(toolType + ".gui-item").toUpperCase()));
                                ItemMeta itemMeta = item.getItemMeta();
                                List<String> itemLore = new ArrayList<>();
                                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                        lpf.getTrench().getString(ttool + ".name")));
                                for (String lore : lpf.getTrench().getStringList(ttool + ".lore")) {
                                    itemLore.add(ChatColor.translateAlternateColorCodes('&', lore)
                                            .replace("%blocksMined%", "0"));
                                }
                                for (String ench : lpf.getTrench().getStringList(ttool + ".enchantments")) {
                                    String[] parts = ench.split("-");
                                    itemMeta.addEnchant(Enchantment.getByName(parts[0]), Integer.parseInt(parts[1]),
                                            true);
                                }
                                itemMeta.setLore(itemLore);
                                item.setItemMeta(itemMeta);
                                //Give the player the item
                                p.getInventory().addItem(item);
                                String newPrice = df.format(price);
                                for (String line : lpf.getMessages().getStringList("purchase")) {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("%cost%",
                                            newPrice));
                                }
                                p.closeInventory();
                            } else {
                                String newPrice = df.format(price);
                                String newBal = df.format(econ.getBalance(p));

                                for (String line : lpf.getMessages().getStringList("insufficient-funds")) {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', line)
                                            .replace("%bal%", newBal)
                                            .replace("%cost%", newPrice));
                                }
                                p.closeInventory();
                            }
                        } else {
                            for (String line : lpf.getMessages().getStringList("inventory-full")) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                            }
                            p.closeInventory();
                        }
                    } else {
                        for (String line : lpf.getMessages().getStringList("no-buy-permission")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                        }
                        p.closeInventory();
                    }
                }
            }
        }
    }
}