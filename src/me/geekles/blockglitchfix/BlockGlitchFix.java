package me.geekles.blockglitchfix;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import me.geekles.blockglitchfix.api.listeners.PlayerWatcher;
import me.geekles.blockglitchfix.api.listeners.WorldWatcher;
import me.geekles.blockglitchfix.config.ConfigData;
import me.geekles.blockglitchfix.config.ConfigDeserializer;
import me.geekles.blockglitchfix.events.BlockGlitchFixListener;
import me.geekles.blockglitchfix.mechanism.GlitchMechanic;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.geekles.blockglitchfix.api.BlockGlitchFixAPI;
import me.geekles.blockglitchfix.runnables.BlockUpdateChecker;

/**
 * Responsible for initializing the numerous parts of the plugin
 *
 * @author geekles
 * @version 1.7
 */
public class BlockGlitchFix extends JavaPlugin {

    public static BlockGlitchFixAPI API;
    private static final List<String> WHITELISTED_VERSIONS = Arrays.asList("1.8", "1.9", "1.10", "1.11", "1.12", "1.14");
    private BlockGlitchFixData data;
    private Listener fixListener;
    private WorldWatcher w_watcher;
    private PlayerWatcher p_watcher;

    public void onEnable() {
        String pckg = getServer().getClass().getName();
        String version = pckg.substring(pckg.indexOf("v") + 1, pckg.indexOf("_R")).replace("_", ".");

        if (!(WHITELISTED_VERSIONS.contains(version))) {
            getLogger().severe("The plugin does not support the current version of your server, " + Bukkit.getVersion() + ". " +
                    "This is likely because the client glitch effect has been already resolved by Mojang. " +
                    "Please file a bug report on the github page if you believe this is an error.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Checks to see if config doesn't already exist
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            getLogger().severe("No Config File Found! Generating a new one...");
            this.saveDefaultConfig(); // Retrieves config.yml stored in plugin
        }

        initValues(); // sets the necessary values from the config

        GlitchMechanic.init(this);

        if (ConfigData.VISUAL_FIX.getBoolean()) {
            getLogger().info("Implementing visual fix procedures.");
            if (!version.equals("1.8")) {
                getLogger().warning("Please note the visual bug fix is intended to resolve " +
                        "a known visual bug during chunk loading for clients on 1.8. Your server is running, "
                        + Bukkit.getVersion() + ", so this feature may not be necessary.");
            }
            this.w_watcher = new WorldWatcher();
            this.p_watcher = new PlayerWatcher();
        }

        fixListener = new BlockGlitchFixListener(this);
        Bukkit.getPluginManager().registerEvents(fixListener, this);

        new BlockUpdateChecker(this).runTaskTimer(this, 5L, ConfigData.BLOCK_UPDATE_INTERVAL.getInt());

        API = BlockGlitchFixAPI.init(this);// initialize the API with the main class instance
        getLogger().info("BlockGlitchFix has been initialized successfully!");
        getLogger().info("Is my performance satisfactory? If you enjoy having me around, leave a review on my spigot page!");
    }

    public void onDisable() {
        HandlerList.unregisterAll(this);
        getLogger().info("BlockGlitchFix has been successfully disabled!");
    }

    /**
     * Responsible for loading config data
     */
    private void initValues() {
        this.data = new BlockGlitchFixData(); // store the instance of BlockGlitchFixData in order to retrieve data from the
        // class later on
        ConfigDeserializer.init(this);
    }

    public BlockGlitchFixData getData() {
        return data;
    }
}
