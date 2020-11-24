package dev.qruet.solidfix.listeners;

import dev.qruet.solidfix.SolidServer;
import dev.qruet.solidfix.config.ConfigData;
import dev.qruet.solidfix.events.WorldPostEvent;
import dev.qruet.solidfix.utils.Tasky;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

/**
 * Watches for world-related events on the server
 *
 * @author qruet
 * @version 1.9_01
 */
public class WorldListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        World world = e.getWorld();
        if(ConfigData.VISUAL_FIX.getBoolean()) {
            Tasky.sync(t -> {
                Bukkit.getPluginManager().callEvent(new WorldPostEvent(world));
                SolidServer.loadWorld(world);
            }, 20 * 10L);
        } else {
            SolidServer.loadWorld(world);
            Bukkit.getPluginManager().callEvent(new WorldPostEvent(world));
        }
    }

    @EventHandler
    public void onUnload(WorldUnloadEvent e) {
        SolidServer.unloadWorld(e.getWorld());
    }

}
