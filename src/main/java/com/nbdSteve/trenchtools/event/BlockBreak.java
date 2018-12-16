package com.nbdsteve.trenchtools.event;

import com.nbdsteve.trenchtools.TrenchTools;
import com.nbdsteve.trenchtools.file.LoadProvidedFiles;
import com.nbdsteve.trenchtools.support.Factions;
import com.nbdsteve.trenchtools.support.MassiveCore;
import com.nbdsteve.trenchtools.support.WorldGuard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Event called when the player breaks a block, most of the code is not executed unless they are using the
 * tool. The tool check is done first to reduce memory usage.
 */
public class BlockBreak implements Listener {
    //Register the main class
    private Plugin pl = TrenchTools.getPlugin(TrenchTools.class);
    //Register LoadProvidedFiles class
    private LoadProvidedFiles lpf = ((TrenchTools) pl).getFiles();

    /**
     * All code for the event is stored in this method.
     *
     * @param e the event, cannot be null.
     */
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        //Get the player
        Player p = e.getPlayer();
        //Check that the player has the trenchtool in their hand
        if (p.getInventory().getItemInHand().hasItemMeta()) {
            if (p.getInventory().getItemInHand().getItemMeta().hasLore()) {
                ItemMeta toolMeta = p.getInventory().getItemInHand().getItemMeta();
                List<String> toolLore = toolMeta.getLore();
                String toolType;
                //Get the level of trench from the tool lore
                if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString("trench-tool-1.unique")))) {
                    toolType = "trench-tool-1";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString("trench-tool-2.unique")))) {
                    toolType = "trench-tool-2";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString("trench-tool-3.unique")))) {
                    toolType = "trench-tool-3";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString("trench-tool-4.unique")))) {
                    toolType = "trench-tool-4";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString("trench-tool-5.unique")))) {
                    toolType = "trench-tool-5";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString("trench-tool-6.unique")))) {
                    toolType = "trench-tool-6";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString("trench-tool-7.unique")))) {
                    toolType = "trench-tool-7";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString("trench-tool-8.unique")))) {
                    toolType = "trench-tool-8";
                } else if (toolLore.contains(
                        ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString("trench-tool-9.unique")))) {
                    toolType = "trench-tool-9";
                } else {
                    return;
                }
                boolean wg = false;
                boolean fac = false;
                //Figure out which plugins are being used and what to support
                if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
                    wg = true;
                    if (!WorldGuard.allowsBreak(e.getBlock().getLocation())) {
                        e.setCancelled(true);
                        return;
                    }
                }
                if (Bukkit.getPluginManager().getPlugin("MassiveCore") != null) {
                    MassiveCore.canBreakBlock(p, e.getBlock());
                    fac = true;
                    if (!MassiveCore.canBreakBlock(p, e.getBlock())) {
                        e.setCancelled(true);
                        return;
                    }
                } else if (Bukkit.getServer().getPluginManager().getPlugin("Factions") != null) {
                    fac = true;
                    if (!Factions.canBreakBlock(p, e.getBlock())) {
                        e.setCancelled(true);
                        return;
                    }
                }
                //Store the break radius for that tool
                int y = -(lpf.getTrench().getInt(toolType + ".radius"));
                int z = -(lpf.getTrench().getInt(toolType + ".radius"));
                int x = -(lpf.getTrench().getInt(toolType + ".radius"));
                int rad = lpf.getTrench().getInt(toolType + ".radius");
                List<String> blocks = new ArrayList<String>();
                String bmID = ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString(toolType + ".blocks-mined-unique-line-id"));
                //Store the blacklisted blocks if that is enabled
                if (lpf.getConfig().getBoolean("enable-block-blacklist")) {
                    for (String line : lpf.getTrench().getStringList("blacklisted-block-list")) {
                        String block = line.toUpperCase();
                        blocks.add(block);
                    }
                }
                //Run the while loop to remove the blocks
                while (y < (rad + 1)) {
                    while (z < (rad + 1)) {
                        while (x < (rad + 1)) {
                            String current = e.getBlock().getRelative(x, y, z).getType().toString();
                            //Check the world to see if the block is protected and shouldn't be broken
                            if (wg && !WorldGuard.allowsBreak(e.getBlock().getRelative(x, y, z).getLocation())) {
                                x++;
                            } else if (fac && !Factions.canBreakBlock(p, e.getBlock().getRelative(x, y, z))) {
                                x++;
                            } else if (blocks.contains(current)) {
                                e.setCancelled(true);
                                x++;
                            } else if (isInsideBorder(e.getBlock().getRelative(x, y, z), e)) {
                                x++;
                            } else if (lpf.getConfig().getBoolean("enable-natural-drops")) {
                                //Don't run this if the block is air, don't increment the block count for air
                                if (!e.getBlock().getRelative(x, y, z).getType().equals(Material.AIR)) {
                                    try {
                                        for (int i = 0; i < toolMeta.getLore().size(); i++) {
                                            String l = toolMeta.getLore().get(i);
                                            if (l.contains(bmID)) {
                                                String mined = "";
                                                for (int m = 0; m < toolLore.get(i).length(); m++) {
                                                    if (Character.isDigit(toolLore.get(i).charAt(m))) {
                                                        if (m != 0) {
                                                            if (toolLore.get(i).charAt(m - 1) != ChatColor.COLOR_CHAR) {
                                                                mined += toolLore.get(i).charAt(m);
                                                            }
                                                        } else {
                                                            mined += toolLore.get(i).charAt(m);
                                                        }
                                                    }
                                                }
                                                int temp = Integer.parseInt(mined) + 1;
                                                // Change the line of lore with the new number of blocks mined
                                                String bmI = ChatColor.translateAlternateColorCodes('&',
                                                        lpf.getTrench().getString(toolType + ".blocks-mined-increment-id")
                                                                .replace("%blocksMined%", String.valueOf(temp)));
                                                toolLore.set(i, (bmID + " " + bmI));
                                                // Update the lore of the players item
                                                toolMeta.setLore(toolLore);
                                                p.getItemInHand().setItemMeta(toolMeta);
                                            }
                                        }
                                    } catch (Exception ex) {
                                        //The pickaxe isn't tracking the number of blocks mined, do nothing
                                    }
                                }
                                e.getBlock().getRelative(x, y, z).breakNaturally();
                                x++;
                            } else {
                                for (ItemStack item : e.getBlock().getRelative(x, y, z).getDrops()) {
                                    p.getInventory().addItem(item);
                                }
                                if (!e.getBlock().getRelative(x, y, z).getType().equals(Material.AIR)) {

                                    try {
                                        for (int i = 0; i < toolMeta.getLore().size(); i++) {
                                            String l = toolMeta.getLore().get(i);
                                            if (l.contains(bmID)) {
                                                String mined = "";
                                                for (int m = 0; m < toolLore.get(i).length(); m++) {
                                                    if (Character.isDigit(toolLore.get(i).charAt(m))) {
                                                        if (m != 0) {
                                                            if (toolLore.get(i).charAt(m - 1) != ChatColor.COLOR_CHAR) {
                                                                mined += toolLore.get(i).charAt(m);
                                                            }
                                                        } else {
                                                            mined += toolLore.get(i).charAt(m);
                                                        }
                                                    }
                                                }
                                                int temp = Integer.parseInt(mined) + 1;
                                                //Change the line of lore with the new number of blocks mined
                                                String bmI = ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString(toolType + ".blocks-mined-increment-id").replace("%blocksMined%", String.valueOf(temp)));
                                                toolLore.set(i, (bmID + " " + bmI));
                                                //Update the lore of the players item
                                                toolMeta.setLore(toolLore);
                                                p.getItemInHand().setItemMeta(toolMeta);
                                            }
                                        }
                                    } catch (Exception ex) {
                                        //The pickaxe isn't tracking the number of blocks mined, do nothing
                                    }
                                }
                                e.getBlock().getRelative(x, y, z).setType(Material.AIR);
                                e.getBlock().getRelative(x, y, z).getDrops().clear();
                                x++;
                            }
                        }
                        x = -(lpf.getTrench().getInt(toolType + ".radius"));
                        z++;
                    }
                    z = -(lpf.getTrench().getInt(toolType + ".radius"));
                    y++;
                }
                if (lpf.getConfig().getBoolean("enable-auto-group")) {
                    //Grouping for diamond
                    if (p.getInventory().contains(Material.DIAMOND)) {
                        int amount = 0;
                        for (ItemStack item : p.getInventory().all(Material.DIAMOND).values()) {
                            amount += item.getAmount();
                        }
                        for (int i = 9; i < amount; amount -= 9) {
                            p.getInventory().removeItem(new ItemStack(Material.DIAMOND, 9));
                            p.getInventory().addItem(new ItemStack(Material.DIAMOND_BLOCK, 1));
                        }
                    }
                    //Grouping for redstone
                    if (p.getInventory().contains(Material.REDSTONE)) {
                        int amount = 0;
                        for (ItemStack item : p.getInventory().all(Material.REDSTONE).values()) {
                            amount += item.getAmount();
                        }
                        for (int i = 9; i < amount; amount -= 9) {
                            p.getInventory().removeItem(new ItemStack(Material.REDSTONE, 9));
                            p.getInventory().addItem(new ItemStack(Material.REDSTONE_BLOCK, 1));
                        }
                    }
                    //Grouping for coal
                    if (p.getInventory().contains(Material.COAL)) {
                        int amount = 0;
                        for (ItemStack item : p.getInventory().all(Material.COAL).values()) {
                            amount += item.getAmount();
                        }
                        for (int i = 9; i < amount; amount -= 9) {
                            p.getInventory().removeItem(new ItemStack(Material.COAL, 9));
                            p.getInventory().addItem(new ItemStack(Material.COAL_BLOCK, 1));
                        }
                    }
                }
            }
        }
    }

    /**
     * Method to check if a block is inside the world border or not
     *
     * @param b the block being checked
     * @param e the event it is in
     * @return boolean, true if the block is inside the border
     */
    private boolean isInsideBorder(Block b, BlockBreakEvent e) {
        //Get the worldborder
        WorldBorder wb = e.getBlock().getLocation().getWorld().getWorldBorder();
        //Store the blocks location
        int blockX = b.getX();
        int blockZ = b.getZ();
        //Get the actual worldborder size
        double size = wb.getSize() / 2;
        //Check if the block is inside, return true if it is
        if (blockX > 0 && blockX > size - 1) {
            return true;
        } else if (blockX < 0 && blockX < (size * -1)) {
            return true;
        } else if (blockZ > 0 && blockZ > size - 1) {
            return true;
        } else if (blockZ < 0 && blockZ < (size * -1)) {
            return true;
        }
        return false;
    }
}