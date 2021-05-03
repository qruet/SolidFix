package dev.qruet.solidfix.listeners;

import dev.qruet.solidfix.CoreManager;
import dev.qruet.solidfix.SolidManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Responsible for registering/unregistering related listeners
 * @author qruet
 * @version 2.0_02
 */
public class ListenerManager extends SolidManager {

    private static final Class<?>[] LISTENER_CLASSES = {PlayerListener.class, WorldListener.class};

    private final LinkedList<Listener> LISTENER_REGISTRY = new LinkedList<>();

    public ListenerManager(CoreManager.Registrar registrar) {
        super(registrar);
        Arrays.stream(LISTENER_CLASSES).forEach(clazz -> {
            Listener listener;
            try {
                listener = ((Class<? extends Listener>) clazz).getConstructor().newInstance();
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
                return;
            }
            Bukkit.getPluginManager().registerEvents(listener, registrar.getPlugin());
            LISTENER_REGISTRY.add(listener);
        });
    }

    /**
     * Loops through the list of handler instances and unregisters them
     * @return was successful
     */
    public boolean disable() {
        LISTENER_REGISTRY.forEach(HandlerList::unregisterAll);
        return true;
    }

}
