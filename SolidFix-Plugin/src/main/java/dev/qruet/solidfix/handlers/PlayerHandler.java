package dev.qruet.solidfix.handlers;

import dev.qruet.solidfix.SolidServer;
import dev.qruet.solidfix.events.BlockUpdateEvent;
import dev.qruet.solidfix.player.SolidMiner;
import dev.qruet.solidfix.utils.BlockUpdateUtil;
import dev.qruet.solidfix.utils.Tasky;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
    private List<UUID> teleportInterruptIgnore = new LinkedList<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleportInterrupt(PlayerTeleportEvent e) {
        Player player = e.getPlayer();

        SolidMiner miner = SolidServer.getMiner(player.getUniqueId());
        if (miner == null)
            return;

        World world = player.getLocation().getWorld();
        PlayerTeleportEvent.TeleportCause cause = e.getCause();
        if (cause != PlayerTeleportEvent.TeleportCause.UNKNOWN ||
                !player.getLocation().getChunk().isLoaded())
            return;

        if (teleportInterruptIgnore.contains(player.getUniqueId())) {
            return;
        }

        if (SolidServer.getWorlds().stream().noneMatch(w -> w.getUID().equals(world.getUID())))
            return;

        BlockUpdateEvent event = new BlockUpdateEvent(player, BlockUpdateEvent.BlockUpdateReason.SAFETY_UPDATE);
        Bukkit.getPluginManager().callEvent(event); // Calls custom BlockUpdate event
        if (!event.isCancelled()) {
            BlockUpdateUtil.updateBlocks(miner, 2); // Updates a small "safe" radius of 2 blocks
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBed(PlayerBedLeaveEvent e) {
        skipInterruptCheck(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onExit(VehicleExitEvent e) {
        LivingEntity entity = e.getExited();
        if (entity instanceof Player) {
            skipInterruptCheck(entity.getUniqueId());
        }
    }

    private void skipInterruptCheck(UUID id) {
        if (teleportInterruptIgnore.contains(id))
            return;
        teleportInterruptIgnore.add(id);

        Tasky.sync(t -> {
            teleportInterruptIgnore.remove(id);
        }, 1L);
    }

}
