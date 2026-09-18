package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.gui.menus.MineListMenu;
import me.neomines.gui.menus.MineMenu;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Only-Players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("neomines.gui")) {
            player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length == 1) {
            if (NeoMines.getPlayerMenuUtility(player).getMenu() == null) {
                new MineListMenu(NeoMines.getPlayerMenuUtility(player)).open();
            } else {
                NeoMines.getPlayerMenuUtility(player).getMenu().open();
            }
        } else if (args.length == 2) {
            if (!MineManager.getInstance().getMineListNames().contains(args[1])) {
                player.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Not-Exist"));
                return true;
            }

            new MineMenu(NeoMines.getPlayerMenuUtility(player), MineManager.getInstance().getMine(args[1])).open();
        } else {
            player.sendMessage(NeoMines.PREFIX + "§b/neomines gui (mine)");
        }

        return true;
    }
}
