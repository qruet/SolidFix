package dev.qruet.solidfix;

import dev.qruet.solidfix.player.SolidMiner;
import dev.qruet.solidfix.timers.SolidTimer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author qruet
 * @version 1.9_01
 */
public class SolidServer {

    private static final Map<UUID, SolidMiner> MINERS = new HashMap<>();
    private static final LinkedList<World> WORLDS = new LinkedList<>();
    private static SolidTimer.Tick HEART_BEAT;

    private static boolean initialized = false;

    public static void init() {
        if (initialized)
            throw new UnsupportedOperationException("Can not initialize an already initialized class.");
        initialized = true;

        Bukkit.getServer().getWorlds().forEach(SolidServer::loadWorld);

        Bukkit.getOnlinePlayers().forEach(SolidServer::register);

        HEART_BEAT = SolidTimer.tick();
    }

    public static void disable() {
        if (!initialized)
            throw new UnsupportedOperationException("Can not yet disable a non-initialized class.");

        HEART_BEAT.cancel();

        Iterator<UUID> iter = MINERS.keySet().iterator();
        while (iter.hasNext())
            unregister(iter.next());
    }

    public static void loadWorld(World world) {
        if (WORLDS.contains(world))
            return;
        WORLDS.add(world);
    }

    public static void unloadWorld(World world) {
        WORLDS.remove(world);
    }

    public static Collection<World> getWorlds() {
        return Collections.unmodifiableCollection(WORLDS);
    }

    public static void register(Player player) {
        SolidMiner miner = new SolidMiner(player);
        MINERS.put(player.getUniqueId(), miner);
    }

    public static void unregister(UUID id) {
        MINERS.remove(id);
    }

    public static SolidMiner getMiner(UUID id) {
        return MINERS.get(id);
    }

    public static Collection<SolidMiner> getOnlinePlayers() {
        return Collections.unmodifiableCollection(MINERS.values());
    }

    public static SolidMiner getOnlinePlayer(UUID id) {
        return MINERS.get(id);
    }

}
