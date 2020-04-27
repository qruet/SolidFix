package me.geekles.blockglitchfix.api.listeners;

import me.geekles.blockglitchfix.BlockGlitchFix;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.UUID;

public class PlayerWatcher implements Listener {

    private static final LinkedList<UUID> playerBlacklist = new LinkedList<>();

    public PlayerWatcher() {
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getPlugin(BlockGlitchFix.class));
    }

    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        playerBlacklist.add(player.getUniqueId());
        new BukkitRunnable() {
            public void run() {
                playerBlacklist.remove(player.getUniqueId());
            }
        }.runTaskLater(JavaPlugin.getPlugin(BlockGlitchFix.class), 20*5L);
    }

    public static Collection<UUID> getBlacklistedPlayers() {
        return Collections.unmodifiableCollection(playerBlacklist);
    }


}
