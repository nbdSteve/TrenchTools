package dev.nuer.trench.event;

import dev.nuer.trench.TrenchTools;
import dev.nuer.trench.file.LoadProvidedFiles;
import dev.nuer.trench.methods.AutoBlock;
import dev.nuer.trench.methods.BlockWorldBorderCheck;
import dev.nuer.trench.methods.UpdateMinedBlocks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
        if (e.isCancelled()) {
            return;
        }
        //Get the player
        Player p = e.getPlayer();
        //Check that the player has the trenchtool in their hand
        if (p.getInventory().getItemInHand().hasItemMeta()) {
            if (p.getInventory().getItemInHand().getItemMeta().hasLore()) {
                ItemMeta toolMeta = p.getInventory().getItemInHand().getItemMeta();
                List<String> toolLore = toolMeta.getLore();
                String toolType = null;
                //Get the level of trench from the tool lore
                for (int i = 1; i < 10; i++) {
                    String tool = "trench-tool-" + i;
                    try {
                        lpf.getTrench().getString(tool + ".unique");
                        if (toolLore.contains(ChatColor.translateAlternateColorCodes('&', lpf.getTrench().getString(tool + ".unique")))) {
                            toolType = tool;
                        }
                    } catch (Exception ex) {
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
                            BlockPlaceEvent trenchRadiusBreak =
                                    new BlockPlaceEvent(e.getBlock().getRelative(x, y, z),
                                            e.getBlock().getRelative(x, y, z).getState(),
                                            e.getBlock().getRelative(x, y, z), p.getItemInHand(),
                                            p, false);
                            String current = trenchRadiusBreak.getBlock().getType().toString();
                            Bukkit.getPluginManager().callEvent(trenchRadiusBreak);
                            if (trenchRadiusBreak.isCancelled()) {
                            } else if (blocks.contains(current)) {
                                e.setCancelled(true);
                            } else if (BlockWorldBorderCheck.isInsideBorder(trenchRadiusBreak.getBlock(),
                                    trenchRadiusBreak, p)) {
                            } else if (lpf.getConfig().getBoolean("enable-natural-drops")) {
                                if (!trenchRadiusBreak.getBlockPlaced().getType().equals(Material.AIR)) {
                                    UpdateMinedBlocks.updateLore(toolMeta, toolLore, p,
                                            toolType, lpf, bmID);
                                }
                                trenchRadiusBreak.getBlock().breakNaturally();
                            } else {
                                for (ItemStack item : trenchRadiusBreak.getBlock().getDrops()) {
                                    p.getInventory().addItem(item);
                                }
                                if (!trenchRadiusBreak.getBlockPlaced().getType().equals(Material.AIR)) {
                                    UpdateMinedBlocks.updateLore(toolMeta, toolLore, p,
                                            toolType, lpf, bmID);
                                }
                                trenchRadiusBreak.getBlock().setType(Material.AIR);
                                trenchRadiusBreak.getBlock().getDrops().clear();
                            }
                            x++;
                        }
                        x = -(lpf.getTrench().getInt(toolType + ".radius"));
                        z++;
                    }
                    z = -(lpf.getTrench().getInt(toolType + ".radius"));
                    y++;
                }
                if (lpf.getConfig().getBoolean("enable-auto-group")) {
                    new AutoBlock(p);
                }
            }
        }
    }
}