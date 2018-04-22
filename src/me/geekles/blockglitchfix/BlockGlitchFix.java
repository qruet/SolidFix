package me.geekles.blockglitchfix;

import java.io.File;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.geekles.blockglitchfix.api.BlockGlitchFixAPI;
import me.geekles.blockglitchfix.events.BlockGlitchFixListeners;
import me.geekles.blockglitchfix.runnables.BlockUpdateChecker;

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

		new BlockUpdateChecker(this).runTaskTimer(this, 5L, data.UPDATE_INTERVAL);

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
			System.out.println(
					ChatColor.RED + "[!] - BlockGlitchFix: Ooops! An error occurred while initializing config values, I'll still work but I'm going to use the default values. " + "Check my spigot page to get the default config if you continue to experience issues. If the issue continues to persist send the following message to the developer " + "geekles!");
			System.out.println(ChatColor.YELLOW + "====================================================================" + ChatColor.GRAY);
			e.printStackTrace();
			System.out.println(ChatColor.YELLOW + "====================================================================");
		}
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
				Location pLoc = player.getLocation();
				World w = pLoc.getWorld();
				Location source = pLoc.getBlock().getLocation();
				Location center = new Location(source.getWorld(), source.getX() - radius, source.getY() - radius, source.getZ() - radius);
				new BukkitRunnable() {
					public void run() {
						LinkedList<Location> LocationUpdateList = new LinkedList<Location>();
						for (int x = (int) center.getX(); x <= center.getX() + (radius * 2); x++) {
							for (int y = (int) center.getY(); y <= center.getY() + (radius * 2); y++) {
								for (int z = (int) center.getZ(); z <= center.getZ() + (radius * 2); z++) {
									LocationUpdateList.add(new Location(w, x, y, z));
								}
							}
						}
						updateBlocks(player, LocationUpdateList);
					}
				}.runTaskAsynchronously(BlockGlitchFix.b);

			}
		}.runTask(this);
	}

	private void updateBlocks(Player player, LinkedList<Location> locations) {
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				for (Location bLoc : locations) {
					if (bLoc.getChunk().isLoaded()) {
						Block block = bLoc.getBlock();
						player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
					}
				}
			}
		}.runTask(this);
	}
}
