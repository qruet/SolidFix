package dev.qruet.solidfix.commands;

import dev.qruet.solidfix.CoreManager;
import dev.qruet.solidfix.SolidFix;
import dev.qruet.solidfix.SolidManager;
import dev.qruet.solidfix.utils.text.T;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * Registers a subclass as a the command executor for SolidFix and handles any
 * incoming arguments associated with the /solidfix alias
 *
 * @author qruet
 * @version 1.9_01
 */
public class CommandManager extends SolidManager {

    private static HashMap<String, CommandExecutor> COMMANDS = new HashMap<String, CommandExecutor>() {{
        put("help", new HelpCmd());
        put("reload", new ReloadCmd());
    }};

    public CommandManager(CoreManager.Registrar registrar) {
        super(registrar);
    }

    /**
     * Registers CommandManager.SolidFixExecutor.class as the command executor
     * @return
     */
    @Override
    public boolean init() {
        PluginCommand rel_cmd = JavaPlugin.getPlugin(SolidFix.class).getCommand("solidfix");
        rel_cmd.setExecutor(new SolidFixExecutor());
        return true;
    }

    @Override
    public boolean disable() {
        return true;
    }

    /**
     * Handles any incoming arguments associated with the /solidfix alias
     */
    private class SolidFixExecutor implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 0) {
                COMMANDS.get("help").onCommand(sender, command, alias, args);
                return true;
            }

            CommandExecutor exec = COMMANDS.get(args[0]);
            if (exec != null) {
                exec.onCommand(sender, command, alias, args);
                return true;
            }

            sender.sendMessage(T.C("&cThat command does not exist. Did you mean &o/solidfix help&c?"));
            return false;
        }
    }
}
