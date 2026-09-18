package me.neomines.commands.commandhandler;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.commands.flagcommands.*;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FlagCommandsHandler implements CommandInterface {

    private static final HashMap<String, CommandInterface> flagCommands = new HashMap<>();

    public FlagCommandsHandler() {
        flagCommands.put("replacemode", new ReplaceModeCommand());
        flagCommands.put("moveregion", new MoveRegionCommand());
        flagCommands.put("rename", new RenameCommand());
        flagCommands.put("teleportplayers", new TeleportPlayersCommand());
        flagCommands.put("teleportplayerstoresetlocation", new TeleportPlayersToResetLocationCommand());
        flagCommands.put("warnhotbar", new WarnHotbarCommand());
        flagCommands.put("warnhotbarmessage", new WarnHotbarMessageCommand());
        flagCommands.put("warn", new WarnCommand());
        flagCommands.put("warnglobal", new WarnGlobalCommand());
        flagCommands.put("warnmessage", new WarnMessageCommand());
        flagCommands.put("warnresetmessage", new WarnResetMessageCommand());
        flagCommands.put("warnseconds", new WarnSecondsCommand());
        flagCommands.put("warndistance", new WarnDistanceCommand());
    }

    public static List<String> getFlagCommandNames() {
        return new ArrayList<>(flagCommands.keySet());
    }

    public boolean exists(String name) {
        return flagCommands.containsKey(name);
    }

    public CommandInterface getExecutor(String name) {
        return flagCommands.get(name);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("neomines.flag")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length == 1) {
            sender.sendMessage(NeoMines.PREFIX + "Barcha mavjud flaglar: \n§b" + FlagCommandsHandler.getFlagCommandNames());
            return true;
        } else if (args.length == 2) {
            sender.sendMessage(NeoMines.PREFIX + "§b/neomines flag <mine> <flag>");
            return true;
        }

        if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist").replaceAll("%mine%", args[1]));
            return true;
        }

        if (exists(args[2].toLowerCase())) {
            getExecutor(args[2].toLowerCase()).onCommand(sender, command, label, args);
        } else {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Unknown-Flag"));
        }

        return false;
    }
}
