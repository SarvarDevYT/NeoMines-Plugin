package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ListCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (!sender.hasPermission("neomines.list")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length == 1) {
            List<String> mineList = MineManager.getInstance().getMineListNames();
            if (mineList.isEmpty()) {
                sender.sendMessage(NeoMines.PREFIX + "Ro'yxatdan o'tgan shaxtalar mavjud emas.");
                return true;
            }
            String str = mineList.toString().replaceAll(",", "§b,§6");
            sender.sendMessage(NeoMines.PREFIX + "Ro'yxatdan o'tgan barcha shaxtalar:\n§6" + str.substring(1, str.length() - 1));
        } else {
            sender.sendMessage(NeoMines.PREFIX + "/neomines list");
        }
        return true;
    }
}
