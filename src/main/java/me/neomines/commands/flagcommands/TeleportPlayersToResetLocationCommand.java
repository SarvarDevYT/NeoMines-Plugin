package me.neomines.commands.flagcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TeleportPlayersToResetLocationCommand implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length == 4) {
            if (!(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false"))) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Invalid-Boolean"));
                return true;
            }

            CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);
            cuboidNeoMine.setTeleportPlayersToResetLocation(Boolean.parseBoolean(args[3]));
            cuboidNeoMine.save();
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Flag.Teleport-Players")
                    .replaceAll("%mine%", args[1])
                    .replaceAll("%arg%", args[3]));
        } else {
            sender.sendMessage(NeoMines.PREFIX + "/neomines flag <mine> teleportplayerstoresetlocation true/false");
        }

        return true;
    }
}
