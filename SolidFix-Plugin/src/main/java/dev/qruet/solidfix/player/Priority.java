package dev.qruet.solidfix.player;

import dev.qruet.solidfix.config.ConfigData;

public enum Priority {

    HIGH, MEDIUM, LOW;


    Priority() {
    }

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
     * @return
     */
    public static Priority getPriority(int ping) {
        if (ConfigData.LOW_PRIORITY_MINIMUM.getInt() >= ping)
            return Priority.LOW;
        else if (ConfigData.MEDIUM_PRIORITY_MINIMUM.getInt() >= ping)
            return Priority.MEDIUM;
        return Priority.HIGH;
    }

}
