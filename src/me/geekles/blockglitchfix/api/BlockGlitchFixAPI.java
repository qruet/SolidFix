package me.geekles.blockglitchfix.api;

import me.geekles.blockglitchfix.BlockGlitchFixData;
import me.geekles.blockglitchfix.config.ConfigData;
import me.geekles.blockglitchfix.events.BlockGlitchFixListener;
import me.geekles.blockglitchfix.mechanism.GlitchMechanic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.geekles.blockglitchfix.BlockGlitchFix;

import java.util.UUID;

/**
 * The BlockGlitchFix resource API with relatively basic manipulation of the
 * plugin's mechanics as well as methods to retrieve data from the plugin's
 * mechanics for you to use in your own resource.
 * 
 * @author Geekles
 * @version 1.5.0
 * 
 */

public class BlockGlitchFixAPI {

	private BlockGlitchFixData data;

	public static BlockGlitchFixAPI init(BlockGlitchFix plugin) {
		return new BlockGlitchFixAPI(plugin);
	}

	/**
	 * This constructor is used to instantiate the API, do not touch
	 * 
	 * @param plugin
	 *            Main class
	 */
	private BlockGlitchFixAPI(BlockGlitchFix plugin) {
		this.data = plugin.getData();
	}

	/**
	 * 
	 * @return Maximum length of time (milliseconds) necessary since last block
	 *         break before player qualifies as "fast block breaking".
	 */
	public long getCoolDownChecker() {
		return ConfigData.BLOCK_BREAK_SENSITIVITY_COOLDOWN.getInt();
	}

	/**
	 * 
	 * @return Maximum length of time (milliseconds) necessary since last rapid
	 *         block break before player is removed from list of active block
	 *         updates.
	 */
	public long getCoolDownCheckerRemoval() {
		return ConfigData.BLOCK_UPDATE_REMOVAL_COOLDOWN.getInt();
	}

	/**
	 * 
	 * @return Returns in ticks how often every player receives block updates
	 */
	public long getUpdateInterval() {
		return ConfigData.BLOCK_UPDATE_INTERVAL.getInt();
	}

	/**
	 * 
	 * @return Returns block radius - All blocks within this radius from the
	 *         player's location are being updated for the player.
	 */
	public int getRadius() {
		return ConfigData.RADIUS.getInt();
	}

	/**
	 * 
	 * @return Returns a list of players receiving active block packet updates.
	 *         These players are actively breaking blocks rapidly.
	 */
	public UUID[] getPlayers() {
		return data.blockCheck.toArray(new UUID[0]);
	}

	/**
	 * @param debug
	 *            Allow method to print stacktrace error if anything goes wrong?
	 *            (Default True)
	 * @param radius
	 *            An int number of blocks from the player's location.
	 * @return Returns true if execution was successful.
	 */
	public boolean updateNearbyBlocks(Player player, int radius, boolean debug) {
		try {
			GlitchMechanic.updateBlocks(player, radius);
		} catch (Exception e) {
			if (debug) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param radius
	 *            An int number of blocks from the player's location.
	 * @return Returns true if execution was successful.
	 */
	public boolean updateNearbyBlocks(Player player, int radius) {
		try {
			updateNearbyBlocks(player, radius, true);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
