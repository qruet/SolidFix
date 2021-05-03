package dev.qruet.solidfix;

import dev.qruet.solidfix.commands.CommandManager;
import dev.qruet.solidfix.handlers.HandlerManager;
import dev.qruet.solidfix.listeners.ListenerManager;
import dev.qruet.solidfix.timers.BlockUpdateManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Manages the managers
 *
 * @author qruet
 * @version 1.9_01
 */
public class CoreManager {

    private static final Class<?>[] MANAGER_CLASSES = {BlockUpdateManager.class, CommandManager.class, HandlerManager.class, ListenerManager.class};

    private final List<SolidManager> MANAGER_REGISTRY = new LinkedList<>();

    private final JavaPlugin plugin;

    public CoreManager(JavaPlugin plugin) {
        this.plugin = plugin;

        Arrays.stream(MANAGER_CLASSES).forEach(clazz -> {
            SolidManager manager;
            try {
                Registrar registrar = new Registrar();
                manager = ((Class<? extends SolidManager>) clazz).getConstructor(Registrar.class).newInstance(registrar);
                registrar.set(manager);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
                return;
            }
            MANAGER_REGISTRY.add(manager);
        });
    }

    public void disable() {
        if (MANAGER_REGISTRY.isEmpty())
            throw new UnsupportedOperationException("Can not yet disable a non-initialized class.");

        MANAGER_REGISTRY.forEach(SolidManager::disable);
        MANAGER_REGISTRY.clear();
    }

    public class Registrar {

        private SolidManager manager;

        private Registrar() {
        }

        public JavaPlugin getPlugin() {
            return plugin;
        }

        private void set(SolidManager manager) {
            this.manager = manager;
        }

        public boolean isRegistered() {
            if (manager == null)
                return false;
            return MANAGER_REGISTRY.contains(manager);
        }
    }

}
