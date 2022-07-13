package dev.qruet.solidfix;

import dev.qruet.solidfix.player.SolidMiner;
import dev.qruet.solidfix.timers.SolidTimer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;

/**
 * Handles online player instances on the server
 *
 * @author qruet
 * @version 1.9_01
 */
public class SolidServer {

    /**
     * Mapped instances of all online players wrapped by a SolidMiner instance
     */
    private static final Map<UUID, SolidMiner> MINERS = new HashMap<>();
    /**
     * List of all loaded worlds on the server
     */
    private static final LinkedList<World> WORLDS = new LinkedList<>();
    /**
     * A timer responsible for ticking all online players
     */
    private static SolidTimer.Tick HEART_BEAT;

    private static boolean initialized = false;

    /**
     * Checks to see if class has already been initialized and updates global variables
     * Starts timer (SolidTimer)
     */
    public static void init() {
        if (initialized)
            throw new UnsupportedOperationException("Can not initialize an already initialized class.");
        initialized = true;

        Bukkit.getServer().getWorlds().forEach(SolidServer::loadWorld);

        Bukkit.getOnlinePlayers().forEach(SolidServer::register);

        HEART_BEAT = SolidTimer.tick();
    }

    /**
     * Unregisters all player instances
     */
    public static void disable() {
        if (!initialized)
            throw new UnsupportedOperationException("Can not yet disable a non-initialized class.");

        HEART_BEAT.cancel();

        Iterator<UUID> iter = MINERS.keySet().iterator();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
    }

    /**
     * Adds newly loaded world to WORLDS
     *
     * @param world
     */
    public static void loadWorld(World world) {
        if (WORLDS.contains(world))
            return;
        WORLDS.add(world);
    }

    /**
     * Removes world from WORLDS list
     *
     * @param world
     */
    public static void unloadWorld(World world) {
        WORLDS.remove(world);
    }

    /**
     * Returns a Collection of WORLDS
     *
     * @return Loaded worlds
     */
    public static Collection<World> getWorlds() {
        return Collections.unmodifiableCollection(WORLDS);
    }

    /**
     * Wraps player instance into a new SolidMiner instance and maps it
     * to the MINERS map
     *
     * @param player
     */
    public static void register(Player player) {
        SolidMiner miner = new SolidMiner(player);
        MINERS.put(player.getUniqueId(), miner);
    }

    /**
     * Removes associated SolidMiner instance to the provided id from the
     * MINERS map
     *
     * @param id
     */
    public static void unregister(UUID id) {
        MINERS.remove(id);
    }

    /**
     * Retrieves associated SolidMiner instance to the provided id from the
     * MINERS map
     *
     * @param id
     * @return SolidMiner instance
     */
    public static SolidMiner getMiner(UUID id) {
        return MINERS.get(id);
    }

    /**
     * Retrieves all SolidMiner instances in MINERS and writes it as an
     * unmodifiable collection
     *
     * @return Online players
     */
    public static Collection<SolidMiner> getOnlinePlayers() {
        return Collections.unmodifiableCollection(MINERS.values());
    }

}
