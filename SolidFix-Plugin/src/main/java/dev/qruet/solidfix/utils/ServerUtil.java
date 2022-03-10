package dev.qruet.solidfix.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public class ServerUtil {

    private static final Class<?> CraftServer = Reflections.getCraftBukkitClass("CraftServer");
    private static final Class<?> MinecraftServer = Reflections.getNMSClass("MinecraftServer");

    public static double getTPS() {
        Object server = CraftServer.cast(Bukkit.getServer());
        double[] tps = new double[0];
        try {
            Object mc_server = Reflections.getMethod(CraftServer, "getServer").invoke(server);
            tps = (double[]) MinecraftServer.getField("recentTps").get(mc_server);
        } catch (IllegalAccessException | NoSuchFieldException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return tps[0];
    }

}
