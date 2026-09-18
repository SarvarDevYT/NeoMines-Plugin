package me.neomines.commands.flagcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WarnGlobalCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length == 4) {
            CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);

            if (!(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false"))) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Invalid-Boolean"));
                return true;
            }

            cuboidNeoMine.setWarnGlobal(Boolean.parseBoolean(args[3]));
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Flag.Warn.Warn-Global")
                    .replaceAll("%mine%", args[1])
                    .replaceAll("%arg%", args[3]));
            cuboidNeoMine.save();
        } else {
            sender.sendMessage(NeoMines.PREFIX + "§b/neomines flag <mine> warnglobal §ctrue/false");
        }

        return true;
    }
}
