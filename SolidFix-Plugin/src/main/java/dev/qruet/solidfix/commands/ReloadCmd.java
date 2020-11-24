package dev.qruet.solidfix.commands;

import dev.qruet.solidfix.config.ConfigData;
import dev.qruet.solidfix.config.ConfigDeserializer;
import dev.qruet.solidfix.utils.text.T;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Command responsible for reloading the plugin's config
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

            long start = System.currentTimeMillis();
            LinkedList<Object> o_data = new LinkedList<>();
            Arrays.stream(ConfigData.values()).forEach(d -> o_data.add(d.get()));
            ConfigDeserializer.reload();
            ConfigData[] n_data = ConfigData.values();
            long end = System.currentTimeMillis();
            for (int i = 0; i < n_data.length; i++) {
                sender.sendMessage(T.C(" &e* &6updated " + n_data[i].getPath() + " &c&o") +
                        o_data.get(i) + " -> " + n_data[i].get());
            }
            player.sendMessage(T.C("&aSuccessfully reloaded configuration in " + (end-start) + "ms!"));
        }
        return false;
    }
}
