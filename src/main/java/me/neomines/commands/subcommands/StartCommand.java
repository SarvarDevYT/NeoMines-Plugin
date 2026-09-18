package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class StartCommand implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!sender.hasPermission("neomines.start")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length == 2) {
            if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
                return true;
            }

            CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);
            cuboidNeoMine.setStopped(false);
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Start").replaceAll("%mine%", args[1]));
            cuboidNeoMine.save();
        } else {
            sender.sendMessage(NeoMines.PREFIX + "/neomines start <mine>");
        }

        return true;
    }
}
