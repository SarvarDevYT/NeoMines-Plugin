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

public class CreateCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(NeoMines.PREFIX + "Only players may execute this command!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("neomines.create")) {
            player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length == 2) {

            if (MineManager.getInstance().getMineListNames().contains(args[1])) {
                player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Exist"));
                return true;
            }

            Region selection = Utils.getWorldEditSelectionOfPlayer(player);
            if (selection != null) {
                CuboidNeoMine cuboidNeoMine = new CuboidNeoMine(args[1], selection.clone());
                MineManager.getInstance().getMines().add(cuboidNeoMine);
                cuboidNeoMine.save();
                player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Create").replaceAll("%mine%", args[1]));
            }
        } else {
            player.sendMessage(NeoMines.PREFIX + "/neomines create <mine>");
        }
        return true;
    }
}
