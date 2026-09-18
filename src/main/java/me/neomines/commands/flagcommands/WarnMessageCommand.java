package me.neomines.commands.flagcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WarnMessageCommand implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length >= 4) {
            CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);

            StringBuilder sb = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }

            String warnMessage = sb.toString().replace('"', ' ').trim();
            cuboidNeoMine.setWarnMessage(warnMessage);
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Flag.Warn.Warn-Message").replaceAll("%mine%", args[1]));
            cuboidNeoMine.save();
        } else {
            sender.sendMessage(NeoMines.PREFIX + "§b/neomines flag <mine> warnmessage \"message\"");
        }

        return true;
    }
}
