package dev.qruet.solidfix.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Responsible for collecting data from the config
 *
 * @author qruet
 * @version 1.7
 */
public class ConfigDeserializer {

    private static JavaPlugin plugin;

    /**
     * Calls necessary functions to deserialize
     * and load values into the plugin from the
     * config
     *
     * @param plugin Plugin instance
     */
    public static void init(JavaPlugin plugin) {
        if (ConfigDeserializer.plugin != null)
            throw new UnsupportedOperationException("Can not initialize an already initialized class.");
        ConfigDeserializer.plugin = plugin;
        deserialize();
        plugin.getLogger().info("Collected data from config.");
    }

    /**
     * Reads values from the config and updates ConfigData's enums
     * to their associated values from config
     */
    private static void deserialize() {
        FileConfiguration config = plugin.getConfig();
        for (ConfigData dataField : ConfigData.values()) {
            plugin.getLogger().info("Reading data from " + dataField.getPath());
            if (dataField.getType() == Integer.class) {
                dataField.setInt(config.getInt(dataField.getPath()));
            } else if (dataField.getType() == Boolean.class) {
                dataField.setBoolean(config.getBoolean(dataField.getPath()));
            }
        }
    }

    /**
     * A command to update config and deserialize the updated values.
     */
    public static void reload() {
        plugin.reloadConfig();
        deserialize();
    }

}
