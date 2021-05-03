package dev.qruet.solidfix.handlers;

import dev.qruet.solidfix.CoreManager;
import dev.qruet.solidfix.SolidManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Registers associated listeners (handlers) responsible for
 * handling certain triggered events
 * @author qruet
 * @version 2.0_02
 */
public class HandlerManager extends SolidManager {

    private static final Class<?>[] HANDLER_CLASSES = {ActionHandler.class, PlayerHandler.class};

    private final LinkedList<Listener> HANDLER_REGISTRY = new LinkedList<>();

    public HandlerManager(CoreManager.Registrar registrar) {
        super(registrar);
        Arrays.stream(HANDLER_CLASSES).forEach(clazz -> {
            Listener listener;
            try {
                listener = ((Class<? extends Listener>) clazz).getConstructor().newInstance();
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
                return;
            }
            Bukkit.getPluginManager().registerEvents(listener, registrar.getPlugin());
            HANDLER_REGISTRY.add(listener);
        });
    }

    /**
     * Loops through the list of handler instances and unregisters them
     * @return was successful
     */
    public boolean disable() {
        HANDLER_REGISTRY.forEach(HandlerList::unregisterAll);
        return true;
    }

}
