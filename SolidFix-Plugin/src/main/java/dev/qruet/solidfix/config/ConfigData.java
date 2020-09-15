package dev.qruet.solidfix.config;

/**
 * Dictionary w/ identifications for fields existing within the config
 *
 * @author qruet
 * @version 1.9_01
 */
public enum ConfigData {

    OVERRIDE_VERSION_CHECK("Override Version Check", Boolean.class, false),

    LOW_PRIORITY_MINIMUM("Priorities.LOW.Max Ping", Integer.class, 150),

    MEDIUM_PRIORITY_MINIMUM("Priorities.MEDIUM.Max Ping", Integer.class, 300),

    LOW_SENSITIVITY("Block Break Sensitivity.LOW", Integer.class, 150),

    MEDIUM_SENSITIVITY("Block Break Sensitivity.MEDIUM", Integer.class, 300),

    HIGH_SENSITIVITY("Block Break Sensitivity.HIGH", Integer.class, 650),

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

    public Object get() {
        if (type == Integer.class)
            return i_data;
        else
            return b_data;
    }

    public Class<?> getType() {
        return type;
    }
}
