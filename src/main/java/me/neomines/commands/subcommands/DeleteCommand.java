package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import me.neomines.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

public class DeleteCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!sender.hasPermission("neomines.delete")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length == 2) {
            if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
                return true;
            }

            CuboidNeoMine cuboidNeoMineToDelete = MineManager.getInstance().getMine(args[1]);
            MineManager.getInstance().getMines().removeIf(mine -> mine.getName().equals(args[1]));
            File file = new File(NeoMines.getInstance().getDataFolder() + "/mines/" + cuboidNeoMineToDelete.getName() + ".yml");
            file.delete();

            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Delete").replaceAll("%mine%", args[1]));

            Utils.updateMenus();

        } else {
            sender.sendMessage(NeoMines.PREFIX + "/neomines delete <mine>");
        }

        return true;
    }
}
