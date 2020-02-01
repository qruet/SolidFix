package me.geekles.blockglitchfix;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import me.geekles.blockglitchfix.config.ConfigData;
import me.geekles.blockglitchfix.config.ConfigLoader;
import me.geekles.blockglitchfix.events.BlockGlitchFixListener;
import me.geekles.blockglitchfix.mechanism.GlitchMechanic;
import org.bukkit.Bukkit;
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

        fixListener = new BlockGlitchFixListener(this);
        Bukkit.getPluginManager().registerEvents(fixListener, this);

        new BlockUpdateChecker(this).runTaskTimer(this, 5L, ConfigData.BLOCK_UPDATE_INTERVAL.get());

        API = BlockGlitchFixAPI.init(this);// initialize the API with the main class instance
        getLogger().info("BlockGlitchFix has been initialized successfully! \\(^w^)/");
        getLogger().info("Is my performance satisfactory? If you enjoy having me around, leave a review on my spigot page! <|⚬_⚬|>");
    }

    public void onDisable() {
        getLogger().info("BlockGlitchFix has been successfully disabled!");
    }

    /**
     * Responsible for loading config data
     */
    private void initValues() {
        this.data = new BlockGlitchFixData(); // store the instance of BlockGlitchFixData in order to retrieve data from the
        // class later on
        ConfigLoader.init(this);
    }

    public BlockGlitchFixData getData() {
        return data;
    }
}
