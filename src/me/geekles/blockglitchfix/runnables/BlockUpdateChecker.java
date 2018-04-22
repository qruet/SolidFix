package me.geekles.blockglitchfix.runnables;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.geekles.blockglitchfix.BlockGlitchFix;
import me.geekles.blockglitchfix.api.listeners.BlockUpdateEvent;
import me.geekles.blockglitchfix.api.listeners.BlockUpdateEvent.BlockUpdateReason;

public class BlockUpdateChecker extends BukkitRunnable {

	private static BlockUpdateChecker instance;
	private BlockGlitchFix plugin;

	public BlockUpdateChecker getInstance() {
		return instance;
	}

	public BlockUpdateChecker(BlockGlitchFix plugin) {
		this.plugin = plugin;
		instance = this;
	}

	/** Stop checking players. */
	public void stopCheck() {
		plugin.data.blockCheck.clear();
		cancel();
	}

	@SuppressWarnings("unchecked")
	public void run() {
		for (UUID id : (HashSet<UUID>) plugin.data.blockCheck.clone()) {
			BlockUpdateEvent event = new BlockUpdateEvent(Bukkit.getPlayer(id), BlockUpdateReason.FAST_BREAKING);
			/* Creates an instance of a custom listener class and is ready to call. */
			Bukkit.getPluginManager().callEvent(event);
			Player player = Bukkit.getPlayer(id);
			if (!event.isCancelled() && player != null) { // makes sure the called event hasn't been cancelled
				plugin.updateBlocks(player, plugin.data.RADIUS); // updates blocks
			}
		}
	}

}
