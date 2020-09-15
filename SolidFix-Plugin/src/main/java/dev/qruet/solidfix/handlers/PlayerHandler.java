package dev.qruet.solidfix.handlers;

import dev.qruet.solidfix.SolidServer;
import dev.qruet.solidfix.events.BlockUpdateEvent;
import dev.qruet.solidfix.utils.BlockUpdateUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Handles general player activities
 *
 * @author qruet
 * @version 1.9_01
 */
public class PlayerHandler implements Listener {

    /**
     * Teleport Safety Feature (Avoid glitching into a bad block packet after teleporting)
     *
     * @param e
     */

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleportInterrupt(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if (SolidServer.getOnlinePlayer(player.getUniqueId()) == null)
            return;

        World world = player.getLocation().getWorld();
        PlayerTeleportEvent.TeleportCause cause = e.getCause();
        if (cause != PlayerTeleportEvent.TeleportCause.UNKNOWN ||
                !player.getLocation().getChunk().isLoaded())
            return;

        if (SolidServer.getWorlds().stream().noneMatch(w -> w.getUID().equals(world.getUID())))
            return;

        BlockUpdateEvent event = new BlockUpdateEvent(player, BlockUpdateEvent.BlockUpdateReason.SAFETY_UPDATE);
        Bukkit.getPluginManager().callEvent(event); // Calls custom BlockUpdate event
        if (!event.isCancelled()) {
            BlockUpdateUtil.updateBlocks(player, 2); // Updates a small "safe" radius of 2 blocks
        }
    }

}
