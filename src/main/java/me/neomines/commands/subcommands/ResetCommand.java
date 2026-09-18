package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ResetCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 2) {
            if (!sender.hasPermission("neomines.reset") && !sender.hasPermission("neomines.reset." + args[1])) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
                return true;
            }

            if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
                return true;
            }

            CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);

            if (cuboidNeoMine.getRegion() == null || cuboidNeoMine.getRandomPattern() == null) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Cant-Reset").replaceAll("%mine%", args[1]));
                return true;
            }

            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Reset"));
            cuboidNeoMine.forceReset();
        } else {
            sender.sendMessage(NeoMines.PREFIX + "§b/neomines reset <mine>");
        }

        return true;
    }
}
