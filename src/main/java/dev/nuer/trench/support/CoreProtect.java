package dev.nuer.trench.support;

import net.coreprotect.CoreProtectAPI;
import org.bukkit.block.Block;

/**
 * CoreProtect support class
 */
public class CoreProtect {

    /**
     * Method to register the block being broken
     *
     * @param user String, the user
     * @param b Block, the block being broken
     */
    public static void registerBreak(String user, Block b) {
        CoreProtectAPI CoreProtect = net.coreprotect.CoreProtect.getInstance().getAPI();
        CoreProtect.logRemoval(user, b.getLocation(), b.getType(), b.getData());
    }
}
