package dev.qruet.solidfix.utils;

import org.bukkit.Bukkit;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class Reflections {


    private static final Map<Class<?>, Class<?>> CORRESPONDING_TYPES = new HashMap<Class<?>, Class<?>>() {{
        put(Integer.class, int.class);
        put(Double.class, double.class);
        put(Float.class, float.class);
        put(Short.class, short.class);
        put(Long.class, long.class);
        put(Byte.class, byte.class);
        put(Boolean.class, boolean.class);
    }};

    private static final ClassLoader LOADER;

    static {
        LOADER = Thread.currentThread().getContextClassLoader();
    }

    /**
     * Legacy (versions pre 1.17)
     *
     * @return Is server running legacy NMS
     */
    public static boolean isLegacy() {
        return Reflections.getIntVersion() < 1170;
    }

    private static Class<?> getPrimitiveType(Class<?> clazz) {
        return CORRESPONDING_TYPES.getOrDefault(clazz, clazz);
    }

    private static Class<?>[] toPrimitiveTypeArray(Class<?>... classes) {
        int a = classes != null ? classes.length : 0;
        Class<?>[] types = new Class<?>[a];
        for (int i = 0; i < a; i++) {
            types[i] = getPrimitiveType(classes[i]);
        }
        return types;
    }

    private static Class<?>[] toPrimitiveTypeArray(Object... values) {
        Class<?>[] classes = new Class[values.length];
        for(int i = 0; i < values.length; i++)
            classes[i] = values[i].getClass();
        return toPrimitiveTypeArray(classes);
    }

    private static boolean equalsTypeArray(Class<?>[] a, Class<?>[] o) {
        if (a.length != o.length)
            return false;
        for (int i = 0; i < a.length; i++)
            if(!(a[i].equals(o[i]) || a[i].isAssignableFrom(o[i]) || o[i].isAssignableFrom(a[i])))
                return false;
        return true;
    }

    private static Object getHandle(Object obj) {
        try {
            return getMethod("getHandle", obj.getClass()).invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param method Method to be invoked
     * @param obj    Instance of class where method exists
     * @return Returns any objects that the method may return
     */
    public static Object invokeMethod(String method, Object obj) {
        try {
            return getMethod(method, obj.getClass()).invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calls a method
     *
     * @param method Method to be invoked
     * @param obj    Instance of the class where method exists
     * @param args   Arguments in the method to be passed
     * @return Returns any objects that the method may return
     */
    public static Object invokeMethodWithArgs(String method, Object obj, Object... args) {
        try {
            return getMethod(method, obj.getClass(), args).invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a method instance
     *
     * @param name       Name of method
     * @param clazz      Class where method exists
     * @param paramTypes Parameters the class may have
     * @return Instance of method
     */
    public static Method getMethod(String name, Class<?> clazz, Object... paramTypes) {
        Class<?>[] t = toPrimitiveTypeArray(paramTypes);
        for (Method m : clazz.getMethods()) {
            Class<?>[] types = toPrimitiveTypeArray(m.getParameterTypes());
            if (m.getName().equals(name) && equalsTypeArray(types, t))
                return m;
        }
        return null;
    }

    /**
     * Locates the associated constructor from the given parameters
     * and returns an instance of the given class from the given
     * parameters.
     * @param clazz Class to instantiate
     * @param params Constructor parameter values
     * @return Instance of given class {@param clazz}
     */
    public static Object instantiate(Class<?> clazz, Object... params) {
        Class<?>[] types = toPrimitiveTypeArray(params);
        try {
            Constructor<?> constructor = null;
            for(Constructor<?> construct : clazz.getDeclaredConstructors()) {
                if(construct.getParameterCount() != types.length)
                    continue;
                Class<?>[] t = construct.getParameterTypes();
                if(equalsTypeArray(t, types)) {
                    constructor = construct;
                    break;
                }
            }

            return constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NullPointerException e) {
            System.err.println("Failed to instantiate " + clazz.getName() + " with " + Arrays.toString(types));
            return null;
        }
    }

    /**
     * Get version of server
     *
     * @return String version
     */
    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static int getIntVersion() {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(getVersion());
        StringBuilder strInt = new StringBuilder();
        while (m.find()) {
            strInt.append(m.group());
        }

        int version = -1;
        try {
            version = Integer.parseInt(strInt.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * Converts string name of class to Class object
     *
     * @param className Name of class
     * @return Class object
     */
    public static Class<?> getNMSClass(String className) {
        String fullName = "net.minecraft.server." + getVersion() + "." + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * Use this instead of getNMSClass for classes in the craftbukkit package
     *
     * @param className Name of class
     * @return Class Object
     */
    public static Class<?> getCraftBukkitClass(String className) {
        String fullName = "org.bukkit.craftbukkit." + getVersion() + "." + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * Retrieve fields from classes
     *
     * @param clazz Class where field exists
     * @param name  Name of field
     * @return Field object
     */
    public static Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getValue(Object instance, String name) {
        Object value = null;
        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            value = field.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static <T> T getValue(Object instance, FieldQuery<T> query) {
        T value = null;
        try {
            Field field = instance.getClass().getDeclaredField(query.fieldName);
            field.setAccessible(true);
            value = (T) field.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static <T> void setValue(Object instance, String name, T value) {
        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve method instance
     *
     * @param clazz Name of class where method exists
     * @param name  Name of method
     * @param args  Parameters that method may have
     * @return Method instance
     */
    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        for (Method m : clazz.getDeclaredMethods())
            if (m.getName().equals(name)
                    && (args.length == 0 || ClassListEqual(args,
                    m.getParameterTypes()))) {
                m.setAccessible(true);
                return m;
            }
        return null;
    }

    public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;
        if (l1.length != l2.length)
            return false;
        for (int i = 0; i < l1.length; i++)
            if (l1[i] != l2[i]) {
                equal = false;
                break;
            }
        return equal;
    }

    public static LinkedList<String> getSubPackagePaths(String packageName) {
        final LinkedList<String> ret = new LinkedList<>();

        try {
            boolean f1;
            do {
                f1 = false;
                Enumeration<URL> res = LOADER.getResources(packageName.replace('.', '/'));

                while (res.hasMoreElements()) {
                    final String dirPath = URLDecoder.decode(res.nextElement().getPath(), "UTF-8");
                    final File dir = new File(dirPath);

                    if (dir.listFiles() != null) {
                        for (final File file : dir.listFiles()) {
                            if (file.isDirectory()) {
                                packageName = !packageName.isEmpty() ? (packageName + '.' + file.getName()) : file.getName();
                                f1 = ret.add(packageName);
                            }
                        }
                    }
                }
            } while (f1);
        } catch (IOException e) {
            return new LinkedList<>();
        }

        return ret;
    }

    public static List<Class> findAllClassesInPackage(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toList());
    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static class FieldQuery<T> {

        final String fieldName;

        public FieldQuery(String fieldName) {
            this.fieldName = fieldName;
        }

    }
}