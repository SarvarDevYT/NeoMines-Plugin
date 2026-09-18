package me.neomines.commands.flagcommands;

import me.neomines.NeoMines;
import me.neomines.commands.CommandInterface;
import me.neomines.mine.CuboidNeoMine;
import me.neomines.schedulers.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

public class RenameCommand implements CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length == 4) {
            CuboidNeoMine cuboidNeoMine = MineManager.getInstance().getMine(args[1]);
            if (MineManager.getInstance().getMineListNames().contains(args[3])) {
                sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Error-Messages.Mine.Exist"));
                return true;
            }

            File file = new File(NeoMines.getInstance().getDataFolder() + "/mines/" + cuboidNeoMine.getName() + ".yml");
            cuboidNeoMine.setName(args[3]);
            File newFile = new File(NeoMines.getInstance().getDataFolder() + "/mines/" + cuboidNeoMine.getName() + ".yml");
            file.renameTo(newFile);
            cuboidNeoMine.resetFiles();
            cuboidNeoMine.save();
            sender.sendMessage(NeoMines.PREFIX + NeoMines.getInstance().getLangString("Commands.Flag.Rename")
                    .replaceAll("%oldmine%", args[1])
                    .replaceAll("%newmine%", args[3]));
        } else {
            sender.sendMessage(NeoMines.PREFIX + "§b/neomines flag <mine> rename [name]");
        }

        return true;
    }
}
