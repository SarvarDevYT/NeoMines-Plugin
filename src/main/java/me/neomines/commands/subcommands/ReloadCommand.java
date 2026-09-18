package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!sender.hasPermission("neomines.reload")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length == 1) {
            NeoMines.getPlayerMenuUtilityMap().clear();
            NeoMines.getInstance().getFileManager().setupFiles();
            MineManager.getInstance().reloadMines();
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Reload.All"));
            return true;
        }

        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("mines")) {
                MineManager.getInstance().reloadMines();
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Reload.Mines"));
                return true;
            }

            if (args[1].equalsIgnoreCase("properties")) {
                NeoMines.getInstance().getFileManager().setupCustomFiles();
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Reload.Properties"));
                return true;
            }

            if (args[1].equalsIgnoreCase("config")) {
                NeoMines.getInstance().getFileManager().setupConfig();
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Reload.Config"));
                return true;
            }

            if (args[1].equalsIgnoreCase("messages")) {
                NeoMines.getInstance().reloadLanguages();
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Reload.Messages"));
                return true;
            }

            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Unknown-Command"));

        } else {
            sender.sendMessage(NeoMines.PREFIX + "/neomines reload (arg)");
        }

        return true;
    }
}
