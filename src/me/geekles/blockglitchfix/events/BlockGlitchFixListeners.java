package me.geekles.blockglitchfix.events;

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
import org.bukkit.scheduler.BukkitRunnable;

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
			Bukkit.getPluginManager().callEvent(event); // Calls custom event
			if (!event.isCancelled()) {
				plugin.updateBlocks(player, 2); // Updates a small "safe" radius of 2 blocks
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		// No need to continue checking to see if player is breaking blocks rapidly if
		// player suddenly decides
		// to place some blocks down :P
		plugin.data.blockCheck.remove(player.getUniqueId());
		plugin.data.lastBreakTime.remove(player.getUniqueId());
		plugin.data.lastBreakTimeSlow.remove(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		// Frees up some memory and unnecessary player checks if the player is no longer
		// online (Optimization)
		plugin.data.blockCheck.remove(player.getUniqueId());
		plugin.data.lastBreakTime.remove(player.getUniqueId());
		plugin.data.lastBreakTimeSlow.remove(player.getUniqueId());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (!plugin.data.blockCheck.contains(player.getUniqueId()) && plugin.data.lastBreakTime.containsKey(player.getUniqueId())) {
			if (System.currentTimeMillis() - plugin.data.lastBreakTime.get(player.getUniqueId()) <= plugin.data.COOLDOWN_CHECKER) { // Checks to see if player is breaking blocks
																																	// faster then the set COOLDOWN Check limit. If
																																	// this is the case, the plugin categorizes the
																																	// player as a player who is breaking blocks
																																	// very fast
				plugin.data.blockCheck.add(player.getUniqueId()); // mark player for block updates
			}
		}
		plugin.data.lastBreakTime.put(player.getUniqueId(), System.currentTimeMillis()); // Temporarily stores the player with the time they last broke a block
	}

	@EventHandler
	public void onBlockUpdate(BlockUpdateEvent e) {
		new BukkitRunnable() {
			public void run() {
				for (UUID id : plugin.data.blockCheck) { // Gets a list of players (based off of their UUID) to check
					if (System.currentTimeMillis() - plugin.data.lastBreakTime.get(id) >= plugin.data.COOLDOWN_CHECKER) { // checks to see the last time the player broke a block
						if (!plugin.data.lastBreakTimeSlow.containsKey(id)) {
							plugin.data.lastBreakTimeSlow.put(id, System.currentTimeMillis()); // marks player for further review later if they continue to break blocks slowly
						} else if (System.currentTimeMillis() - plugin.data.lastBreakTimeSlow.get(id) >= plugin.data.COOLDOWN_CHECKER_REMOVAL) {
							plugin.data.lastBreakTimeSlow.remove(id); // free up some memory
							new BukkitRunnable() {
								@Override
								public void run() {
									plugin.data.blockCheck.remove(id);
								}
							}.runTask(plugin);
						}
					} else if (plugin.data.lastBreakTimeSlow.containsKey(id)) { // Checks to see if player was marked for further review to see if they continue
																				// to break blocks slowly
						plugin.data.lastBreakTimeSlow.remove(id); // Removes player from further review since they have once again began breaking
																	// blocks faster then the COOLDOWN Check limit.
					}
				}
			}
		}.runTaskAsynchronously(plugin);
	}

}
