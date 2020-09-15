package dev.qruet.solidfix.handlers;

import dev.qruet.solidfix.SolidServer;
import dev.qruet.solidfix.player.SolidMiner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

/**
 * Responsible for listening for triggering events to fire block updates
 *
 * @author qruet
 * @version 1.9_01
 */
public class ActionHandler implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        SolidMiner miner = SolidServer.getMiner(id);
        if (miner == null)
            return;
        miner.logBlockBreak(e.getBlock());
    }

}
