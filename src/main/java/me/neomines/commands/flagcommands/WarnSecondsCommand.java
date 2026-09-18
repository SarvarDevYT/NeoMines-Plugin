package me.neomines.commands.flagcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class WarnSecondsCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length >= 4) {
            CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);

            List<Integer> integers = new ArrayList<>();
            for (int i = 3; i < args.length; i++) {
                try {
                    integers.add(Integer.parseInt(args[i]));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(NeoMines.PREFIX + "Kiritilgan argumentlardan biri butun son (§cInteger§7) emas.");
                    return true;
                }
            }

            cuboidNeoMine.setWarnSeconds(integers);
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Flag.Warn.Warn-Seconds").replaceAll("%mine%", args[1]));
            cuboidNeoMine.save();
        } else {
            sender.sendMessage(NeoMines.PREFIX + "Masalan: §b/neomines flag <mine> warnseconds 1 2 3 10 20 60");
        }

        return true;
    }
}
