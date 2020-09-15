package dev.qruet.solidfix.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.LinkedList;

/**
 * Responsible for updating a player's client with new block packets
 *
 * @author qruet
 * @version 1.9_01
 */
public class BlockUpdateUtil {
    /**
     * Updates the players with blocks that surround them using packets.
     *
     * @param player Online player
     * @param radius Update radius
     */
    public static void updateBlocks(Player player, int radius) {
        updateBlocks(player, player.getLocation(), radius);
    }

    public synchronized static void updateBlocks(Player player, final Location origin, int radius) {
        if (!origin.getChunk().isLoaded())
            return;

        World w = origin.getWorld();

        Location source = origin.getBlock().getLocation();
        Location center = new Location(w, source.getX() - radius, source.getY() - radius, source.getZ() - radius);

        Tasky.async(t -> {
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
        });
    }

    private synchronized static void updateBlocks(Player player, LinkedList<Location> locations) {
        if (!Bukkit.isPrimaryThread()) {
            Tasky.sync(t -> {
                updateBlocks(player, locations);
            });
        }
        locations.forEach(l -> {
            Block block = l.getBlock();
            Tasky.async(t -> {
                player.sendBlockChange(l, block.getType(), block.getData());
            });
        });
    }
}
