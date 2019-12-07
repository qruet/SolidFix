package me.geekles.blockglitchfix.events;

import java.util.HashSet;
import java.util.UUID;

import me.geekles.blockglitchfix.BlockGlitchFixData;
import me.geekles.blockglitchfix.config.ConfigData;
import me.geekles.blockglitchfix.mechanism.GlitchMechanic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import me.geekles.blockglitchfix.BlockGlitchFix;
import me.geekles.blockglitchfix.api.listeners.BlockUpdateEvent;
import me.geekles.blockglitchfix.api.listeners.BlockUpdateEvent.BlockUpdateReason;

public class BlockGlitchFixListener implements Listener {

    private BlockGlitchFixData data;

    public BlockGlitchFixListener(BlockGlitchFix plugin) {
        this.data = plugin.getData();
    }

    /**
     * Teleport Safety Feature (Avoid glitching into a bad block packet after teleporting)
     * @param e
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleportInterrupt(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        TeleportCause cause = e.getCause();
        if (cause != TeleportCause.UNKNOWN)
            return;

        BlockUpdateEvent event = new BlockUpdateEvent(player, BlockUpdateReason.SAFETY_UPDATE);
        Bukkit.getPluginManager().callEvent(event); // Calls custom BlockUpdate event
        if (!event.isCancelled()) {
            GlitchMechanic.updateBlocks(player, 2); // Updates a small "safe" radius of 2 blocks
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        data.removeData(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        data.removeData(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        if (!data.blockCheck.contains(id) && data.lastBreakTime.containsKey(id)) {
            if (System.currentTimeMillis() - data.lastBreakTime.get(id) <= ConfigData.BLOCK_BREAK_SENSITIVITY_COOLDOWN.get()) {
                data.blockCheck.add(id); // mark player for block updates
            }
        }
        data.lastBreakTime.put(id, System.currentTimeMillis()); // Temporarily stores the player with the time they last broke a block
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onBlockUpdate(BlockUpdateEvent e) {
        for (UUID id : (HashSet<UUID>) data.blockCheck.clone()) { // Gets a list of players (based off of their UUID) to check
            if (System.currentTimeMillis() - data.lastBreakTime.get(id) >= ConfigData.BLOCK_BREAK_SENSITIVITY_COOLDOWN.get()) { // checks to see the last time the player broke a block
                if (!data.lastBreakTimeSlow.containsKey(id)) {
                    data.lastBreakTimeSlow.put(id, System.currentTimeMillis()); // marks player for further review later if they continue to break blocks slowly
                } else if (System.currentTimeMillis() - data.lastBreakTimeSlow.get(id) >= ConfigData.BLOCK_UPDATE_REMOVAL_COOLDOWN.get()) {
                    data.removeData(id);
                }
                continue;
            }
            data.lastBreakTimeSlow.remove(id);
        }
    }

}
