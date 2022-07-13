package dev.qruet.solidfix.commands;

import dev.qruet.solidfix.SolidFix;
import dev.qruet.solidfix.utils.text.T;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Command responsible for providing helpful information on the plugin
 *
 * @author qruet
 * @version 2.0
 */
public class HelpCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        JavaPlugin plugin = JavaPlugin.getPlugin(SolidFix.class);

        sender.sendMessage(T.center(T.C("&8&m------------------------------")));
        sender.sendMessage(T.center(T.C("&6&n" +
                plugin.getDescription().getName() + " v." +
                plugin.getDescription().getVersion())));
        sender.sendMessage(T.center(T.C("&e&ocreated by qruet")));
        sender.sendMessage(T.C(" &eresource: " +
                "&b&ohttps://www.spigotmc.org/resources/solidfix.54103/"));
        sender.sendMessage(T.C(" &esupport: &b&ohttps://discord.com/invite/fx9gm7T"));
        sender.sendMessage(T.C(" &edonate: &b&ohttps://www.paypal.com/paypalme/qruet"));
        sender.sendMessage(T.C("  &8&m-----&r"));
        sender.sendMessage(T.C(" &7&oI work hard to ensure I provide quality support"));
        sender.sendMessage(T.C(" &7&ofree of charge for everyone. Consider donating?"));
        sender.sendMessage(T.C("  &8&m-----&r"));
        sender.sendMessage(T.C(" &6Description: "));
        sender.sendMessage(T.C("  &7" + plugin.getDescription().getDescription().replaceAll("(?<=[.!?\\\\-]).", "\n")));
        sender.sendMessage(T.C("  &8&m-----&r"));
        sender.sendMessage(T.C(" &6Commands: "));
        sender.sendMessage(T.C("  &e* &8/&7reload &8- &6Reloads the plugin's config"));
        sender.sendMessage(T.C("       &6- &c&osolidfix.admin.reload"));
        sender.sendMessage(T.C("  &e* &8/&7help &8- &6Provides information on this plugin"));
        sender.sendMessage(T.center(T.C("&8&m------------------------------")));
        return true;
    }

}
