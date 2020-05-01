package me.geekles.blockglitchfix.mechanism;

import me.geekles.blockglitchfix.BlockGlitchFix;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;

/**
 * Responsible for updating a player's client with new block packets
 *
 * @author geekles
 * @version 1.7
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
        Location pLoc = player.getLocation();
        if (!pLoc.getChunk().isLoaded())
            return;

        World w = pLoc.getWorld();

        Location source = pLoc.getBlock().getLocation();
        Location center = new Location(w, source.getX() - radius, source.getY() - radius, source.getZ() - radius);
        new BukkitRunnable() {
            public void run() {
                LinkedList<Location> updateList = new LinkedList<>();
                for (int x = (int) center.getX(); x <= center.getX() + (radius * 2); x++) {
                    for (int y = (int) center.getY(); y <= center.getY() + (radius * 2); y++) {
                        for (int z = (int) center.getZ(); z <= center.getZ() + (radius * 2); z++) {
                            Location loc = new Location(w, x, y, z);
                            if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                                z += 15;
                                continue;
                            }
                            updateList.add(loc);
                        }
                    }
                }
                updateBlocks(player, updateList);
            }
        }.runTaskAsynchronously(GlitchMechanic.plugin);
    }

    private synchronized static void updateBlocks(Player player, LinkedList<Location> locations) {
        if (!Bukkit.isPrimaryThread()) {
            new BukkitRunnable() {
                public void run() {
                    updateBlocks(player, locations);
                }
            }.runTask(JavaPlugin.getPlugin(BlockGlitchFix.class));
        }
        locations.parallelStream().forEach(l -> {
            Block block = l.getBlock();
            player.sendBlockChange(l, block.getType(), block.getData());
        });
    }
}
