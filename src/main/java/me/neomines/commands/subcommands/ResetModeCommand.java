package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.mine.components.NeoMineResetMode;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ResetModeCommand implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!sender.hasPermission("neomines.resetmode")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(NeoMines.PREFIX + "/neomines resetmode <mine> <mode>");
            return true;
        }

        if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
            return true;
        }

        String modeStr = args[2].toUpperCase();
        try {
            NeoMineResetMode mode = NeoMineResetMode.valueOf(modeStr);
            CuboidNeoMine mine = MineManager.getInstance().getMine(args[1]);
            mine.setResetMode(mode);

            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Reset-Mode")
                    .replaceAll("%mine%", mine.getName())
                    .replaceAll("%mode%", mode.name()));

            mine.save();
        } catch (IllegalArgumentException e) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Invalid-Reset-Mode"));
        }

        return true;
    }
}
