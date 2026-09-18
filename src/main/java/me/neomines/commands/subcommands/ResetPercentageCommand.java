package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ResetPercentageCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!sender.hasPermission("neomines.resetpercentage")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(NeoMines.PREFIX + "/neomines resetpercentage <mine> <percentage>");
            return true;
        }

        if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
            return true;
        }

        String percentArg = args[2];
        if (percentArg.endsWith("%")) {
            percentArg = percentArg.substring(0, percentArg.length() - 1);
        }

        double resetPercentInput;
        try {
            resetPercentInput = Double.parseDouble(percentArg);
        } catch (NumberFormatException e) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Not-Number"));
            return true;
        }

        if (resetPercentInput < 0 || resetPercentInput > 100) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Invalid-Range")
                    .replaceAll("%start%", "0.0")
                    .replaceAll("%end%", "100.0"));
            return true;
        }

        CuboidNeoMine mine = MineManager.getInstance().getMine(args[1]);
        mine.setResetPercentage(resetPercentInput);

        sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Reset-Percentage")
                .replaceAll("%mine%", mine.getName())
                .replaceAll("%percentage%", String.valueOf(resetPercentInput)));

        mine.save();
        return true;
    }
}
