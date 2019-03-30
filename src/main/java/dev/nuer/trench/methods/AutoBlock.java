package dev.nuer.trench.methods;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class containing the methods to change gems into blocks
 */
public class AutoBlock {
    /**
     * Void method to group gems into blocks if the player is mining them
     *
     * @param p the player who is mining
     */
    public AutoBlock(Player p) {
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
