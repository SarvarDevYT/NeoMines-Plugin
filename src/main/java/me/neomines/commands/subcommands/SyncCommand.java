package me.neomines.commands.subcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SyncCommand implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!sender.hasPermission("neomines.sync")) {
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.No-Permission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(NeoMines.PREFIX + "§b/neomines sync");
            return true;
        }

        MineManager.getInstance().getMines().forEach(cuboidNeoMine -> cuboidNeoMine.setCountdown(0));
        sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Sync"));
        return true;
    }
}
