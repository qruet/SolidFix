package me.geekles.blockglitchfix;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.geekles.blockglitchfix.api.BlockGlitchFixAPI;
import me.geekles.blockglitchfix.api.listeners.BlockUpdateEvent;
import me.geekles.blockglitchfix.api.listeners.BlockUpdateEvent.BlockUpdateReason;
import me.geekles.blockglitchfix.events.BlockGlitchFixListeners;

public class BlockGlitchFix extends JavaPlugin {

	public BlockGlitchFixData data;

	private static BlockGlitchFix b;

	public void onEnable() {
		b = this;
		// Checks to see if config doesn't already exist
		if (!new File(this.getDataFolder(), "config.yml").exists()) {
			System.out.println(ChatColor.YELLOW + "No Config Found! Generating a new one...");
			this.saveDefaultConfig(); // Retrieves config.yml stored in plugin
		}
		getLogger().info("Initializing some values...");
		initializeValues(); // sets the necessary values from the config
		getLogger().info("Initializing a few other things...");
		blockUpdater(); // triggers the blockUpdater loop
		getLogger().info("Registering Event(s)...");
		Bukkit.getPluginManager().registerEvents(new BlockGlitchFixListeners(this), this);
		getLogger().info("BlockGlitchFix has been initialized successfully! \\(^w^)/");
		getLogger().info("Is my performance satisfactory? If you enjoy having me around, leave a review on my spigot page! ^w^");
	}

	public void onDisable() {
		getLogger().info("BlockGlitchFix has been successfully disabled!");
	}

	private void initializeValues() {

		this.data = new BlockGlitchFixData(); // store the instance of BlockGlitchFixData in order to retrieve data from the
												// class later on
		new BlockGlitchFixAPI(this); // initialize the API with the main class instance

		try {
			data.COOLDOWN_CHECKER = this.getConfig().getLong("BlockBreakSensitivityCooldown");
			data.COOLDOWN_CHECKER_REMOVAL = this.getConfig().getLong("BlockUpdatesRemovalCooldown");
			data.UPDATE_INTERVAL = this.getConfig().getLong("BlockUpdateInterval");
			data.RADIUS = this.getConfig().getInt("Radius");
		} catch (Exception e) {
			System.out.println(ChatColor.RED
					+ "[!] - BlockGlitchFix: Ooops! An error occurred while initializing config values, I'll still work but I'm going to use the default values. "
					+ "Check my spigot page to get the default config if you continue to experience issues. If the issue continues to persist send the following message to the developer "
					+ "geekles!");
			System.out.println(ChatColor.YELLOW + "====================================================================" + ChatColor.GRAY);
			e.printStackTrace();
			System.out.println(ChatColor.YELLOW + "====================================================================");
		}
	}

	protected void blockUpdater() {
		new BukkitRunnable() {
			public void run() {
				for (UUID id : data.blockCheck) {
					BlockUpdateEvent event = new BlockUpdateEvent(Bukkit.getPlayer(id), BlockUpdateReason.FAST_BREAKING);
					/* Creates an instance of a custom listener class and is ready to call. */
					Bukkit.getPluginManager().callEvent(event);
					Player player = Bukkit.getPlayer(id);
					if (!event.isCancelled() && player != null) { // makes sure the called event hasn't been cancelled
						updateBlocks(Bukkit.getPlayer(id), data.RADIUS); // updates blocks
					}
				}
			}
		}.runTaskTimer(this, 1L, data.UPDATE_INTERVAL /* Value can be found in config */);
	}

	/** Updates the players with blocks that surround them using packets.
	 * 
	 * @param player
	 *            Online player
	 * @param radius
	 *            Update radius */

	public void updateBlocks(Player player, int radius) {

		new BukkitRunnable() {
			public void run() {
				Location source = player.getLocation().getBlock().getLocation();
				Location center = new Location(source.getWorld(), source.getX() - radius, source.getY() - radius, source.getZ() - radius);
				Location temp = center.clone();
				new BukkitRunnable() {
					@SuppressWarnings("deprecation")
					public void run() {

						for (int x = (int) center.getX(); x <= center.getX() + (radius * 2); x++) {
							for (int y = (int) center.getY(); y <= center.getY() + (radius * 2); y++) {
								for (int z = (int) center.getZ(); z <= center.getZ() + (radius * 2); z++) {
									temp.setX(x);
									temp.setY(y);
									temp.setZ(z);
									Block block = temp.getBlock();
									player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
								}
							}
						}
					}
				}.runTaskAsynchronously(BlockGlitchFix.b);

			}
		}.runTask(this);
	}

}
