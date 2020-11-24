package dev.qruet.solidfix;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the managers
 *
 * @author qruet
 * @version 1.9_01
 */
public class CoreManager {

    private static final Map<SolidManager, Boolean> MANAGERS = new HashMap<>();

    private CoreManager() {
        throw new UnsupportedOperationException();
    }

    public static void init() {
        if (!MANAGERS.isEmpty())
            throw new UnsupportedOperationException("Can not initialize an already initialized class.");

        Reflections reflections = new Reflections(CoreManager.class.getPackage().getName());
        reflections.getSubTypesOf(SolidManager.class).forEach(clazz -> {
            SolidManager manager;
            try {
                Registrar registrar = new Registrar();
                manager = clazz.getConstructor(Registrar.class).newInstance(registrar);
                registrar.set(manager);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
                return;
            }
            MANAGERS.put(manager, manager.init());
        });
    }

    public static void disable() {
        if (MANAGERS.isEmpty())
            throw new UnsupportedOperationException("Can not yet disable a non-initialized class.");

        MANAGERS.forEach((k, v) -> {
            if (v)
                k.disable();
        });
        MANAGERS.clear();
    }

    public static class Registrar {

        private SolidManager manager;

        private Registrar() {
        }

        private void set(SolidManager manager) {
            this.manager = manager;
        }

        public boolean isRegistered() {
            if (manager == null)
                return false;
            return MANAGERS.getOrDefault(manager, false);
        }
    }

}
