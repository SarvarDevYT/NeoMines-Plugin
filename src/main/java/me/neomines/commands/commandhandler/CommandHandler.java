package me.neomines.commands.commandhandler;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Set;

public class CommandHandler implements CommandExecutor {

    private static final HashMap<String, CommandInterface> commands = new HashMap<>();

    public static Set<String> getCommandNames() {
        return commands.keySet();
    }

    public void register(String name, CommandInterface cmd) {
        commands.put(name.toLowerCase(), cmd);
    }

    public boolean exists(String name) {
        return commands.containsKey(name.toLowerCase());
    }

    public CommandInterface getExecutor(String name) {
        return commands.get(name.toLowerCase());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            CommandInterface helpCmd = getExecutor("help");
            if (helpCmd != null) {
                helpCmd.onCommand(sender, command, label, args);
            }
            return true;
        }

        String sub = args[0].toLowerCase();
        if (exists(sub)) {
            getExecutor(sub).onCommand(sender, command, label, args);
            Utils.updateMenus();
        } else {
            if (!sender.hasPermission("neomines.*")) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
                return true;
            }
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Unknown-Command"));
        }
        return true;
    }
}
