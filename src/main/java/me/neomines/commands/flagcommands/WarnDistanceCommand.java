package me.neomines.commands.flagcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WarnDistanceCommand implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length == 4) {
            CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);
            int warnDistance;
            try {
                warnDistance = Integer.parseInt(args[3]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(NeoMines.PREFIX + "Masofa butun son (§cInteger§7) bo'lishi kerak.");
                return true;
            }

            cuboidNeoMine.setWarnDistance(warnDistance);
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Flag.Warn.Warn-Distance")
                    .replaceAll("%mine%", args[1])
                    .replaceAll("%arg%", args[3]));
            cuboidNeoMine.save();
        } else {
            sender.sendMessage(NeoMines.PREFIX + "§b/neomines flag <mine> warndistance [distance]");
        }

        return true;
    }
}
