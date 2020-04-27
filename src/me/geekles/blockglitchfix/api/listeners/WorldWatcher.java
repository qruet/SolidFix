package me.geekles.blockglitchfix.api.listeners;

import me.geekles.blockglitchfix.BlockGlitchFix;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class WorldWatcher implements Listener {

    private static final LinkedList<World> worldBlacklist = new LinkedList<>();

    public WorldWatcher() {
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getPlugin(BlockGlitchFix.class));
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        World world = e.getWorld();
        worldBlacklist.add(world);
        new BukkitRunnable() {
            public void run() {
                worldBlacklist.remove(world);
            }
        }.runTaskLater(JavaPlugin.getPlugin(BlockGlitchFix.class), 20*10L);
    }

    public static Collection<World> getBlacklistedWorlds() {
        return Collections.unmodifiableCollection(worldBlacklist);
    }

}
