package me.geekles.blockglitchfix.events;

import java.util.HashSet;
import java.util.UUID;

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

public class BlockGlitchFixListeners implements Listener {

	private BlockGlitchFix plugin;

	public BlockGlitchFixListeners(BlockGlitchFix plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onTeleportInterrupt(PlayerTeleportEvent e) {
		/* Although doesn't often happen, it "could" happen theoretically where the
		 * plugin doesn't update the blocks fast enough and a player encounters a
		 * glitched block If the player is teleported for an unknown reason a small
		 * block update is made just to be "safe", although not necessarily necessary,
		 * it helps further secure the impossibility of a player once again being stuck
		 * in a block. */
		Player player = e.getPlayer();
		TeleportCause cause = e.getCause();
		if (cause == TeleportCause.UNKNOWN) { // Checks to see if the cause for the teleport is unkown (This also can be true
												// not just when a player is stuck in a block but plugins that do not mark a
												// reason for a player being teleported)
			BlockUpdateEvent event = new BlockUpdateEvent(player, BlockUpdateReason.SAFETY_UPDATE);
			Bukkit.getPluginManager().callEvent(event); // Calls custom BlockUpdate event
			if (!event.isCancelled()) {
				plugin.updateBlocks(player, 2); // Updates a small "safe" radius of 2 blocks
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		// No need to continue checking to see if player is breaking blocks rapidly if
		// player suddenly decides
		// to place some blocks down :P
		plugin.data.removeData(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		// Frees up some memory and unnecessary player checks if the player is no longer
		// online (Optimization)
		plugin.data.removeData(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		UUID id = e.getPlayer().getUniqueId();
		if (!plugin.data.blockCheck.contains(id) && plugin.data.lastBreakTime.containsKey(id)) {
			if (System.currentTimeMillis() - plugin.data.lastBreakTime.get(id) <= plugin.data.COOLDOWN_CHECKER) {
				plugin.data.blockCheck.add(id); // mark player for block updates
			}
		}
		plugin.data.lastBreakTime.put(id, System.currentTimeMillis()); // Temporarily stores the player with the time they last broke a block
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onBlockUpdate(BlockUpdateEvent e) {
		for (UUID id : (HashSet<UUID>) plugin.data.blockCheck.clone()) { // Gets a list of players (based off of their UUID) to check
			if (System.currentTimeMillis() - plugin.data.lastBreakTime.get(id) >= plugin.data.COOLDOWN_CHECKER) { // checks to see the last time the player broke a block
				if (!plugin.data.lastBreakTimeSlow.containsKey(id)) {
					plugin.data.lastBreakTimeSlow.put(id, System.currentTimeMillis()); // marks player for further review later if they continue to break blocks slowly
				} else if (System.currentTimeMillis() - plugin.data.lastBreakTimeSlow.get(id) >= plugin.data.COOLDOWN_CHECKER_REMOVAL) {
					plugin.data.removeData(id);
				}
			} else if (plugin.data.lastBreakTimeSlow.containsKey(id)) { // Checks to see if player was marked for further review to see if they continue
																		// to break blocks slowly
				plugin.data.lastBreakTimeSlow.remove(id); // Removes player from further review since they have once again began breaking
															// blocks faster then the COOLDOWN Check limit.
			}
		}
	}

}
