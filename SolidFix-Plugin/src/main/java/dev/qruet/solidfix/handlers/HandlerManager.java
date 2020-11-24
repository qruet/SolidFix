package dev.qruet.solidfix.handlers;

import dev.qruet.solidfix.CoreManager;
import dev.qruet.solidfix.SolidFix;
import dev.qruet.solidfix.SolidManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * Registers associated listeners (handlers) responsible for
 * handling certain triggered events
 * @author qruet
 * @version 2.0_02
 */
public class HandlerManager extends SolidManager {

    private static final LinkedList<Listener> HANDLERS = new LinkedList<>();

    public HandlerManager(CoreManager.Registrar registrar) {
        super(registrar);
    }

    /**
     * Loops through classes within its package that implements Listener and registers them
     *
     * @return was successful
     */
    public boolean init() {
        Reflections reflections = new Reflections(HandlerManager.class.getPackage().getName());
        reflections.getSubTypesOf(Listener.class).forEach(clazz -> {
            Listener listener;
            try {
                listener = clazz.getConstructor().newInstance();
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
                return;
            }
            Bukkit.getPluginManager().registerEvents(listener, JavaPlugin.getPlugin(SolidFix.class));
            HANDLERS.add(listener);
        });
        return true;
    }

    /**
     * Loops through the list of handler instances and unregisters them
     * @return was successful
     */
    public boolean disable() {
        HANDLERS.forEach(HandlerList::unregisterAll);
        return true;
    }

}
