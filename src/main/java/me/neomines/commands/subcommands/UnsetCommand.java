package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UnsetCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!sender.hasPermission("neomines.unset")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (!(args.length == 2 || args.length == 3)) {
            sender.sendMessage(NeoMines.PREFIX + "/neomines unset <mine> (block)");
            return true;
        }

        if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
            return true;
        }

        CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);

        if (args.length == 2) {
            cuboidNeoMine.clearComposition();
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Unset.Clear")
                    .replaceAll("%mine%", args[1]));
        } else {
            BlockData blockData;
            try {
                blockData = Bukkit.createBlockData(args[2].toLowerCase());
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(NeoMines.PREFIX + "§cNoto'g'ri material yoki blok: §e" + args[2]);
                return true;
            }

            if (cuboidNeoMine.containsBlockData(blockData)) {
                cuboidNeoMine.removeBlock(blockData);
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Unset.Remove.Success")
                        .replaceAll("%material%", blockData.getAsString())
                        .replaceAll("%mine%", args[1])
                        .replaceAll("%remainingChance%", String.valueOf(100 - cuboidNeoMine.getCompositionChance())));
            } else {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Unset.Remove.Error")
                        .replaceAll("%material%", blockData.getAsString())
                        .replaceAll("%mine%", args[1])
                        .replaceAll("%remainingChance%", String.valueOf(100 - cuboidNeoMine.getCompositionChance())));
                return true;
            }
        }

        cuboidNeoMine.save();
        return true;
    }
}
