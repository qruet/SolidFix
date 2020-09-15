package dev.qruet.solidfix;

import dev.qruet.solidfix.config.ConfigData;
import dev.qruet.solidfix.config.ConfigDeserializer;
import dev.qruet.solidfix.utils.Tasky;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for initializing the numerous parts of the plugin
 *
 * @author qruet
 * @version 1.9_01
 */
public class SolidFix extends JavaPlugin {

    private static final List<String> WHITELISTED_VERSIONS = Arrays.asList("1.8", "1.9", "1.10", "1.11", "1.12", "1.14");

    public void onEnable() {
        Tasky.setPlugin(this);

        String pckg = getServer().getClass().getName();
        String version = pckg.substring(pckg.indexOf("v") + 1, pckg.indexOf("_R")).replace("_", ".");

        // Checks to see if config doesn't already exist
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            getLogger().severe("No Config File Found! Generating a new one...");
            this.saveDefaultConfig(); // Retrieves config.yml stored in plugin
        }

        getLogger().info("Deserializing config values:");

        ConfigDeserializer.init(this); //deserialize config

        if (!ConfigData.OVERRIDE_VERSION_CHECK.getBoolean() && !(WHITELISTED_VERSIONS.contains(version))) {
            getLogger().severe("The plugin does not support the current version of your server, " + Bukkit.getVersion() + ". " +
                    "This is likely because the client glitch effect has been already resolved by Mojang. " +
                    "Note: You can override this error by changing \"Override Version Check\" from \"False\" to \"True\"" +
                    " in the config.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (ConfigData.VISUAL_FIX.getBoolean()) {
            getLogger().info("Implementing visual fix procedures.");
            if (!version.equals("1.8")) {
                getLogger().warning("Please note the visual bug fix is intended to resolve " +
                        "a known visual bug during chunk loading for clients on 1.8. Your server is running, "
                        + Bukkit.getVersion() + ", so this feature may not be necessary.");
            }
        }

        getLogger().info("Initializing core systems.");
        Tasky.sync(t -> {
            CoreManager.init();
            SolidServer.init();
        }, 1L);

        getLogger().info("Initialized successfully!");
        getLogger().info("Is my performance satisfactory? If you enjoy having me around, leave a review on my spigot page!");
        getLogger().info("https://www.spigotmc.org/resources/block-glitch-%E2%98%85-fix-%E2%98%85-an-essential-solution-that-finally-exists.54103/");
    }

    public void onDisable() {
        try {
            SolidServer.disable();
            CoreManager.disable();
        } catch (UnsupportedOperationException e) {
        }
        HandlerList.unregisterAll(this);
        getLogger().info("BlockGlitchFix has been successfully disabled!");
    }
}
