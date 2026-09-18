package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SetDelayCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!sender.hasPermission("neomines.setdelay")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length == 3) {
            if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
                return true;
            }

            CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);
            int resetDelay;
            try {
                resetDelay = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Number"));
                return true;
            }

            if (resetDelay > 1000000) {
                sender.sendMessage(NeoMines.PREFIX + "Reset vaqti §c1000000 §7soniyadan kam bo'lishi kerak.");
                return true;
            }

            cuboidNeoMine.setResetDelay(resetDelay);
            cuboidNeoMine.setCountdown(resetDelay);
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Delay")
                    .replaceAll("%mine%", args[1])
                    .replaceAll("%seconds%", args[2]));
            cuboidNeoMine.save();
        } else {
            sender.sendMessage(NeoMines.PREFIX + "/neomines setdelay <mine> [seconds]");
        }

        return true;
    }
}
