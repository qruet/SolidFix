package dev.qruet.solidfix.events;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldEvent;

/**
 * Event called when world is fully loaded and ready to be checked by plugin
 * @author qruet
 * @version 1.9_01
 */
public class WorldPostEvent extends WorldEvent {

    private static final HandlerList handlers = new HandlerList();

    public WorldPostEvent(World world) {
        super(world);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
