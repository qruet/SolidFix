package dev.qruet.solidfix.api;

import dev.qruet.solidfix.SolidFix;
import dev.qruet.solidfix.SolidServer;
import dev.qruet.solidfix.config.ConfigData;
import dev.qruet.solidfix.player.SolidMiner;
import dev.qruet.solidfix.utils.BlockUpdateUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

/**
 * The SolidFix resource API with relatively basic manipulation of the
 * plugin's mechanics as well as methods to retrieve data from the plugin's
 * mechanics for you to use in your own resource.
 *
 * @author qruet
 * @version 1.5.0
 */

public class SolidFixAPI {

    private static final SolidFix plugin;

    static {
        plugin = JavaPlugin.getPlugin(SolidFix.class);
    }

    /**
     * Retrieves values stored within the config.yml
     *
     * @param data Data type
     * @return Associated value
     */
    public Object getSolidData(ConfigData data) {
        return JavaPlugin.getPlugin(SolidFix.class).getConfig().get(data.getPath());
    }

    /**
     * Retrieves a collection of SolidMiner instances
     *
     * @return SolidMiners
     */
    public Collection<SolidMiner> getSolidPlayers() {
        return SolidServer.getOnlinePlayers();
    }

    /**
     * @param radius An int number of blocks from the player's location.
     * @param debug  Allow method to print stacktrace error if anything goes wrong?
     *               (Default True)
     * @return Returns true if execution was successful.
     */
    public boolean updateNearbyBlocks(Player player, int radius, boolean debug) {
        SolidMiner miner = SolidServer.getMiner(player.getUniqueId());
        Validate.notNull(miner, "Failed to retrieve SolidMiner instance for player, " + player.getName() + "!");
        try {
            BlockUpdateUtil.updateBlocksAsync(miner, radius);
        } catch (Exception e) {
            if (debug) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    /**
     * @param radius An int number of blocks from the player's location.
     * @return Returns true if execution was successful.
     */
    public boolean updateNearbyBlocks(Player player, int radius) {
        try {
            updateNearbyBlocks(player, radius, true);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
