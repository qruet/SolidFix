package dev.qruet.solidfix.commands;

import dev.qruet.solidfix.config.ConfigData;
import dev.qruet.solidfix.config.ConfigDeserializer;
import dev.qruet.solidfix.utils.text.T;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author qruet
 * @version 1.9_01
 */
public class ReloadCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("solidfix.admin.reload"))
                return false;
            ConfigData[] o_data = ConfigData.values();
            ConfigDeserializer.reload();
            ConfigData[] n_data = ConfigData.values();
            for (int i = 0; i < o_data.length; i++) {
                player.sendMessage(T.C("&e* &6updated " + n_data[i].getPath() + " &c&o" +
                        o_data[i].get() + " -> " + n_data[i].get()));
            }
            player.sendMessage(T.C("&aSuccessfully reloaded configuration."));
        }
        return false;
    }
}
