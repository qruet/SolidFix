package me.geekles.blockglitchfix.config;

/**
 * @author geekles
 * @version 1.7
 */
public enum ConfigData {

    BLOCK_BREAK_SENSITIVITY_COOLDOWN("BlockBreakSensitivityCooldown", 50),

    BLOCK_UPDATE_REMOVAL_COOLDOWN("BlockUpdatesRemovalCooldown", 2000),

    BLOCK_UPDATE_INTERVAL("BlockUpdateInterval", 5),
    //TODO Dynamic Option (TPS Dependent)
    RADIUS("Radius", 4);

    private final String path;
    private int data;

    ConfigData(String path, int data) {
        this.path = path;
        this.data = data;
    }

    /**
     * Retrieve fields config path
     *
     * @return Config path
     */
    public String getPath() {
        return path;
    }

    /**
     * Update the field's value
     *
     * @param val updated value
     */
    public void set(int val) {
        this.data = val;
    }

    /**
     * Retrieve the field's value
     *
     * @return currently set value
     */
    public int get() {
        return data;
    }
}
