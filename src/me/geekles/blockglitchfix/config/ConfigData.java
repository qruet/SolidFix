package me.geekles.blockglitchfix.config;

/**
 * @author geekles
 * @version 1.7
 */
public enum ConfigData {

    BLOCK_BREAK_SENSITIVITY_COOLDOWN("Block Break Sensitivity Cooldown", Integer.class, 50),

    BLOCK_UPDATE_REMOVAL_COOLDOWN("Block Updates Removal Cooldown", Integer.class, 2000),

    BLOCK_UPDATE_INTERVAL("Block Update Interval", Integer.class, 5),
    //TODO Dynamic Option (TPS Dependent)
    RADIUS("Radius", Integer.class, 4),

    VISUAL_FIX("Chunk Visual Bug Fix", Boolean.class, false);

    private final String path;
    private final Class<?> type;
    private int i_data;
    private boolean b_data;

    ConfigData(String path, Class<?> type, boolean data) {
        this.path = path;
        this.b_data = data;
        this.type = type;
    }

    ConfigData(String path, Class<?> type, int data) {
        this.path = path;
        this.i_data = data;
        this.type = type;
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
    public void setInt(int val) {
        this.i_data = val;
    }

    public void setBoolean(boolean val) {
        this.b_data = val;
    }

    /**
     * Retrieve the field's value
     *
     * @return currently set value
     */
    public int getInt() {
        return i_data;
    }

    public boolean getBoolean() {
        return b_data;
    }

    public Class<?> getType() {
        return type;
    }
}
