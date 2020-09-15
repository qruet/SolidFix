package dev.qruet.solidfix.utils.text;

import org.bukkit.ChatColor;

/**
 * String-based color utility
 * @author qruet
 * @version 1.9_01
 */
public class T {

    public static String C(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public static String center(String message) {
        return MessageManager.getCenteredMessage(message);
    }
}
