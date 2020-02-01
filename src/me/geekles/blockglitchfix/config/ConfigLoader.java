package me.geekles.blockglitchfix.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Responsible for collecting data from the config
 * @author geekles
 * @version 1.7
 */
public class ConfigLoader {
    /**
     * Called while plugin is being initialized
     *
     * @param plugin Plugin instance
     */
    public static void init(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        for (ConfigData dataField : ConfigData.values()) {
            plugin.getLogger().info("Reading data from " + dataField.getPath());
            dataField.set(config.getInt(dataField.getPath()));
        }
        plugin.getLogger().info("Collected data from config.");
    }


}
