package me.geekles.blockglitchfix;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
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

		if (!new File(this.getDataFolder(), "config.yml").exists()) {
			System.out.println(ChatColor.YELLOW + "No Config Found! Generating a new one...");
			this.saveDefaultConfig();
		}
		getLogger().info("Initializing some values...");
		initializeValues();
		getLogger().info("Initializing a few other things...");
		blockUpdater();
		blockCheckListManager();
		getLogger().info("Registering Event(s)...");
		Bukkit.getPluginManager().registerEvents(new BlockGlitchFixListeners(this), this);
		getLogger().info("Alright I'm fully awake now!");
	}

	public void onDisable() {
		getLogger().info("Going to sleep... Bye!");
	}

	private void initializeValues() {

		this.data = new BlockGlitchFixData();
		new BlockGlitchFixAPI(this);

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

	protected void blockCheckListManager() {
		new BukkitRunnable() {
			public void run() {
				Set<UUID> blockCheckCopy = new HashSet<UUID>(data.blockCheck);
				for (UUID id : blockCheckCopy) {
					if (System.currentTimeMillis() - data.lastBreakTime.get(id) >= data.COOLDOWN_CHECKER) {
						if (!data.lastBreakTimeSlow.containsKey(id)) {
							data.lastBreakTimeSlow.put(id, System.currentTimeMillis());
						} else if (System.currentTimeMillis() - data.lastBreakTimeSlow.get(id) >= data.COOLDOWN_CHECKER_REMOVAL) {
							data.lastBreakTimeSlow.remove(id);
							data.blockCheck.remove(id);
						}
					} else if (data.lastBreakTimeSlow.containsKey(id)) {
						data.lastBreakTimeSlow.remove(id);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 1L, 5L);
	}

	protected void blockUpdater() {
		new BukkitRunnable() {
			public void run() {
				Set<UUID> blockCheckCopy = new HashSet<UUID>(data.blockCheck);
				for (UUID id : blockCheckCopy) {
					BlockUpdateEvent event = new BlockUpdateEvent(Bukkit.getPlayer(id), BlockUpdateReason.FAST_BREAKING);
					new BukkitRunnable() {
						@Override
						public void run() {
							Bukkit.getPluginManager().callEvent(event);
						}

					}.runTask(b);
					if (!event.isCancelled()) {
						updateBlocks(Bukkit.getPlayer(id), data.RADIUS);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 1L, data.UPDATE_INTERVAL);
	}

	@SuppressWarnings("deprecation")
	public void updateBlocks(Player player, int radius) {
		Location source = player.getLocation().getBlock().getLocation();
		Location center = new Location(source.getWorld(), source.getX() - radius, source.getY() - radius, source.getZ() - radius);
		Location temp = center.clone();
		for (int x = (int) center.getX(); x <= center.getX() + (radius * 2); x++) {
			for (int y = (int) center.getY(); y <= center.getY() + (radius * 2); y++) {
				for (int z = (int) center.getZ(); z <= center.getZ() + (radius * 2); z++) {
					temp.setX(x);
					temp.setY(y);
					temp.setZ(z);
					Block t = temp.getBlock();
					player.sendBlockChange(t.getLocation(), t.getType(), t.getData());
				}
			}
		}
	}

}
