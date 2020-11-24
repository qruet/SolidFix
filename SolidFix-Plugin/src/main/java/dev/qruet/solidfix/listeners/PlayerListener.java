package dev.qruet.solidfix.listeners;

import dev.qruet.solidfix.SolidServer;
import dev.qruet.solidfix.config.ConfigData;
import dev.qruet.solidfix.utils.Tasky;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Watches for player/server related activities
 *
 * @author qruet
 * @version 1.9_01
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if(ConfigData.VISUAL_FIX.getBoolean()) {
            Tasky.sync(t -> {
                if (Bukkit.getPlayer(player.getUniqueId()) != null)
                    SolidServer.register(player);
            }, 20 * 5L);
        } else {
            SolidServer.register(player);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        SolidServer.unregister(e.getPlayer().getUniqueId());
    }

}
