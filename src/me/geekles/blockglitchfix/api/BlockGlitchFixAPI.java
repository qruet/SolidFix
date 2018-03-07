package me.geekles.blockglitchfix.api;

import org.bukkit.entity.Player;

import me.geekles.blockglitchfix.BlockGlitchFix;

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

	/**
	 * API instance
	 */
	public static BlockGlitchFixAPI API;

	private BlockGlitchFix plugin;

	/**
	 * This constructor is used to instantiate the API, do not touch
	 * 
	 * @param plugin
	 *            Main class
	 */
	public BlockGlitchFixAPI(BlockGlitchFix plugin) {
		API = this;
		this.plugin = plugin;
	}

	/**
	 * 
	 * @return Maximum length of time (milliseconds) necessary since last block
	 *         break before player qualifies as "fast block breaking".
	 */
	public long getCoolDownChecker() {
		return plugin.data.COOLDOWN_CHECKER;
	}

	/**
	 * 
	 * @return Maximum length of time (milliseconds) necessary since last rapid
	 *         block break before player is removed from list of active block
	 *         updates.
	 */
	public long getCoolDownCheckerRemoval() {
		return plugin.data.COOLDOWN_CHECKER_REMOVAL;
	}

	/**
	 * 
	 * @return Returns in ticks how often every player receives block updates
	 */
	public long getUpdateInterval() {
		return plugin.data.UPDATE_INTERVAL;
	}

	/**
	 * 
	 * @return Returns block radius - All blocks within this radius from the
	 *         player's location are being updated for the player.
	 */
	public int getRadius() {
		return plugin.data.RADIUS;
	}

	/**
	 * 
	 * @return Returns a list of players receiving active block packet updates.
	 *         These players are actively breaking blocks rapidly.
	 */
	public Player[] getPlayers() {
		return plugin.data.blockCheck.toArray(new Player[plugin.data.blockCheck.size()]);
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
			plugin.updateBlocks(player, radius);
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
