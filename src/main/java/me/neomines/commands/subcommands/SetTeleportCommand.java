package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTeleportCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Only-Players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("neomines.setteleport")) {
            player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(NeoMines.PREFIX + "§b/neomines settp <mine>");
            return true;
        }

        if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
            player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
            return true;
        }

        CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);
        cuboidNeoMine.setTeleportLocation(player.getLocation());
        cuboidNeoMine.save();
        player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Set-Teleport").replaceAll("%mine%", args[1]));

        return true;
    }
}
