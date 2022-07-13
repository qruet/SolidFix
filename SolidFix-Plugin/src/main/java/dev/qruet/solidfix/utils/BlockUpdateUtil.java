package dev.qruet.solidfix.utils;

import dev.qruet.solidfix.player.SolidMiner;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedList;

/**
 * Responsible for updating a player's client with new block packets
 *
 * @author qruet
 * @version 2.0
 */
public class BlockUpdateUtil {
    /**
     * Updates the players with blocks that surround them using packets.
     *
     * @param miner  Player
     * @param radius Update radius
     */
    public static void updateBlocksAsync(SolidMiner miner, int radius) {
        updateBlocksAsync(miner, miner.getPlayer().getLocation(), radius);
    }

    public static void updateBlocksAsync(SolidMiner miner, final Location origin, int radius) {
        if (!origin.getChunk().isLoaded())
            return;

        World w = origin.getWorld();

        Location source = origin.getBlock().getLocation();
        Location offset = new Location(w, source.getX() - radius, source.getY() - radius, source.getZ() - radius);

        Tasky.async(new Thread(() -> {
            LinkedList<Location> updateList = new LinkedList<>();
            for (int x = (int) offset.getX(); x <= offset.getX() + (radius * 2); x++) {
                for (int y = (int) offset.getY(); y <= offset.getY() + (radius * 2); y++) {
                    for (int z = (int) offset.getZ(); z <= offset.getZ() + (radius * 2); z++) {
                        Location loc = new Location(w, x, y, z);
                        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                            z += 15;
                            continue;
                        }
                        updateList.add(loc);
                    }
                }
            }
            updateList.forEach(miner::sendBlockChange);
        }));
    }
}
