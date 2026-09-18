package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.mine.components.NeoMineBlock;
import me.neomines.schedulers.MineManager;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SetCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!sender.hasPermission("neomines.set")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (!(args.length == 3 || args.length == 4)) {
            sender.sendMessage(NeoMines.PREFIX + "/neomines set <mine> [block] n%, for example: §8/neomines set Stone_Mine stone 50%");
            return true;
        }

        if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
            return true;
        }

        BlockData blockData;
        try {
            blockData = Bukkit.createBlockData(args[2].toLowerCase());
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(NeoMines.PREFIX + "§cNoto'g'ri material yoki blok: §e" + args[2]);
            return true;
        }

        CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);

        if (args.length == 3) {
            try {
                cuboidNeoMine.addBlock(new NeoMineBlock(blockData, 100 - cuboidNeoMine.getCompositionChance()));
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Set.Error")
                        .replaceAll("%chance-over-100%", ex.getMessage())
                        .replaceAll("%remainingChance%", String.valueOf(100 - cuboidNeoMine.getCompositionChance())));
                return true;
            }

            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Set.Success")
                    .replaceAll("%material%", blockData.getAsString(true))
                    .replaceAll("%mine%", args[1])
                    .replaceAll("%remainingChance%", String.valueOf(100 - cuboidNeoMine.getCompositionChance())));
            cuboidNeoMine.save();
            return true;
        } else {
            if (args[3].endsWith("%")) {
                double percentInput;
                try {
                    percentInput = Double.parseDouble(args[3].substring(0, args[3].length() - 1));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Not-Number"));
                    return true;
                }

                if (percentInput < 0 || percentInput > 100) {
                    sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Invalid-Chance"));
                    return true;
                }

                try {
                    cuboidNeoMine.addBlock(new NeoMineBlock(blockData, percentInput));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Set.Error")
                            .replaceAll("%chance-over-100%", ex.getMessage())
                            .replaceAll("%remainingChance%", String.valueOf(100 - cuboidNeoMine.getCompositionChance())));
                    return true;
                }

                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Set.Success")
                        .replaceAll("%material%", blockData.getAsString(true))
                        .replaceAll("%mine%", args[1])
                        .replaceAll("%remainingChance%", String.valueOf(100 - cuboidNeoMine.getCompositionChance())));
                cuboidNeoMine.save();
                return true;
            } else {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Not-Percentage"));
            }
        }

        return false;
    }
}
