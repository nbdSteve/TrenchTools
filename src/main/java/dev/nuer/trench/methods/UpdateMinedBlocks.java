package dev.nuer.trench.methods;

import dev.nuer.trench.file.LoadProvidedFiles;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class UpdateMinedBlocks {

    public static void updateLore(ItemMeta itemMeta, List<String> itemLore, Player player,
                                  String itemType, LoadProvidedFiles lpf, String bmID) {
        try {
            for (int i = 0; i < itemLore.size(); i++) {
                String l = itemLore.get(i);
                if (l.contains(bmID)) {
                    String mined = "";
                    for (int m = 0; m < itemLore.get(i).length(); m++) {
                        if (Character.isDigit(itemLore.get(i).charAt(m))) {
                            if (m != 0) {
                                if (itemLore.get(i).charAt(m - 1) != ChatColor.COLOR_CHAR) {
                                    mined += itemLore.get(i).charAt(m);
                                }
                            } else {
                                mined += itemLore.get(i).charAt(m);
                            }
                        }
                    }
                    int temp = Integer.parseInt(mined) + 1;
                    // Change the line of lore with the new number of blocks mined
                    String bmI = ChatColor.translateAlternateColorCodes('&',
                            lpf.getTrench().getString(itemType + ".blocks-mined-increment-id")
                                    .replaceAll("%blocksMined%", String.valueOf(temp)));
                    itemLore.set(i, (bmID + " " + bmI));
                    // Update the lore of the players item
                    itemMeta.setLore(itemLore);
                    player.getItemInHand().setItemMeta(itemMeta);
                }
            }
        } catch (Exception ex) {
            //The pickaxe isn't tracking the number of blocks mined, do nothing
        }
    }
}
