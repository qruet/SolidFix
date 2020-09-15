package dev.qruet.solidfix.listeners;

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

public class ListenerManager extends SolidManager {

    private static final LinkedList<Listener> LISTENERS = new LinkedList<>();

    public ListenerManager(CoreManager.Registrar registrar) {
        super(registrar);
    }

    public boolean init() {
        Reflections reflections = new Reflections(ListenerManager.class.getPackage().getName());
        reflections.getSubTypesOf(Listener.class).forEach(clazz -> {
            Listener listener;
            try {
                listener = clazz.getConstructor().newInstance();
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
                return;
            }
            Bukkit.getPluginManager().registerEvents(listener, JavaPlugin.getPlugin(SolidFix.class));
            LISTENERS.add(listener);
        });
        return true;
    }

    public boolean disable() {
        LISTENERS.forEach(HandlerList::unregisterAll);
        return true;
    }

}
