package me.neomines.commands.subcommands;

import com.sk89q.worldedit.regions.Region;
import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import me.neomines.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RedefineCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Only-Players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("neomines.redefine")) {
            player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length == 2) {
            if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
                player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
                return true;
            }

            Region selection = Utils.getWorldEditSelectionOfPlayer(player);
            if (selection != null) {
                CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);
                cuboidNeoMine.setRegion(selection.clone());
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Flag.Move-Region").replaceAll("%mine%", args[1]));
                cuboidNeoMine.save();
            }
        } else {
            player.sendMessage(NeoMines.PREFIX + "/neomines redefine <mine>");
        }

        return true;
    }
}
