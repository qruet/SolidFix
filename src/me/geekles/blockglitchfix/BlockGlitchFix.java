package me.geekles.blockglitchfix;

import java.io.File;

import me.geekles.blockglitchfix.config.ConfigData;
import me.geekles.blockglitchfix.config.ConfigLoader;
import me.geekles.blockglitchfix.events.BlockGlitchFixListener;
import me.geekles.blockglitchfix.mechanism.GlitchMechanic;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.geekles.blockglitchfix.api.BlockGlitchFixAPI;
import me.geekles.blockglitchfix.runnables.BlockUpdateChecker;

/**
 * Responsible for initializing the numerous parts of the plugin
 *
 * @author geekles
 * @version 1.6.6
 */
public class BlockGlitchFix extends JavaPlugin {

    public static BlockGlitchFixAPI API;
    private BlockGlitchFixData data;

    public void onEnable() {
        // Checks to see if config doesn't already exist
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            getLogger().severe("No Config File Found! Generating a new one...");
            this.saveDefaultConfig(); // Retrieves config.yml stored in plugin
        }

        initValues(); // sets the necessary values from the config

        GlitchMechanic.init(this);

        Bukkit.getPluginManager().registerEvents(new BlockGlitchFixListener(this), this);

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
