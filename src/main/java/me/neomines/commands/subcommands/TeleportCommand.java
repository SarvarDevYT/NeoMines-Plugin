package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length < 2 || args.length > 3) {
            sender.sendMessage(NeoMines.PREFIX + "§b/neomines tp <mine> (player)");
            return true;
        }

        if (!sender.hasPermission("neomines.teleport") && !sender.hasPermission("neomines.teleport." + args[1])) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
            return true;
        }

        CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);
        if (cuboidNeoMine.getTeleportLocation() == null) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Teleport-Error"));
            return true;
        }

        if (args.length == 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Only-Players"));
                return true;
            }
            Player player = (Player) sender;
            player.teleport(cuboidNeoMine.getTeleportLocation());
            player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Teleport.Self").replaceAll("%mine%", args[1]));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Player-Not-Found").replaceAll("%player%", args[2]));
            return true;
        }

        target.teleport(cuboidNeoMine.getTeleportLocation());
        sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Teleport.Other")
                .replaceAll("%mine%", args[1])
                .replaceAll("%player%", args[2]));
        return true;
    }
}
