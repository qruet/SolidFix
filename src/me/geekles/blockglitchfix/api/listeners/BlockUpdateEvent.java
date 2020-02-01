package me.geekles.blockglitchfix.api.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called when nearby blocks for a player is updated to prevent
 * potential collision interferences between the client and server.
 *
 * @author geekles
 */

public class BlockUpdateEvent extends Event implements Cancellable {

    public enum BlockUpdateReason {
        /**
         * SAFETY_UPDATE - An occassional update that occurs when a player is
         * teleported for an unknown reason. Perhaps stuck in a block? FAST_BREAKING -
         * If player is rapidly breaking blocks, this reason is used.
         */
        SAFETY_UPDATE, FAST_BREAKING
    }

    private Player player;
    private boolean isCancelled;
    private BlockUpdateReason reason;
    private static final HandlerList handlers = new HandlerList();

    public BlockUpdateEvent(Player player, BlockUpdateReason reason) {
        this.player = player;
        this.reason = reason;
        this.isCancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * @return Returns reason for why the BlockUpdateEvent was called
     */
    public BlockUpdateReason getReason() {
        return reason;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;

    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
