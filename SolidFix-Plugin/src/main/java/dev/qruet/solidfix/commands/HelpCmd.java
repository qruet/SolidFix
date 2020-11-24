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
 * @author qruet
 * @version 1.9_01
 */
public class HelpCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(T.center(T.C("&8&m------------------------------")));
            player.sendMessage(T.center(T.C("&6&n" +
                    JavaPlugin.getPlugin(SolidFix.class).getDescription().getName() + " v." +
                    JavaPlugin.getPlugin(SolidFix.class).getDescription().getVersion())));
            player.sendMessage(T.center(T.C("&e&ocreated by qruet")));
            player.sendMessage(T.C(" &eresource: " +
                    "&b&ohhttps://www.spigotmc.org/resources/solidfix.54103/"));
            player.sendMessage(T.C(" &esupport: &b&ohttps://discord.com/invite/fx9gm7T"));
            player.sendMessage(T.C(" &edonate: &b&ohttps://www.paypal.com/paypalme/qruet"));
            player.sendMessage(T.C("  &8&m-----&r"));
            player.sendMessage(T.C(" &7&oI work hard to ensure I provide quality support"));
            player.sendMessage(T.C(" &7&ofree of charge for everyone. Consider donating?"));
            player.sendMessage(T.C("  &8&m-----&r"));
            player.sendMessage(T.C(" &6Description: "));
            player.sendMessage(T.C("  &7A free easy-to-use tool that removes the nuisance"));
            player.sendMessage(T.C("  &7of ghost blocks - removing interruptions while"));
            player.sendMessage(T.C("  &7quick mining."));
            player.sendMessage(T.C("  &8&m-----&r"));
            player.sendMessage(T.C(" &6Commands: "));
            player.sendMessage(T.C("  &e* &8/&7reload &8- &6Reloads the plugin's config"));
            player.sendMessage(T.C("       &6- &c&osolidfix.admin.reload"));
            player.sendMessage(T.C("  &e* &8/&7help &8- &6Provides information on this plugin"));
            player.sendMessage(T.center(T.C("&8&m------------------------------")));
            return true;
        }
        return false;
    }

}
