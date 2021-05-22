package dev.qruet.solidfix.utils;

import dev.qruet.solidfix.player.SolidMiner;
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
     * @param miner  Player
     * @param radius Update radius
     */
    public static void updateBlocks(SolidMiner miner, int radius) {
        updateBlocks(miner, miner.getPlayer().getLocation(), radius);
    }

    public synchronized static void updateBlocks(SolidMiner miner, final Location origin, int radius) {
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
            updateBlocks(miner, updateList);
        });
    }

    private synchronized static void updateBlocks(SolidMiner miner, LinkedList<Location> locations) {
        if (!Bukkit.isPrimaryThread()) {
            Tasky.sync(t -> {
                updateBlocks(miner, locations);
            });
        }
        locations.forEach(loc -> {
            Tasky.async(t -> {
                miner.sendBlockChange(loc);
            });
        });
    }
}
