package me.geekles.blockglitchfix.mechanism;

import me.geekles.blockglitchfix.BlockGlitchFix;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;

/**
 * Responsible for updating a player's client with new block packets
 * @author geekles
 * @version 1.6.6
 */
public class GlitchMechanic {

    private static JavaPlugin plugin;

    public static void init(BlockGlitchFix plugin) {
        GlitchMechanic.plugin = plugin;
    }

    /**
     * Updates the players with blocks that surround them using packets.
     *
     * @param player Online player
     * @param radius Update radius
     */
    public static void updateBlocks(Player player, int radius) {
        new BukkitRunnable() {
            public void run() {
                Location pLoc = player.getLocation();
                World w = pLoc.getWorld();

                Location source = pLoc.getBlock().getLocation();
                Location center = new Location(w, source.getX() - radius, source.getY() - radius, source.getZ() - radius);
                new BukkitRunnable() {
                    public void run() {
                        LinkedList<Location> LocationUpdateList = new LinkedList<>();
                        for (int x = (int) center.getX(); x <= center.getX() + (radius * 2); x++) {
                            for (int y = (int) center.getY(); y <= center.getY() + (radius * 2); y++) {
                                for (int z = (int) center.getZ(); z <= center.getZ() + (radius * 2); z++) {
                                    LocationUpdateList.add(new Location(w, x, y, z));
                                }
                            }
                        }
                        updateBlocks(player, LocationUpdateList);
                    }
                }.runTaskAsynchronously(GlitchMechanic.plugin);

            }
        }.runTask(GlitchMechanic.plugin);
    }

    private synchronized static void updateBlocks(Player player, LinkedList<Location> locations) {
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                for(int i = 0; i < locations.size(); i++) {
                    Location bLoc = locations.get(i);
                    if(bLoc.getX() % 16 == 0 || bLoc.getZ() % 16 == 0) {
                        if (!bLoc.getChunk().isLoaded()) {
                            i += 16;
                            continue;
                        }
                    }
                    Block block = bLoc.getBlock();
                    player.sendBlockChange(bLoc, block.getType(), block.getData());
                }
            }
        }.runTask(GlitchMechanic.plugin);
    }
}
