package dev.qruet.solidfix.player;

import dev.qruet.solidfix.config.ConfigData;

/**
 * Enum that designates player priority
 * @author qruet
 * @version 2.0_02
 */
public enum Priority {

    HIGH, MEDIUM, LOW;


    Priority() {
    }

    /**
     * Retrieves associated block break sensitivity with priority
     * @return sensitivity
     */
    public int getSensitivity() {
        switch (this) {
            case LOW:
                return ConfigData.LOW_SENSITIVITY.getInt();
            case MEDIUM:
                return ConfigData.MEDIUM_SENSITIVITY.getInt();
            case HIGH:
                return ConfigData.HIGH_SENSITIVITY.getInt();
        }
        return ConfigData.HIGH_SENSITIVITY.getInt();
    }

    /**
     * Provides appropriate associated priority for designated ping rate
     *
     * @param ping Player ping
     * @return Priority type
     */
    public static Priority getPriority(int ping) {
        if (ConfigData.LOW_PRIORITY_MINIMUM.getInt() >= ping)
            return Priority.LOW;
        else if (ConfigData.MEDIUM_PRIORITY_MINIMUM.getInt() >= ping)
            return Priority.MEDIUM;
        return Priority.HIGH;
    }

}
