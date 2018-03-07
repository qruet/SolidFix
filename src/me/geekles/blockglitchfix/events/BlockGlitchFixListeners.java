package me.geekles.blockglitchfix.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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
		Player player = e.getPlayer();
		TeleportCause cause = e.getCause();
		if (cause == TeleportCause.UNKNOWN) {
			BlockUpdateEvent event = new BlockUpdateEvent(player, BlockUpdateReason.SAFETY_UPDATE);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				plugin.updateBlocks(player, 2);
			}
		}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (plugin.data.blockCheck.contains(player.getUniqueId())) {
			plugin.data.blockCheck.remove(player.getUniqueId());
		}
		if (plugin.data.lastBreakTime.containsKey(player.getUniqueId())) {
			plugin.data.lastBreakTime.remove(player.getUniqueId());
		}
		if (plugin.data.lastBreakTimeSlow.containsKey(player.getUniqueId())) {
			plugin.data.lastBreakTimeSlow.remove(player.getUniqueId());
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (!plugin.data.blockCheck.contains(player.getUniqueId()) && plugin.data.lastBreakTime.containsKey(player.getUniqueId())) {
			if (System.currentTimeMillis() - plugin.data.lastBreakTime.get(player.getUniqueId()) <= plugin.data.COOLDOWN_CHECKER) {
				plugin.data.blockCheck.add(player.getUniqueId());
			}
		}
		plugin.data.lastBreakTime.put(player.getUniqueId(), System.currentTimeMillis());
	}

}
