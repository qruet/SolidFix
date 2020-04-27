package me.geekles.blockglitchfix.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Responsible for collecting data from the config
 * @author geekles
 * @version 1.7
 */
public class ConfigDeserializer {
    /**
     * Called while plugin is being initialized
     *
     * @param plugin Plugin instance
     */
    public static void init(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        for (ConfigData dataField : ConfigData.values()) {
            plugin.getLogger().info("Reading data from " + dataField.getPath());
            if (dataField.getType() == Integer.class) {
                dataField.setInt(config.getInt(dataField.getPath()));
            } else if (dataField.getType() == Boolean.class) {
                dataField.setBoolean(config.getBoolean(dataField.getPath()));
            }
        }
        plugin.getLogger().info("Collected data from config.");
    }


}
